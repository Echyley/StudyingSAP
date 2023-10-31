/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.searchservices.admin.service.SnCommonConfigurationService;
import de.hybris.platform.searchservices.constants.SearchservicesConstants;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnListenerFactory;
import de.hybris.platform.searchservices.core.service.SnSessionService;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.index.service.SnIndexService;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.data.SnIndexerOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContextFactory;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerListener;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerStrategy;
import de.hybris.platform.searchservices.spi.service.SnSearchProvider;
import de.hybris.platform.searchservices.spi.service.SnSearchProviderFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * Base class for {@link SnIndexerStrategy} implementations.
 */
public abstract class AbstractSnIndexerStrategy implements SnIndexerStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSnIndexerStrategy.class);

	protected static final int DEFAULT_MAX_INDEXER_BATCH_SIZE = 500;

	private SnCommonConfigurationService snCommonConfigurationService;
	private SnSessionService snSessionService;
	private SnIndexerContextFactory snIndexerContextFactory;
	private SnListenerFactory snListenerFactory;
	private SnIndexService snIndexService;
	private SnSearchProviderFactory snSearchProviderFactory;

	@Override
	public SnIndexerResponse execute(final SnIndexerRequest indexerRequest) throws SnIndexerException
	{
		LOG.debug("Indexer operation started");

		SnIndexerContext indexerContext = null;
		List<SnIndexerListener> listeners = null;

		try
		{
			snSessionService.initializeSession();

			indexerContext = snIndexerContextFactory.createIndexerContext(indexerRequest);
			listeners = snListenerFactory.getListeners(indexerContext, SnIndexerListener.class);
			snSessionService.updateSessionForContext(indexerContext);

			final List<IndexerBatchGroup> indexerBatchGroups = buildIndexerBatchGroups(indexerContext);
			final int totalItems = indexerBatchGroups.stream().mapToInt(IndexerBatchGroup::getTotalItems).sum();

			if (CollectionUtils.isEmpty(indexerBatchGroups)
					&& SnIndexerOperationType.INCREMENTAL == indexerRequest.getIndexerOperationType())
			{
				LOG.debug("Skipping indexer operation: no items found matching the criteria");
				return createIndexerResponse(indexerContext, 0, 0, SnIndexerOperationStatus.COMPLETED);
			}

			final String indexId = snIndexService.getDefaultIndexId(indexerRequest.getIndexTypeId());
			indexerContext.setIndexId(indexId);

			final SnSearchProvider<?> searchProvider = snSearchProviderFactory.getSearchProviderForContext(indexerContext);

			if (SnIndexerOperationType.FULL == indexerRequest.getIndexerOperationType())
			{
				snCommonConfigurationService.exportConfiguration(indexerContext.getIndexConfiguration().getId());
			}

			final SnIndexerOperation indexerOperation = searchProvider.createIndexerOperation(indexerContext,
					indexerRequest.getIndexerOperationType(), totalItems);
			indexerContext.setIndexId(indexerOperation.getIndexId());
			indexerContext.setIndexerOperationId(indexerOperation.getId());

			executeBeforeIndexListeners(indexerContext, listeners);

			final SnIndexerResponse indexerResponse;

			if (CollectionUtils.isEmpty(indexerBatchGroups))
			{
				updateIndexerProgress(indexerRequest, SearchservicesConstants.MAX_PROGRESS_VALUE);
				indexerResponse = createIndexerResponse(indexerContext, 0, 0, SnIndexerOperationStatus.COMPLETED);
			}
			else
			{
				updateIndexerProgress(indexerRequest, SearchservicesConstants.MIN_PROGRESS_VALUE);
				indexerResponse = doExecute(indexerContext, indexerBatchGroups);
			}

			indexerContext.setIndexerResponse(indexerResponse);

			searchProvider.commit(indexerContext, indexerContext.getIndexId());
			searchProvider.completeIndexerOperation(indexerContext, indexerContext.getIndexerOperationId());

			executeAfterIndexListeners(indexerContext, listeners);

			LOG.debug("Indexer operation finished with status '{}'", indexerResponse.getStatus());

			return indexerResponse;
		}
		catch (final SnException | RuntimeException e)
		{
			if (indexerContext != null)
			{
				indexerContext.addException(e);
				executeAfterIndexErrorListeners(indexerContext, listeners);
				failIndexerOperationQuietly(indexerContext, e);
			}
			final String exceptionName;
			if (e.getCause() != null)
			{
				exceptionName = ExceptionUtils.getRootCause(e).getClass().getName();
			}
			else
			{
				exceptionName = e.getClass().getName();
			}
			final String errorMessage = MessageFormat.format("Indexer operation failed with {0}: {1}", exceptionName,
					e.getMessage());
			throw new SnIndexerException(errorMessage, e);
		}
		finally
		{
			snSessionService.destroySession();
		}
	}

	protected void updateIndexerProgress(final SnIndexerRequest indexerRequest, final Double value)
	{
		if (indexerRequest.getProgressTracker() != null)
		{
			indexerRequest.getProgressTracker().setProgress(value);
		}
	}

	protected void failIndexerOperationQuietly(final SnIndexerContext indexerContext, final Exception exception)
	{
		if (StringUtils.isBlank(indexerContext.getIndexerOperationId()))
		{
			return;
		}

		try
		{
			final String message = extractErrorMessage(exception);
			final SnSearchProvider<?> searchProvider = snSearchProviderFactory.getSearchProviderForContext(indexerContext);
			searchProvider.failIndexerOperation(indexerContext, indexerContext.getIndexerOperationId(), message);
		}
		catch (final SnException | RuntimeException e)
		{
			LOG.error("Update indexer operation status failed", e);
		}
	}

	protected String extractErrorMessage(final Exception exception)
	{
		return exception == null ? null : exception.getMessage();
	}

	protected void executeBeforeIndexListeners(final SnIndexerContext indexerContext, final List<SnIndexerListener> listeners)
			throws SnException
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerListener listener : listeners)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Running {}.beforeIndex ...", listener.getClass().getCanonicalName());
			}

			listener.beforeIndex(indexerContext);
		}
	}

	protected void executeAfterIndexListeners(final SnIndexerContext indexerContext, final List<SnIndexerListener> listeners)
			throws SnException
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerListener listener : Lists.reverse(listeners))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Running {}.afterIndex ...", listener.getClass().getCanonicalName());
			}

			listener.afterIndex(indexerContext);
		}
	}

	protected void executeAfterIndexErrorListeners(final SnIndexerContext indexerContext, final List<SnIndexerListener> listeners)
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerListener listener : Lists.reverse(listeners))
		{
			try
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Running {}.afterIndexError ...", listener.getClass().getCanonicalName());
				}

				listener.afterIndexError(indexerContext);
			}
			catch (final SnIndexerException | RuntimeException exception)
			{
				indexerContext.addException(exception);
			}
		}
	}

	protected List<IndexerBatchGroup> buildIndexerBatchGroups(final SnIndexerContext indexerContext) throws SnIndexerException
	{
		final int maxIndexerBatchSize = DEFAULT_MAX_INDEXER_BATCH_SIZE;

		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = joinAndDedupIndexerItemSourceOperations(
				indexerContext);
		final List<IndexerBatchGroup> indexerBatchGroups = new ArrayList<>();

		int indexerBatchIndex = 0;
		int indexerBatchSize = 0;
		List<SnIndexerItemSourceOperation> indexerBatchItemSourceOperations = null;

		for (final SnIndexerItemSourceOperation indexerItemSourceOperation : indexerItemSourceOperations)
		{
			final SnDocumentOperationType documentOperationType = indexerItemSourceOperation.getDocumentOperationType();
			final SnIndexerItemSource indexerItemSource = indexerItemSourceOperation.getIndexerItemSource();
			final List<PK> indexerItemSourcePks = indexerItemSource.getPks(indexerContext);
			final int indexerItemSourcePksSize = indexerItemSourcePks.size();

			if (indexerItemSourcePksSize + indexerBatchSize <= maxIndexerBatchSize)
			{
				if (indexerBatchItemSourceOperations == null)
				{
					indexerBatchItemSourceOperations = new ArrayList<>();
				}

				indexerBatchSize += indexerItemSourcePksSize;
				indexerBatchItemSourceOperations.add(indexerItemSourceOperation);
			}
			else if (indexerItemSourcePksSize <= maxIndexerBatchSize)
			{
				if (indexerBatchItemSourceOperations != null)
				{
					indexerBatchGroups
							.add(buildIndexerBatchGroup(indexerBatchItemSourceOperations, indexerBatchIndex, indexerBatchSize));
					indexerBatchIndex++;
				}

				indexerBatchSize = indexerItemSourcePksSize;
				indexerBatchItemSourceOperations = new ArrayList<>();
				indexerBatchItemSourceOperations.add(indexerItemSourceOperation);
			}
			else
			{
				if (indexerBatchItemSourceOperations != null)
				{
					indexerBatchGroups
							.add(buildIndexerBatchGroup(indexerBatchItemSourceOperations, indexerBatchIndex, indexerBatchSize));
					indexerBatchIndex++;
				}

				indexerBatchGroups.add(
						buildIndexerBatchGroup(documentOperationType, indexerItemSourcePks, indexerBatchIndex, maxIndexerBatchSize));
				indexerBatchIndex++;

				indexerBatchSize = 0;
				indexerBatchItemSourceOperations = null;
			}

			if (indexerBatchItemSourceOperations != null)
			{
				indexerBatchGroups.add(buildIndexerBatchGroup(indexerBatchItemSourceOperations, indexerBatchIndex, indexerBatchSize));
			}
		}

		return indexerBatchGroups;
	}

	protected List<SnIndexerItemSourceOperation> joinAndDedupIndexerItemSourceOperations(final SnIndexerContext indexerContext)
			throws SnIndexerException
	{
		final List<SnIndexerItemSourceOperation> targetItemSourceOperations = new ArrayList<>();

		SnDocumentOperationType documentOperationType = null;
		Set<PK> indexerItemSourcePks = null;
		Set<String> fieldIds = null;

		for (final SnIndexerItemSourceOperation sourceIndexerItemSourceOperation : indexerContext.getIndexerItemSourceOperations())
		{
			final SnDocumentOperationType sourceDocumentOperationType = sourceIndexerItemSourceOperation.getDocumentOperationType();
			final Set<String> sourceFieldIds = sourceIndexerItemSourceOperation.getFieldIds();
			final SnIndexerItemSource sourceIndexerItemSource = sourceIndexerItemSourceOperation.getIndexerItemSource();
			final List<PK> sourceIndexerItemSourcePks = sourceIndexerItemSource.getPks(indexerContext);

			if (CollectionUtils.isNotEmpty(sourceIndexerItemSourcePks))
			{
				if (documentOperationType == null)
				{
					documentOperationType = sourceDocumentOperationType;
					indexerItemSourcePks = new LinkedHashSet<>(sourceIndexerItemSourcePks);
					fieldIds = sourceFieldIds;
				}
				else if (Objects.equal(documentOperationType, sourceDocumentOperationType) && Objects.equal(fieldIds, sourceFieldIds))
				{
					indexerItemSourcePks.addAll(sourceIndexerItemSourcePks);
				}
				else
				{
					final SnIndexerItemSource targetIndexerItemSource = new PksSnIndexerItemSource(List.copyOf(indexerItemSourcePks));
					final SnIndexerItemSourceOperation targetItemSourceOperation = new DefaultSnIndexerItemSourceOperation(
							documentOperationType, targetIndexerItemSource, fieldIds);

					targetItemSourceOperations.add(targetItemSourceOperation);

					documentOperationType = null;
					indexerItemSourcePks = null;
					fieldIds = null;
				}
			}
		}

		if (CollectionUtils.isNotEmpty(indexerItemSourcePks) && documentOperationType != null)
		{
			final SnIndexerItemSource targetIndexerItemSource = new PksSnIndexerItemSource(List.copyOf(indexerItemSourcePks));
			final SnIndexerItemSourceOperation targetItemSourceOperation = new DefaultSnIndexerItemSourceOperation(
					documentOperationType, targetIndexerItemSource, fieldIds);

			targetItemSourceOperations.add(targetItemSourceOperation);
		}

		return targetItemSourceOperations;
	}

	protected IndexerBatchGroup buildIndexerBatchGroup(final List<SnIndexerItemSourceOperation> batchIndexerItemSourceOperations,
			final int batchIndex, final int batchSize)
	{
		final List<IndexerBatch> indexerBatches = List
				.of(new IndexerBatch(batchIndexerItemSourceOperations, buildIndexerBatchId(batchIndex)));
		return new IndexerBatchGroup(indexerBatches, batchSize);
	}

	protected IndexerBatchGroup buildIndexerBatchGroup(final SnDocumentOperationType documentOperationType,
			final List<PK> indexerItemSourcePks, final int batchIndex, final int maxBatchSize)
	{
		final List<IndexerBatch> indexerBatches = new ArrayList<>();

		for (int index = 0, start = 0; start < indexerItemSourcePks.size(); index++, start += maxBatchSize)
		{
			final int end = Math.min(start + maxBatchSize, indexerItemSourcePks.size());

			final List<PK> batchIndexerItemSourcePks = indexerItemSourcePks.subList(start, end);
			final SnIndexerItemSource batchIndexerItemSource = new PksSnIndexerItemSource(batchIndexerItemSourcePks);
			final List<SnIndexerItemSourceOperation> batchIndexerItemsGroups = List
					.of(new DefaultSnIndexerItemSourceOperation(documentOperationType, batchIndexerItemSource));

			final IndexerBatch indexerBatch = new IndexerBatch(batchIndexerItemsGroups, buildIndexerBatchId(batchIndex + index));

			indexerBatches.add(indexerBatch);
		}

		return new IndexerBatchGroup(indexerBatches, indexerItemSourcePks.size());
	}

	protected String buildIndexerBatchId(final int batchIndex)
	{
		return String.valueOf(batchIndex);
	}

	protected abstract SnIndexerResponse doExecute(final SnIndexerContext indexerContext,
			final List<IndexerBatchGroup> indexerBatchGroups) throws SnIndexerException;

	protected SnIndexerResponse createIndexerResponse(final SnIndexerContext indexerContext, final Integer totalItems,
			final Integer processedItems, final SnIndexerOperationStatus status)
	{
		final DefaultSnIndexerResponse indexerResponse = new DefaultSnIndexerResponse(indexerContext.getIndexConfiguration(),
				indexerContext.getIndexType());
		indexerResponse.setTotalItems(totalItems);
		indexerResponse.setProcessedItems(processedItems);
		indexerResponse.setStatus(status);

		return indexerResponse;
	}

	public SnCommonConfigurationService getSnCommonConfigurationService()
	{
		return snCommonConfigurationService;
	}

	@Required
	public void setSnCommonConfigurationService(final SnCommonConfigurationService snCommonConfigurationService)
	{
		this.snCommonConfigurationService = snCommonConfigurationService;
	}

	public SnSessionService getSnSessionService()
	{
		return snSessionService;
	}

	@Required
	public void setSnSessionService(final SnSessionService snSessionService)
	{
		this.snSessionService = snSessionService;
	}

	public SnIndexerContextFactory getSnIndexerContextFactory()
	{
		return snIndexerContextFactory;
	}

	@Required
	public void setSnIndexerContextFactory(final SnIndexerContextFactory snIndexerContextFactory)
	{
		this.snIndexerContextFactory = snIndexerContextFactory;
	}

	public SnListenerFactory getSnListenerFactory()
	{
		return snListenerFactory;
	}

	@Required
	public void setSnListenerFactory(final SnListenerFactory snListenerFactory)
	{
		this.snListenerFactory = snListenerFactory;
	}

	public SnIndexService getSnIndexService()
	{
		return snIndexService;
	}

	@Required
	public void setSnIndexService(final SnIndexService snIndexService)
	{
		this.snIndexService = snIndexService;
	}

	public SnSearchProviderFactory getSnSearchProviderFactory()
	{
		return snSearchProviderFactory;
	}

	@Required
	public void setSnSearchProviderFactory(final SnSearchProviderFactory snSearchProviderFactory)
	{
		this.snSearchProviderFactory = snSearchProviderFactory;
	}

	protected static class IndexerBatch
	{
		private final List<SnIndexerItemSourceOperation> indexerItemSourceOperations;
		private final String indexerBatchId;

		public IndexerBatch(final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final String indexerBatchId)
		{
			this.indexerItemSourceOperations = indexerItemSourceOperations;
			this.indexerBatchId = indexerBatchId;
		}

		public List<SnIndexerItemSourceOperation> getIndexerItemSourceOperations()
		{
			return indexerItemSourceOperations;
		}

		public String getIndexerBatchId()
		{
			return indexerBatchId;
		}
	}

	protected static class IndexerBatchGroup
	{
		private final List<IndexerBatch> indexerBatches;
		private final int totalItems;

		public IndexerBatchGroup(final List<IndexerBatch> indexerBatches, final int totalItems)
		{
			this.indexerBatches = indexerBatches;
			this.totalItems = totalItems;
		}

		public List<IndexerBatch> getIndexerBatches()
		{
			return indexerBatches;
		}

		public int getTotalItems()
		{
			return totalItems;
		}
	}
}
