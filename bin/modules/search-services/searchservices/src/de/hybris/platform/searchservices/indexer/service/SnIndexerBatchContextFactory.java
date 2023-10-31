/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.indexer.SnIndexerException;


/**
 * Implementations of this interface are responsible for creating instances of {@link SnIndexerBatchContext}.
 */
public interface SnIndexerBatchContextFactory
{
	/**
	 * Creates a new instance of {@link SnIndexerBatchContext}.
	 *
	 * @param indexerBatchRequest
	 *           - the indexer batch request
	 *
	 * @return the new instance of {@link SnIndexerBatchContext}
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerBatchContext createIndexerBatchContext(SnIndexerBatchRequest indexerBatchRequest) throws SnIndexerException;
}
