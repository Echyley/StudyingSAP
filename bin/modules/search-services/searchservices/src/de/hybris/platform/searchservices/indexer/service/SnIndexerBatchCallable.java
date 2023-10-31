/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * Callable for indexer batches.
 */
public interface SnIndexerBatchCallable extends Callable<SnIndexerBatchResponse>
{
	/**
	 * Initializes the indexer batch runnable.
	 *
	 * @param indexerContext
	 *           - the indexer context
	 * @param indexerItemSourceOperations
	 *           - the indexer item source operations
	 * @param indexerBatchId
	 *           - the indexer batch id
	 */
	void initialize(SnIndexerContext indexerContext, List<SnIndexerItemSourceOperation> indexerItemSourceOperations,
			String indexerBatchId);

	/**
	 * Returns the indexer batch id;
	 *
	 * @return the indexer batch id
	 */
	String getIndexerBatchId();
}
