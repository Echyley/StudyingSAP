/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl;

import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.dto.ChangeInfo;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder;
import de.hybris.platform.outboundsync.dto.impl.DeltaDetectionOutboundItemChange;
import de.hybris.platform.outboundsync.job.CountingChangesCollector;
import de.hybris.platform.outboundsync.job.FilteringService;
import de.hybris.platform.outboundsync.job.ItemChangeSender;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel;

import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

public class StreamingChangesCollector implements CountingChangesCollector
{
	private static final Logger LOG = Log.getLogger(StreamingChangesCollector.class);
	private static final ChangeInfoParser DEFAULT_PARSER = new DefaultChangeInfoParser();

	private final OutboundSyncStreamConfigurationModel streamConfiguration;
	private final FilteringService filteringService;
	private final ItemChangeSender itemChangeSender;
	private final AtomicInteger numOfChanges;
	private final CronJobModel cronJobModel;
	private ChangeInfoParser infoParser;

	public StreamingChangesCollector(@NotNull final FilteringService filteringService,
	                                 @NotNull final ItemChangeSender changeSender,
	                                 @NotNull final CronJobModel jobModel,
	                                 @NotNull final OutboundSyncStreamConfigurationModel streamCfg)
	{
		Preconditions.checkArgument(filteringService != null, "FilteringService cannot be null.");
		Preconditions.checkArgument(changeSender != null, "ItemChangeSender cannot be null.");
		Preconditions.checkArgument(jobModel != null, "CronJobModel cannot be null");
		Preconditions.checkArgument(streamCfg != null, "OutboundSyncStreamConfigurationModel cannot be null.");

		this.filteringService = filteringService;
		itemChangeSender = changeSender;
		streamConfiguration = streamCfg;
		cronJobModel = jobModel;
		numOfChanges = new AtomicInteger();
		infoParser = DEFAULT_PARSER;
	}

	/**
	 * {@inheritDoc}
	 * Sends each individual change to the spring integration channel gateway.
	 */
	@Override
	public boolean collect(final ItemChangeDTO itemChangeDTO)
	{
		LOG.debug("Sending changes for itemChangeDTO: {}", itemChangeDTO);
		final OutboundItemDTO itemDTO = convert(itemChangeDTO);
		final IntegrationObjectModel io = streamConfiguration.getOutboundChannelConfiguration().getIntegrationObject();
		filteringService.applyFilters(itemDTO, io).ifPresent(this::sendChange);
		return true;
	}

	private OutboundItemDTO convert(final ItemChangeDTO change)
	{
		final OutboundChannelConfigurationModel channelConfig = streamConfiguration.getOutboundChannelConfiguration();
		return OutboundItemDTOBuilder.outboundItemDTO()
		                             .withItem(new DeltaDetectionOutboundItemChange(change))
		                             .withChannelConfiguration(channelConfig)
		                             .withCronJobPK(cronJobModel.getPk())
		                             .withInfo(infoFromChange(change))
		                             .build();
	}

	private ChangeInfo infoFromChange(final ItemChangeDTO change)
	{
		return StringUtils.isNotBlank(streamConfiguration.getInfoExpression())
				? infoParser.parse(change.getInfo()).orElse(null)
				: null;
	}

	private void sendChange(final OutboundItemDTO itemDTO)
	{
		itemChangeSender.send(itemDTO);
		numOfChanges.incrementAndGet();
	}

	@Override
	public void finish()
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Detected {} changes of type {}", numOfChanges.get(),
					streamConfiguration.getItemTypeForStream().getCode());
		}
	}

	/**
	 * {@inheritDoc}
	 * This collector counts all changes processed during the lifetime of this specific instance. If
	 * "reset" is required, then a new instance of the collector must be created.
	 */
	@Override
	public int getNumberOfChangesCollected()
	{
		return numOfChanges.get();
	}

	/**
	 * Injects implementation of an info parser to be used instead of the default implementation.
	 *
	 * @param parser a custom parser to be used for parsing the {@link ItemChangeDTO#getInfo()} data. {@code null} value is ignored.
	 */
	public void setInfoParser(final ChangeInfoParser parser)
	{
		if (parser != null)
		{
			infoParser = parser;
		}
	}
}