/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.hybris.platform.azure.dtu.DatabaseUtilization;
import de.hybris.platform.azure.dtu.DatabaseUtilizationService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.threadregistry.OperationInfo;
import de.hybris.platform.core.threadregistry.RevertibleUpdate;
import de.hybris.platform.util.config.ConfigIntf;

/**
 * Buffered implementation of {@link DatabaseUtilizationService}, used for retrieving information about database utilization.
 * Information about database utilization is cached when is requested.
 * <p>
 * It supports below cache configuration settings:
 * <ul>
 * <li>database.utilization.query.interval.seconds</li>
 * <li>database.utilization.buffer.size.seconds - allows to specify how long DatabaseUtilization objects are available to retrieve</li>
 * </ul>
 */
public class BufferedDatabaseUtilizationService implements DatabaseUtilizationService
{
	static final String DATABASE_UTILIZATION_QUERY_INTERVAL = "database.utilization.query.interval.seconds";
	static final String DATABASE_UTILIZATION_BUFFER_SIZE = "database.utilization.buffer.size.seconds";
	static final Comparator<DatabaseUtilization> COMPARE_BY_OBSERVATION_TIME = Comparator.comparing(
			DatabaseUtilization::getObservationTime);

	private static final Logger LOG = LoggerFactory.getLogger(BufferedDatabaseUtilizationService.class);
	private static final Duration DEFAULT_TIME_TO_NOT_PROCESS_NEW_DATA = Duration.ofSeconds(15);
	private static final Duration DEFAULT_BUFFER_SIZE = Duration.ofHours(1);

	final DatabaseUtilizationService origin;
	private final AtomicReference<DatabaseUtilizationBuffer> buffer = new AtomicReference<>(new DatabaseUtilizationBuffer());
	private Duration refreshInterval;
	private Duration bufferSize;

	public BufferedDatabaseUtilizationService(final DatabaseUtilizationService origin)
	{
		this(origin, Registry.getCurrentTenant().getConfig());
	}
	
	BufferedDatabaseUtilizationService(final DatabaseUtilizationService origin, final ConfigIntf config)
	{
		this.origin = Objects.requireNonNull(origin);
		this.bufferSize = Duration.ofSeconds(config.getLong(DATABASE_UTILIZATION_BUFFER_SIZE, DEFAULT_BUFFER_SIZE.getSeconds()));
		this.refreshInterval = Duration.ofSeconds(
				config.getLong(DATABASE_UTILIZATION_QUERY_INTERVAL, DEFAULT_TIME_TO_NOT_PROCESS_NEW_DATA.getSeconds()));
	}

	private static DatabaseUtilization maxTime(final Collection<DatabaseUtilization> stats)
	{
		return Collections.max(stats, COMPARE_BY_OBSERVATION_TIME);
	}

	private static DatabaseUtilization minTime(final Collection<DatabaseUtilization> stats)
	{
		return Collections.min(stats, COMPARE_BY_OBSERVATION_TIME);
	}

	@Override
	public List<DatabaseUtilization> getUtilization(final Duration duration)
	{
		final Instant now = Instant.now();
		final Instant fromDate = now.minus(duration);
		final Collection<DatabaseUtilization> duList = getRefreshedBufferIfNeeded(now).getDataFromLocalCache();

		return duList.stream()
		             .filter(rec -> rec.getObservationTime().isAfter(fromDate))
		             .collect(Collectors.toList());
	}

	@Override
	public boolean isActive()
	{
		return this.origin.isActive();
	}

	@Override
	public String getStatusReason()
	{
		return this.origin.getStatusReason();
	}

	protected DatabaseUtilizationBuffer getRefreshedBufferIfNeeded(final Instant now)
	{
		final DatabaseUtilizationBuffer currentBuffer = buffer.get();
		if (!currentBuffer.isRefreshNeeded(now))
		{
			return currentBuffer;
		}

		final DatabaseUtilizationBuffer lockedBuffer = currentBuffer.copyLocked();
		if (buffer.compareAndSet(currentBuffer, lockedBuffer))
		{
			// in case new buffer could not be created, fall back to current buffer
			DatabaseUtilizationBuffer newBuffer = currentBuffer;
			try
			{
				newBuffer = createNewBuffer(now, currentBuffer).orElse(newBuffer);
			}
			finally
			{
				buffer.set(newBuffer);
			}
		}

		return buffer.get();
	}

	protected Optional<DatabaseUtilizationBuffer> createNewBuffer(final Instant now,
	                                                              final DatabaseUtilizationBuffer currentBuffer)
	{

		try (final RevertibleUpdate ignore = OperationInfo.updateThread(
				OperationInfo.builder().withCategory(OperationInfo.Category.SYSTEM).build()))
		{
			final List<DatabaseUtilization> newData = getData(getDuration(now, currentBuffer.getMax()));
			return Optional.of(currentBuffer.copyUnlockedWithRefreshedData(now, bufferSize, newData, refreshInterval));
		}
		catch (final Exception e)
		{
			LOG.warn("error while getting utilization data", e);
			return Optional.empty();
		}
	}

	private List<DatabaseUtilization> getData(final Duration duration)
	{
		final List<DatabaseUtilization> retrievedData = origin.getUtilization(duration);

		if (LOG.isDebugEnabled() && !retrievedData.isEmpty())
		{
			LOG.debug("Retrieved data from {} to {} size: {}", minTime(retrievedData).getObservationTime(),
					maxTime(retrievedData).getObservationTime(), retrievedData.size());
		}

		return retrievedData;
	}

	private Duration getDuration(final Instant now, final Instant bufferMax)
	{
		return bufferMax.equals(Instant.EPOCH) ? bufferSize : Duration.between(bufferMax, now);
	}

	protected static class DatabaseUtilizationBuffer
	{
		private final Instant bufferedInstant;
		private final List<DatabaseUtilization> values;
		private final Instant validUntil;
		private final boolean locked;

		public DatabaseUtilizationBuffer()
		{
			this(Collections.emptyList(), Instant.EPOCH, Duration.ZERO);
		}

		private DatabaseUtilizationBuffer(final List<DatabaseUtilization> values, final Instant now, final Duration validDuration)
		{
			this(values, now, now.plus(validDuration), false);
		}

		private DatabaseUtilizationBuffer(final List<DatabaseUtilization> values, final Instant bufferedInstant,
		                                  final Instant validUntil, final boolean locked)
		{
			this.bufferedInstant = Objects.requireNonNull(bufferedInstant);
			this.values = ImmutableList.copyOf(Objects.requireNonNull(values));
			this.validUntil = Objects.requireNonNull(validUntil);
			this.locked = locked;
		}

		boolean isRefreshNeeded(final Instant now)
		{
			return !locked && isExpired(now);
		}

		boolean isExpired(final Instant now)
		{
			return validUntil.isBefore(now);
		}

		Instant getMax()
		{
			return bufferedInstant;
		}

		Collection<DatabaseUtilization> getDataFromLocalCache()
		{
			return values;
		}

		/**
		 * creates a copy of this buffer and merges new data. After adding new data old entries (outside of {@code bufferSize} window)
		 * are removed from the buffer.
		 */
		DatabaseUtilizationBuffer copyUnlockedWithRefreshedData(final Instant now, final Duration bufferSize,
		                                                        final List<DatabaseUtilization> retrievedData,
		                                                        final Duration validDuration)
		{
			final List<DatabaseUtilization> currentDUs = new LinkedList<>(retrievedData);
			currentDUs.addAll(values);

			final Instant removeAfterDate = now.minus(bufferSize);
			currentDUs.removeIf(rec -> rec.getObservationTime().isBefore(removeAfterDate));

			return new DatabaseUtilizationBuffer(currentDUs, now, validDuration);
		}

		/**
		 * @return new copy of this {@link DatabaseUtilizationBuffer} instance with locked flag set to {@code true}
		 */
		DatabaseUtilizationBuffer copyLocked()
		{
			return new DatabaseUtilizationBuffer(values, bufferedInstant, validUntil, true);
		}

	}

}
