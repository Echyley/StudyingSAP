/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;


import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.model.AbstractSnIndexerItemSourceModel;
import de.hybris.platform.searchservices.model.SnIndexerItemSourceOperationModel;

import java.util.List;
import java.util.Map;


/**
 * Service for indexer operations.
 */
public interface SnIndexerService
{
	/**
	 * Creates a new instance of {@link SnIndexerItemSource}.
	 *
	 * @param itemSource
	 *           - the item source
	 * @param parameters
	 *           - the parameters
	 *
	 * @return the new instance of {@link SnIndexerItemSource}
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerItemSource createItemSource(AbstractSnIndexerItemSourceModel itemSource, Map<String, Object> parameters)
			throws SnIndexerException;

	/**
	 * Creates a new instance of {@link SnIndexerItemSourceOperation}.
	 *
	 * @param itemSourceOperation
	 *           - the item source operation
	 * @param parameters
	 *           - the parameters
	 *
	 * @return the new instance of {@link SnIndexerItemSourceOperation}
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerItemSourceOperation createItemSourceOperation(SnIndexerItemSourceOperationModel itemSourceOperation,
			Map<String, Object> parameters) throws SnIndexerException;

	/**
	 * Creates a full indexer request.
	 *
	 * @param indexTypeId
	 *           - the index type id
	 * @param indexerItemSource
	 *           - the indexer item source
	 *
	 * @return the new indexer request
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerRequest createFullIndexerRequest(String indexTypeId, SnIndexerItemSource indexerItemSource) throws SnIndexerException;

	/**
	 * Creates a full indexer request.
	 *
	 * @param indexTypeId
	 *           - the index type id
	 * @param indexerItemSource
	 *           - the indexer item source
	 * @param progressTracker
	 *           - the progress tracker
	 *
	 * @return the new indexer request
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerRequest createFullIndexerRequest(String indexTypeId, SnIndexerItemSource indexerItemSource,
			SnProgressTracker progressTracker) throws SnIndexerException;

	/**
	 * Creates an incremental indexer request.
	 *
	 * @param indexTypeId
	 *           - the index type id
	 * @param indexerOperationType
	 *           - the indexer operation type
	 * @param indexerItemSourceOperations
	 *           - the indexer item source operations
	 *
	 * @return the new indexer request
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerRequest createIncrementalIndexerRequest(String indexTypeId,
			List<SnIndexerItemSourceOperation> indexerItemSourceOperations) throws SnIndexerException;

	/**
	 * Creates an incremental indexer request.
	 *
	 * @param indexTypeId
	 *           - the index type id
	 * @param indexerOperationType
	 *           - the indexer operation type
	 * @param indexerItemSourceOperations
	 *           - the indexer item source operations
	 * @param progressTracker
	 *           - the progress tracker
	 *
	 * @return the new indexer request
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerRequest createIncrementalIndexerRequest(String indexTypeId,
			List<SnIndexerItemSourceOperation> indexerItemSourceOperations, SnProgressTracker progressTracker)
			throws SnIndexerException;

	/**
	 * Starts a new indexer operation.
	 *
	 * @param indexerRequest
	 *           - the indexer request
	 *
	 * @return the indexer response
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerResponse index(SnIndexerRequest indexerRequest) throws SnIndexerException;
}
