/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.constants.SearchservicesConstants;
import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.model.IncrementalSnIndexerCronJobModel;
import de.hybris.platform.searchservices.model.SnIndexerItemSourceOperationModel;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Implementation of {@link JobPerformable} for running incremental indexer operations.
 */
public class IncrementalSnIndexerJob<T extends IncrementalSnIndexerCronJobModel> extends AbstractSnIndexerJob<T>
{
	@Override
	protected SnIndexerRequest createIndexerRequest(final T cronJob, final SnProgressTracker progressTracker)
			throws SnIndexerException
	{
		final Map<String, Object> parameters = buildParameters(cronJob);
		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = new ArrayList<>();

		for (final SnIndexerItemSourceOperationModel indexerItemSourceOperationModel : cronJob.getIndexerItemSourceOperations())
		{
			final SnIndexerItemSourceOperation indexerItemSourceOperation = getSnIndexerService()
					.createItemSourceOperation(indexerItemSourceOperationModel, parameters);
			indexerItemSourceOperations.add(indexerItemSourceOperation);
		}

		return getSnIndexerService().createIncrementalIndexerRequest(cronJob.getIndexType().getId(), indexerItemSourceOperations,
				progressTracker);
	}

	protected Map<String, Object> buildParameters(final T cronJob) throws SnIndexerException
	{
		final Date startTime = getLastSuccessfulStartTime(cronJob).orElseThrow(
				() -> new SnIndexerException("The incremental index cronjob cannot retrieve the last successful start time. "
						+ "Please trigger the full index cronjob once to index this index type for the first time."));

		return Map.of(SearchservicesConstants.INDEXER_ITEM_SOURCE_PARAM_START_TIME, startTime);
	}
}
