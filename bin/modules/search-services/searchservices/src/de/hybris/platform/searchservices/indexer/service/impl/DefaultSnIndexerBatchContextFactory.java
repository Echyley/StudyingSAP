/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContextFactory;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchRequest;

import java.util.Objects;


/**
 * Default implementation for {@link SnIndexerBatchContextFactory}.
 */
public class DefaultSnIndexerBatchContextFactory extends DefaultSnIndexerContextFactory implements SnIndexerBatchContextFactory
{
	@Override
	public SnIndexerBatchContext createIndexerBatchContext(final SnIndexerBatchRequest indexerBatchRequest)
			throws SnIndexerException
	{
		Objects.requireNonNull(indexerBatchRequest, "indexerBatchRequest must not be null");

		final DefaultSnIndexerBatchContext context = new DefaultSnIndexerBatchContext();
		populateContext(context, indexerBatchRequest.getIndexTypeId());
		populateIndexerContext(context, indexerBatchRequest);
		populateIndexerBatchContext(context, indexerBatchRequest);

		return context;
	}

	protected void populateIndexerBatchContext(final DefaultSnIndexerBatchContext context,
			final SnIndexerBatchRequest indexerBatchRequest)
	{
		context.setIndexerBatchRequest(indexerBatchRequest);
		context.setIndexId(indexerBatchRequest.getIndexId());
		context.setIndexerOperationId(indexerBatchRequest.getIndexerOperationId());
		context.setIndexerBatchId(indexerBatchRequest.getIndexerBatchId());
	}
}
