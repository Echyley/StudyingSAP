/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

/**
 * Represents an indexer batch context.
 */
public interface SnIndexerBatchContext extends SnIndexerContext
{
	/**
	 * Returns the indexer batch request.
	 *
	 * @return the indexer batch request
	 */
	SnIndexerBatchRequest getIndexerBatchRequest();

	/**
	 * Returns the indexer batch response.
	 *
	 * @return the indexer batch response
	 */
	SnIndexerBatchResponse getIndexerBatchResponse();

	/**
	 * Sets the indexer batch response.
	 *
	 * @param indexerBatchResponse
	 *           - the indexer batch response
	 */
	void setIndexerBatchResponse(final SnIndexerBatchResponse indexerBatchResponse);

	/**
	 * Returns the indexer batch id.
	 *
	 * @return the indexer batch id
	 */
	String getIndexerBatchId();
}
