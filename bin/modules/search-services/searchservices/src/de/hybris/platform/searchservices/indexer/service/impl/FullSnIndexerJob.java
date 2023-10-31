/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.model.FullSnIndexerCronJobModel;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;

import java.util.Map;


/**
 * Implementation of {@link JobPerformable} for running full indexer operations.
 */
public class FullSnIndexerJob<T extends FullSnIndexerCronJobModel> extends AbstractSnIndexerJob<T>
{
	@Override
	protected SnIndexerRequest createIndexerRequest(final T cronJob, final SnProgressTracker progressTracker)
			throws SnIndexerException
	{
		final Map<String, Object> parameters = buildParameters(cronJob);
		final SnIndexerItemSource indexerItemSource = getSnIndexerService().createItemSource(cronJob.getIndexerItemSource(),
				parameters);

		return getSnIndexerService().createFullIndexerRequest(cronJob.getIndexType().getId(), indexerItemSource, progressTracker);
	}

	protected Map<String, Object> buildParameters(final T cronJob) throws SnIndexerException
	{
		return Map.of();
	}
}
