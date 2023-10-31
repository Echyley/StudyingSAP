/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.outboundsync.job.ChangesCollectorFactory;
import de.hybris.platform.outboundsync.job.CountingChangesCollector;
import de.hybris.platform.outboundsync.job.FilteringService;
import de.hybris.platform.outboundsync.job.ItemChangeSender;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel;

/**
 * Streaming changes collector implementation for the factory.
 */
public class DefaultChangesCollectorFactory implements ChangesCollectorFactory
{
	private ItemChangeSender itemChangeSender;
	private FilteringService filteringService;

	@Override
	public CountingChangesCollector createCountingCollector(final OutboundSyncCronJobModel cronJob,
	                                                        final OutboundSyncStreamConfigurationModel stream)
	{
		return new StreamingChangesCollector(filteringService, itemChangeSender, cronJob, stream);
	}

	public void setItemChangeSender(final ItemChangeSender itemChangeSender)
	{
		this.itemChangeSender = itemChangeSender;
	}

	public void setFilteringService(final FilteringService filteringService)
	{
		this.filteringService = filteringService;
	}
}
