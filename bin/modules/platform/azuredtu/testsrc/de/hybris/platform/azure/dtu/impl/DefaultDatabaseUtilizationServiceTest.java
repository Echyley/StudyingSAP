/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.azure.dtu.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.azure.dtu.DatabaseUtilization;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;


@IntegrationTest
public class DefaultDatabaseUtilizationServiceTest extends ServicelayerBaseTest
{
	private final BulkPropertyConfigSwitcher bulkPropertyConfigSwitcher = new BulkPropertyConfigSwitcher();


	@After
	public void cleanup()
	{
		bulkPropertyConfigSwitcher.switchAllBack();
	}

	@Test
	public void shouldReturnEmptyDTUForEmptyQuery()
	{
		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(
				from -> Collections.emptyList());

		final List<DatabaseUtilization> utilization = givenUtilizationService.getUtilization(Duration.ofSeconds(100));

		assertThat(utilization).isEmpty();
	}


	@Test
	public void shouldReturnEmptyDTUObjectsForOneElementsFromDB()
	{
		final Instant now = Instant.now();
		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(from ->
				limitAndSortDUObjects(
						Collections.singletonList(createDU(xSecondsBackOrForw(now, -12L))),
						from, Instant.now()));

		final List<DatabaseUtilization> dList = givenUtilizationService.getUtilization(Duration.ofSeconds(10));
		assertThat(dList).isEmpty();
	}


	@Test
	public void shouldReturnEmptyDTUObjectsForTwoOrMoreElementsFromDB()
	{
		final Instant now = Instant.now();
		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(from ->
				limitAndSortDUObjects(
						Arrays.asList(createDU(xSecondsBackOrForw(now, -12L)),
								createDU(xSecondsBackOrForw(now, -15L))),
						from, Instant.now()));

		final List<DatabaseUtilization> dList = givenUtilizationService.getUtilization(Duration.ofSeconds(10));
		assertThat(dList).isEmpty();
	}

	@Test
	public void shouldReturnSomething()
	{
		final Instant now = Instant.now();

		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(from ->
				limitAndSortDUObjects(
						Arrays.asList(createDU(xSecondsBackOrForw(now, -10L)),
								createDU(xSecondsBackOrForw(now, -15L)),
								createDU(xSecondsBackOrForw(now, -20L)))
						, from, Instant.now())
		);

		List<DatabaseUtilization> dList = givenUtilizationService.getUtilization(Duration.ofSeconds(14));
		assertThat(dList).isNotEmpty();
		assertThat(dList).hasSize(1)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnly(xSecondsBackOrForw(now, -10L));
		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(18));
		assertThat(dList).hasSize(2)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnlyElementsOf(xSecondsBackOrForw(now, -10L, -15L));
		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(21));
		assertThat(dList).hasSize(3)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnlyElementsOf(xSecondsBackOrForw(now, -10L, -15L, -20L));
	}

	@Test
	public void shouldTestSmallBuffer() throws InterruptedException
	{
		bulkPropertyConfigSwitcher.switchToValue(BufferedDatabaseUtilizationService.DATABASE_UTILIZATION_BUFFER_SIZE, "3");
		bulkPropertyConfigSwitcher.switchToValue(BufferedDatabaseUtilizationService.DATABASE_UTILIZATION_QUERY_INTERVAL, "1");

		final Instant now = Instant.now();


		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(from ->
				limitAndSortDUObjects(
						Arrays.asList(createDU(xSecondsBackOrForw(now, -2L)),
								createDU(xSecondsBackOrForw(now, 2L)),
								createDU(xSecondsBackOrForw(now, 4L)))
						, from, Instant.now())
		);

		List<DatabaseUtilization> dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(1)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnly(xSecondsBackOrForw(now, -2L));

		Thread.sleep(1100);

		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).isEmpty();

		Thread.sleep(1100);

		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(1)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnly(xSecondsBackOrForw(now, 2L));

	}

	@Test
	public void shouldTestPeriodToNotFetchNewData() throws InterruptedException
	{
		bulkPropertyConfigSwitcher.switchToValue(BufferedDatabaseUtilizationService.DATABASE_UTILIZATION_BUFFER_SIZE, "100");
		bulkPropertyConfigSwitcher.switchToValue(BufferedDatabaseUtilizationService.DATABASE_UTILIZATION_QUERY_INTERVAL, "5");

		final Instant now = Instant.now();

		final BufferedDatabaseUtilizationService givenUtilizationService = new BufferedDatabaseUtilizationService(from ->
				limitAndSortDUObjects(
						Arrays.asList(createDU(xSecondsBackOrForw(now, -2L)),
								createDU(xSecondsBackOrForw(now, -1L)),
								createDU(xSecondsBackOrForw(now, 1L)))
						, from, Instant.now())
		);

		List<DatabaseUtilization> dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(2)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnlyElementsOf(xSecondsBackOrForw(now, -2L, -1L));
		Thread.sleep(2000);
		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(2)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnlyElementsOf(xSecondsBackOrForw(now, -2L, -1L));
		Thread.sleep(2000);
		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(2)
		                 .extracting(DatabaseUtilization::getObservationTime)
		                 .containsOnlyElementsOf(xSecondsBackOrForw(now, -2L, -1L));
		Thread.sleep(1000);

		dList = givenUtilizationService.getUtilization(Duration.ofSeconds(20));
		assertThat(dList).hasSize(3);

	}

	@Test
	public void shouldUseExpiredDataWhenOtherThreadIsLoadingDataFromDB()
			throws InterruptedException, ExecutionException, TimeoutException
	{
		bulkPropertyConfigSwitcher.switchToValue(BufferedDatabaseUtilizationService.DATABASE_UTILIZATION_QUERY_INTERVAL, "3");
		final ExecutorService executorService = Executors.newFixedThreadPool(2, RegistrableThread::new);

		final AtomicLong gettingDataDelayMs = new AtomicLong(0);

		final BufferedDatabaseUtilizationService utilizationService = new BufferedDatabaseUtilizationService(
				from -> {
					try
					{
						Thread.sleep(gettingDataDelayMs.get());
					}
					catch (final InterruptedException ignore)
					{
					}
					return Collections.singletonList(createDU(Instant.now()));
				});

		final Duration duration = Duration.ofSeconds(20);
		final List<DatabaseUtilization> bufferedValues = utilizationService.getUtilization(duration);
		assertThat(bufferedValues).hasSize(1);
		final DatabaseUtilization bufferedValue = bufferedValues.get(0);

		//enable long lasting gathering of data
		gettingDataDelayMs.set(TimeUnit.SECONDS.toMillis(3));
		//wait for buffer to expire
		Thread.sleep(TimeUnit.SECONDS.toMillis(4));

		final Future<List<DatabaseUtilization>> future1 = executorService.submit(
				() -> utilizationService.getUtilization(duration));

		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		final Future<List<DatabaseUtilization>> future2 = executorService.submit(
				() -> utilizationService.getUtilization(duration));

		final List<DatabaseUtilization> result1 = future1.get(10, TimeUnit.SECONDS);
		final List<DatabaseUtilization> result2 = future2.get(10, TimeUnit.SECONDS);

		assertThat(result1).hasSize(2).contains(bufferedValue);
		assertThat(result2).hasSize(1).contains(bufferedValue);
	}

	private Instant xSecondsBackOrForw(final Instant now, final Long secondsToAdd)
	{
		return now.plus(Duration.ofSeconds(secondsToAdd));
	}

	private List<Instant> xSecondsBackOrForw(final Instant now, final Long... secondsToAdd)
	{
		final Instant[] ret = new Instant[secondsToAdd.length];
		int i = 0;
		for (final Long secAgo : secondsToAdd)
		{
			ret[i++] = now.plus(Duration.ofSeconds(secAgo));
		}

		return Arrays.asList(ret);
	}

	private DatabaseUtilization createDU(final Instant observationTime)
	{
		return DatabaseUtilization.builder().withObservationTime(observationTime).build();
	}

	private List<DatabaseUtilization> limitAndSortDUObjects(final List<DatabaseUtilization> dbUtilizationList,
	                                                        final Duration duration,
	                                                        final Instant to)
	{
		return dbUtilizationList.stream()
		                        .filter(rec -> Instant.from(rec.getObservationTime())
		                                              .isAfter(Instant.now().minus(duration)))
		                        .filter(rec -> Instant.from(rec.getObservationTime()).isBefore(to))
		                        .sorted(BufferedDatabaseUtilizationService.COMPARE_BY_OBSERVATION_TIME.reversed())
		                        .collect(Collectors.toList());
	}

	private static class BulkPropertyConfigSwitcher
	{
		private final List<PropertyConfigSwitcher> switchers = new ArrayList<>();

		public void switchToValue(final String key, final String value)
		{
			final PropertyConfigSwitcher switcher = new PropertyConfigSwitcher(key);
			switcher.switchToValue(value);

			switchers.add(switcher);
		}

		public void switchAllBack()
		{
			for (final PropertyConfigSwitcher switcher : switchers)
			{
				switcher.switchBackToDefault();
			}
		}
	}

}
