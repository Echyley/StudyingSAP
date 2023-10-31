/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchCallable;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerStrategy;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Default implementation for {@link SnIndexerStrategy}.
 */
public class DefaultSnIndexerStrategy extends AbstractSnIndexerStrategy implements ApplicationContextAware
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSnIndexerStrategy.class);

	protected static final int DEFAULT_INDEXER_THREAD_POOL_SIZE = 5;
	protected static final int DEFAULT_MAX_INDEXER_BATCH_RETRIES = 1;
	protected static final int DEFAULT_MAX_INDEXER_RETRIES = 3;

	private String indexerBatchCallableId;
	private ApplicationContext applicationContext;

	@Override
	protected SnIndexerResponse doExecute(final SnIndexerContext indexerContext, final List<IndexerBatchGroup> indexerBatchGroups)
			throws SnIndexerException
	{
		final SnIndexerRequest indexerRequest = indexerContext.getIndexerRequest();
		final int totalItems = indexerBatchGroups.stream().mapToInt(IndexerBatchGroup::getTotalItems).sum();
		final int totalBatches = indexerBatchGroups.stream().map(IndexerBatchGroup::getIndexerBatches).mapToInt(List::size).sum();

		final int threadPoolSize = Math.min(totalBatches, DEFAULT_INDEXER_THREAD_POOL_SIZE);
		final int maxRetries = Math.max(0, DEFAULT_MAX_INDEXER_RETRIES);
		final int maxBatchRetries = Math.max(0, DEFAULT_MAX_INDEXER_BATCH_RETRIES);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of items: {}", totalItems);
			LOG.debug("Number of indexer batches: {}", totalBatches);
			LOG.debug("Thread pool size: {}", threadPoolSize);
		}

		final IndexerTracker indexerTracker = new IndexerTracker(indexerRequest.getProgressTracker(), totalItems, maxRetries);
		final IndexerBatchThreadPoolExecutor executorService = new IndexerBatchThreadPoolExecutor(threadPoolSize);
		SnIndexerOperationStatus indexerOperationStatus;

		try
		{
			indexerOperationStatus = executeIndexerBatchGroups(indexerContext, indexerTracker, executorService, indexerBatchGroups,
					maxBatchRetries);
		}
		catch (final InterruptedException e)
		{
			indexerOperationStatus = SnIndexerOperationStatus.ABORTED;
		}
		finally
		{
			executorService.shutdownNow();
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of processed items: {}", indexerTracker.getProcessedItems());
		}

		return createIndexerResponse(indexerContext, indexerTracker.getTotalItems(), indexerTracker.getProcessedItems(),
				indexerOperationStatus);
	}

	protected SnIndexerOperationStatus executeIndexerBatchGroups(final SnIndexerContext indexerContext,
			final IndexerTracker indexerTracker, final IndexerBatchThreadPoolExecutor executorService,
			final List<IndexerBatchGroup> indexerBatchGroups, final int maxBatchRetries)
			throws SnIndexerException, InterruptedException
	{
		for (final IndexerBatchGroup indexerBatchGroup : indexerBatchGroups)
		{
			final SnIndexerOperationStatus indexerOperationStatus = executeIndexerBatchGroup(indexerContext, executorService,
					indexerTracker, indexerBatchGroup, maxBatchRetries);
			if (indexerOperationStatus != SnIndexerOperationStatus.COMPLETED)
			{
				return indexerOperationStatus;
			}
		}

		return SnIndexerOperationStatus.COMPLETED;
	}

	protected SnIndexerOperationStatus executeIndexerBatchGroup(final SnIndexerContext indexerContext,
			final IndexerBatchThreadPoolExecutor executorService, final IndexerTracker indexerTracker,
			final IndexerBatchGroup indexerBatchGroup, final int maxBatchRetries) throws SnIndexerException, InterruptedException
	{
		final CompletionService<SnIndexerBatchResponse> completionService = new ExecutorCompletionService<>(executorService);

		final Map<String, IndexerBatchTracker> indexerBatchTrackers = createIndexerBatchTrackers(indexerContext, indexerBatchGroup,
				maxBatchRetries);
		for (final IndexerBatchTracker indexerBatchTracker : indexerBatchTrackers.values())
		{
			submitIndexerBatch(completionService, indexerBatchTracker);
		}

		while (MapUtils.isNotEmpty(indexerBatchTrackers))
		{
			final IndexerBatchFuture indexerBatchFuture = (IndexerBatchFuture) completionService.take();
			final String indexerBatchId = indexerBatchFuture.getIndexerBatchCallable().getIndexerBatchId();
			final IndexerBatchTracker indexerBatchTracker = indexerBatchTrackers.get(indexerBatchId);

			final SnIndexerBatchResponse indexerBatchResponse = retrieveIndexerBatchResponse(indexerContext, indexerBatchTracker,
					indexerBatchFuture);

			if (indexerBatchResponse.getStatus() == SnIndexerOperationStatus.COMPLETED)
			{
				if (indexerBatchResponse.getProcessedItems() != null)
				{
					indexerTracker.incrementProcessedItems(indexerBatchResponse.getProcessedItems());
				}

				indexerBatchTrackers.remove(indexerBatchId);
			}
			else if (indexerBatchResponse.getStatus() == SnIndexerOperationStatus.ABORTED)
			{
				return SnIndexerOperationStatus.ABORTED;
			}
			else
			{
				if (canSubmitIndexerBatchAfterError(indexerTracker, indexerBatchTracker))
				{
					submitIndexerBatch(completionService, indexerBatchTracker);
				}
				else
				{
					return SnIndexerOperationStatus.FAILED;
				}
			}
		}

		return SnIndexerOperationStatus.COMPLETED;
	}

	protected Map<String, IndexerBatchTracker> createIndexerBatchTrackers(final SnIndexerContext indexerContext,
			final IndexerBatchGroup indexerBatchGroup, final int maxBatchRetries) throws SnIndexerException
	{
		final Map<String, IndexerBatchTracker> indexerBatchTrackers = new HashMap<>();

		for (final IndexerBatch indexerBatch : indexerBatchGroup.getIndexerBatches())
		{
			final SnIndexerBatchCallable indexerBatchCallable = createIndexerBatchCallable(indexerContext,
					indexerBatch.getIndexerItemSourceOperations(), indexerBatch.getIndexerBatchId());
			final IndexerBatchTracker indexerBatchTracker = new IndexerBatchTracker(indexerBatch, indexerBatchCallable,
					maxBatchRetries);

			indexerBatchTrackers.put(indexerBatch.getIndexerBatchId(), indexerBatchTracker);
		}

		return indexerBatchTrackers;
	}

	protected SnIndexerBatchCallable createIndexerBatchCallable(final SnIndexerContext indexerContext,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final String indexerBatchId)
			throws SnIndexerException
	{
		try
		{
			final SnIndexerBatchCallable indexerBatchCallable = applicationContext.getBean(indexerBatchCallableId,
					SnIndexerBatchCallable.class);
			indexerBatchCallable.initialize(indexerContext, indexerItemSourceOperations, indexerBatchId);

			return indexerBatchCallable;
		}
		catch (final BeansException e)
		{
			throw new SnIndexerException("Cannot create indexer batch callable [" + indexerBatchCallableId + "]", e);
		}
	}

	protected SnIndexerBatchResponse retrieveIndexerBatchResponse(final SnIndexerContext indexerContext,
			final IndexerBatchTracker indexerBatchTracker, final IndexerBatchFuture indexerBatchFuture) throws InterruptedException
	{
		try
		{
			final SnIndexerBatchResponse indexerBatchResponse = indexerBatchFuture.get();

			if (indexerBatchResponse.getStatus() != SnIndexerOperationStatus.COMPLETED
					&& indexerBatchResponse.getStatus() != SnIndexerOperationStatus.ABORTED)
			{
				LOG.info("Indexer batch {} failed", indexerBatchTracker.getIndexerBatchId());
			}

			return indexerBatchResponse;
		}
		catch (final ExecutionException e)
		{
			LOG.error(MessageFormat.format("Indexer batch {0} failed", indexerBatchTracker.getIndexerBatchId()), e.getCause());

			final DefaultSnIndexerBatchResponse indexerBatchResponse = new DefaultSnIndexerBatchResponse(
					indexerContext.getIndexConfiguration(), indexerContext.getIndexType());
			indexerBatchResponse.setStatus(SnIndexerOperationStatus.FAILED);

			return indexerBatchResponse;
		}
	}

	protected void submitIndexerBatch(final CompletionService<SnIndexerBatchResponse> completionService,
			final IndexerBatchTracker indexerBatchTracker)
	{
		final String batchId = indexerBatchTracker.getIndexerBatch().getIndexerBatchId();
		completionService.submit(indexerBatchTracker.getIndexerBatchCallable());

		LOG.debug("Indexer batch {} has been submitted", batchId);
	}

	protected boolean canSubmitIndexerBatchAfterError(final IndexerTracker indexerTracker,
			final IndexerBatchTracker indexerBatchTracker)
	{
		if (indexerTracker.getAndDecrementRetriesLeft() <= 0)
		{
			LOG.info("Indexer batch {} retry failed, max total retries has been reached: [maxRetries={}]",
					indexerBatchTracker.getIndexerBatchId(), indexerTracker.getMaxRetries());
			return false;
		}
		else if (indexerBatchTracker.getAndDecrementRetriesLeft() <= 0)
		{
			LOG.info("Indexer batch {} retry failed, max batch retries has been reached: [maxBatchRetries={}]",
					indexerBatchTracker.getIndexerBatchId(), indexerBatchTracker.getMaxRetries());
			return false;
		}
		else
		{
			LOG.info("Indexer batch {} retry: [retriesLeft={}, batchRetriesLeft={}]", indexerBatchTracker.getIndexerBatchId(),
					indexerTracker.retriesLeft, indexerBatchTracker.retriesLeft);
			return true;
		}
	}

	public String getIndexerBatchCallableId()
	{
		return indexerBatchCallableId;
	}

	@Required
	public void setIndexerBatchCallableId(final String indexerBatchCallableId)
	{
		this.indexerBatchCallableId = indexerBatchCallableId;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	protected static class IndexerBatchThreadFactory implements ThreadFactory
	{
		@Override
		public Thread newThread(final Runnable runnable)
		{
			return new RegistrableThread(runnable, "indexer-thread");
		}

	}

	protected static class IndexerBatchThreadPoolExecutor extends ThreadPoolExecutor
	{
		public IndexerBatchThreadPoolExecutor(final int threadPoolSize)
		{
			super(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
					new IndexerBatchThreadFactory());
		}

		@Override
		protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable)
		{
			return (RunnableFuture<T>) new IndexerBatchFuture((SnIndexerBatchCallable) callable);
		}

		@Override
		protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value)
		{
			throw new UnsupportedOperationException();
		}
	}

	protected static class IndexerBatchFuture extends FutureTask<SnIndexerBatchResponse>
	{
		private final SnIndexerBatchCallable indexerBatchCallable;

		public IndexerBatchFuture(final SnIndexerBatchCallable indexerBatchCallable)
		{
			super(indexerBatchCallable);
			this.indexerBatchCallable = indexerBatchCallable;
		}

		public SnIndexerBatchCallable getIndexerBatchCallable()
		{
			return indexerBatchCallable;
		}
	}

	protected static class IndexerTracker
	{
		protected static final double PROGRESS_MULTIPLIER = 100D;

		private final SnProgressTracker progressTracker;
		private final int totalItems;
		private int processedItems;
		private final int maxRetries;
		private int retriesLeft;

		public IndexerTracker(final SnProgressTracker progressTracker, final int totalItems, final int maxRetries)
		{
			this.progressTracker = progressTracker;
			this.totalItems = totalItems;
			this.processedItems = 0;
			this.maxRetries = maxRetries;
			this.retriesLeft = maxRetries;
		}

		public int getTotalItems()
		{
			return totalItems;
		}

		public int getProcessedItems()
		{
			return processedItems;
		}

		public void incrementProcessedItems(final int increment)
		{
			processedItems = processedItems + increment;

			if (progressTracker != null)
			{
				progressTracker.setProgress(((double) processedItems / (double) totalItems) * PROGRESS_MULTIPLIER);
			}
		}

		public int getMaxRetries()
		{
			return maxRetries;
		}

		public int getRetriesLeft()
		{
			return retriesLeft;
		}

		public int getAndDecrementRetriesLeft()
		{
			return retriesLeft--;
		}
	}

	protected static class IndexerBatchTracker
	{
		private final IndexerBatch indexerBatch;
		private final SnIndexerBatchCallable indexerBatchCallable;
		private final int maxRetries;
		private int retriesLeft;

		public IndexerBatchTracker(final IndexerBatch indexerBatch, final SnIndexerBatchCallable indexerBatchCallable,
				final int maxRetries)
		{
			this.indexerBatch = indexerBatch;
			this.indexerBatchCallable = indexerBatchCallable;
			this.maxRetries = maxRetries;
			this.retriesLeft = maxRetries;
		}

		public String getIndexerBatchId()
		{
			return indexerBatch.getIndexerBatchId();
		}

		public IndexerBatch getIndexerBatch()
		{
			return indexerBatch;
		}

		public SnIndexerBatchCallable getIndexerBatchCallable()
		{
			return indexerBatchCallable;
		}

		public int getMaxRetries()
		{
			return maxRetries;
		}

		public int getRetriesLeft()
		{
			return retriesLeft;
		}

		public int getAndDecrementRetriesLeft()
		{
			return retriesLeft--;
		}
	}
}
