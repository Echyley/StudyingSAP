/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.FilteringService;
import de.hybris.platform.outboundsync.job.SyncFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * A filter service for all outbound item dto's collected by {@link StreamingChangesCollector}.
 */
public class DefaultFilteringService implements FilteringService
{
	private static final Logger LOG = Log.getLogger(DefaultFilteringService.class);
	private List<SyncFilter> syncFilterList = new ArrayList<>();
	private OutboundItemConsumer changeConsumer;

	/**
	 * Constructor to create FilteringService
	 *
	 * @param outboundItemConsumer - outbound item consumer responsible for consuming changes that will not be sent outbound
	 */
	public DefaultFilteringService(@NotNull final OutboundItemConsumer outboundItemConsumer)
	{
		Preconditions.checkArgument(outboundItemConsumer != null, "Outbound Item Consumer cannot be null.");
		this.changeConsumer = outboundItemConsumer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<OutboundItemDTO> applyFilters(@NotNull final OutboundItemDTO dto, @NotNull final IntegrationObjectModel io)
	{
		Preconditions.checkArgument(dto != null, "OutboundItem DTO cannot be null.");
		Preconditions.checkArgument(io != null, "IntegrationObject cannot be null.");
		return syncFilterList.stream()
		                     .filter(syncFilter -> syncFilter.isApplicable(dto))
		                     .findFirst()
		                     .map(filter -> consumeIfEvaluateIsEmpty(dto, io, filter))
		                     .orElse(Optional.of(dto));
	}

	private Optional<OutboundItemDTO> consumeIfEvaluateIsEmpty(final OutboundItemDTO dto, final IntegrationObjectModel io, final SyncFilter applicableSyncFilter)
	{
		final Optional<OutboundItemDTO> evaluatedDTO = applicableSyncFilter.evaluate(dto, io);
		if (evaluatedDTO.isEmpty())
		{
			LOG.debug("Consuming ignored change {}", dto);
			changeConsumer.consume(dto);
		}
		return evaluatedDTO;
	}

	public void setSyncFilterList(final List<SyncFilter> syncFilterList)
	{
		this.syncFilterList = syncFilterList == null ? Collections.emptyList() : Collections.unmodifiableList(syncFilterList);
	}

	OutboundItemConsumer getOutboundItemConsumer()
	{
		return changeConsumer;
	}

	void setOutboundItemConsumer(final OutboundItemConsumer outboundItemConsumer)
	{
		changeConsumer = outboundItemConsumer;
	}
}
