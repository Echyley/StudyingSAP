/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;


/**
 * Default implementation for {@link SnIndexerContext}.
 */
public class DefaultSnIndexerBatchContext extends DefaultSnIndexerContext implements SnIndexerBatchContext
{
	private SnIndexerBatchRequest indexerBatchRequest;
	private SnIndexerBatchResponse indexerBatchResponse;
	private String indexerBatchId;

	@Override
	public SnIndexerBatchRequest getIndexerBatchRequest()
	{
		return indexerBatchRequest;
	}

	public void setIndexerBatchRequest(final SnIndexerBatchRequest indexerBatchRequest)
	{
		this.indexerBatchRequest = indexerBatchRequest;
	}

	@Override
	public SnIndexerBatchResponse getIndexerBatchResponse()
	{
		return indexerBatchResponse;
	}

	@Override
	public void setIndexerBatchResponse(final SnIndexerBatchResponse indexerBatchResponse)
	{
		this.indexerBatchResponse = indexerBatchResponse;
	}

	@Override
	public String getIndexerBatchId()
	{
		return indexerBatchId;
	}

	public void setIndexerBatchId(final String indexerBatchId)
	{
		this.indexerBatchId = indexerBatchId;
	}
}
