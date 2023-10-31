/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.spi.service;

import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnContext;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.index.data.SnIndex;
import de.hybris.platform.searchservices.indexer.data.SnIndexerOperation;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSearchResult;
import de.hybris.platform.searchservices.spi.data.AbstractSnSearchProviderConfiguration;
import de.hybris.platform.searchservices.spi.data.SnExportConfiguration;
import de.hybris.platform.searchservices.suggest.data.SnSuggestQuery;
import de.hybris.platform.searchservices.suggest.data.SnSuggestResult;

import java.util.List;
import java.util.Locale;


/**
 * Abstraction for different search provider implementations.
 */
public interface SnSearchProvider<T extends AbstractSnSearchProviderConfiguration>
{
	/**
	 * Returns the search provider configuration.
	 *
	 * @param indexConfiguration
	 *           - the index configuration
	 *
	 * @return the search provider configuration
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	T getSearchProviderConfiguration(SnIndexConfiguration indexConfiguration) throws SnException;

	/**
	 * Exports the configuration to the search provider.
	 *
	 * @param exportConfiguration
	 *           - the export configuration
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void exportConfiguration(SnExportConfiguration exportConfiguration, List<Locale> locales) throws SnException;

	/**
	 * Creates an index.
	 *
	 * @param context
	 *           - the context
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	SnIndex createIndex(SnContext context) throws SnException;

	/**
	 * Deletes an index (if it exists).
	 *
	 * @param context
	 *           - the context
	 * @param indexId
	 *           - the index id
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void deleteIndex(SnContext context, String indexId) throws SnException;

	/**
	 * Creates a new indexer operation.
	 *
	 * @param context
	 *           - the context
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	SnIndexerOperation createIndexerOperation(SnContext context, SnIndexerOperationType indexerOperationType, int totalItems)
			throws SnException;

	/**
	 * Updates the status of an indexer operation.
	 *
	 * @param context
	 *           - the context
	 * @param status
	 *           - the status
	 * @param errorMessage
	 *           - the errorMessage
	 *
	 * @throws SnException
	 *            if an error occurs
	 *
	 * @deprecated Replaced by {@link #completeIndexerOperation(SnContext, String)},
	 *             {@link #abortIndexerOperation(SnContext, String, String)} and
	 *             {@link #failIndexerOperation(SnContext, String, String)}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	SnIndexerOperation updateIndexerOperationStatus(SnContext context, final String indexerOperationId,
			SnIndexerOperationStatus status, String errorMessage) throws SnException;

	/**
	 * Marks an indexer operation as completed.
	 *
	 * @param context
	 *           - the context
	 * @param indexerOperationId
	 *           - the indexer operation id
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void completeIndexerOperation(SnContext context, final String indexerOperationId) throws SnException;

	/**
	 * Marks an indexer operation as aborted.
	 *
	 * @param context
	 *           - the context
	 * @param indexerOperationId
	 *           - the indexer operation id
	 * @param message
	 *           - the message
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void abortIndexerOperation(SnContext context, final String indexerOperationId, String message) throws SnException;

	/**
	 * Marks an indexer operation as failed.
	 *
	 * @param context
	 *           - the context
	 * @param indexerOperationId
	 *           - the indexer operation id
	 * @param message
	 *           - the message
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void failIndexerOperation(SnContext context, final String indexerOperationId, String message) throws SnException;

	/**
	 * Executes a document batch.
	 *
	 * @param context
	 *           - the context
	 * @param indexId
	 *           - the index id
	 * @param documentBatchRequest
	 *           - the document batch request
	 * @param indexerOperationId
	 *           - the indexer operation id
	 *
	 * @return the document batch response
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	SnDocumentBatchResponse executeDocumentBatch(SnContext context, String indexId, SnDocumentBatchRequest documentBatchRequest,
			String indexerOperationId) throws SnException;

	/**
	 * Makes sure that documents are stored on the index storage and are visible for queries.
	 *
	 * @param context
	 *           - the context
	 * @param indexId
	 *           - the index id
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	void commit(SnContext context, String indexId) throws SnException;

	/**
	 * Executes a search query on a specific index.
	 *
	 * @param context
	 *           - the context
	 * @param indexId
	 *           - the index id
	 * @param searchQuery
	 *           - the search query
	 *
	 * @return the search result
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	SnSearchResult search(SnContext context, String indexId, SnSearchQuery searchQuery) throws SnException;

	/**
	 * Executes a suggest query on a specific index.
	 *
	 * @param context
	 *           - the context
	 * @param indexId
	 *           - the index id
	 * @param suggestQuery
	 *           - the suggest query
	 *
	 * @return the suggest result
	 *
	 * @throws SnException
	 *            if an error occurs
	 */
	SnSuggestResult suggest(SnContext context, String indexId, SnSuggestQuery suggestQuery) throws SnException;
}
