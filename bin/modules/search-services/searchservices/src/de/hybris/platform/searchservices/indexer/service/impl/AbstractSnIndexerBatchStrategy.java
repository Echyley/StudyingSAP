/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnListenerFactory;
import de.hybris.platform.searchservices.core.service.SnSessionService;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContextFactory;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchListener;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchStrategy;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;


/**
 * Base class for {@link SnIndexerBatchStrategy} implementations.
 */
public abstract class AbstractSnIndexerBatchStrategy implements SnIndexerBatchStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSnIndexerBatchStrategy.class);

	private SnSessionService snSessionService;
	private SnIndexerBatchContextFactory snIndexerBatchContextFactory;
	private SnListenerFactory snListenerFactory;

	@Override
	public SnIndexerBatchResponse execute(final SnIndexerBatchRequest indexerBatchRequest)
			throws SnIndexerException, InterruptedException
	{
		LOG.debug("Indexer batch operation {} started", indexerBatchRequest.getIndexerBatchId());

		SnIndexerBatchContext indexerBatchContext = null;
		List<SnIndexerBatchListener> listeners = null;

		try
		{
			snSessionService.initializeSession();

			indexerBatchContext = snIndexerBatchContextFactory.createIndexerBatchContext(indexerBatchRequest);
			listeners = snListenerFactory.getListeners(indexerBatchContext, SnIndexerBatchListener.class);
			snSessionService.updateSessionForContext(indexerBatchContext);

			// aborts current indexer batch if cancellation was requested
			if (Thread.interrupted() || (indexerBatchRequest.getProgressTracker() != null
					&& indexerBatchRequest.getProgressTracker().isCancellationRequested()))
			{
				return createIndexerBatchResponse(indexerBatchContext, 0, 0, SnIndexerOperationStatus.ABORTED, null, null);
			}

			executeBeforeIndexBatchListeners(indexerBatchContext, listeners);

			SnIndexerBatchResponse indexerBatchResponse;

			if (CollectionUtils.isEmpty(indexerBatchContext.getIndexerItemSourceOperations()))
			{
				indexerBatchResponse = createIndexerBatchResponse(indexerBatchContext, 0, 0, SnIndexerOperationStatus.COMPLETED, null,
						null);
			}
			else
			{
				indexerBatchResponse = doExecute(indexerBatchContext, indexerBatchRequest.getIndexerBatchId());
			}

			indexerBatchContext.setIndexerResponse(indexerBatchResponse);
			indexerBatchContext.setIndexerBatchResponse(indexerBatchResponse);

			executeAfterIndexBatchListeners(indexerBatchContext, listeners);

			LOG.debug("Indexer batch operation {} finished", indexerBatchRequest.getIndexerBatchId());

			return indexerBatchResponse;
		}
		catch (final SnException | RuntimeException e)
		{
			if (indexerBatchContext != null)
			{
				indexerBatchContext.addException(e);
				executeAfterIndexBatchErrorListeners(indexerBatchContext, listeners);
			}

			LOG.debug("Indexer batch operation {} failed", indexerBatchRequest.getIndexerBatchId());

			throw new SnIndexerException(
					MessageFormat.format("Indexer batch operation {0} failed", indexerBatchRequest.getIndexerBatchId()), e);
		}
		finally
		{
			snSessionService.destroySession();
		}
	}

	protected void executeBeforeIndexBatchListeners(final SnIndexerBatchContext indexerBatchContext,
			final List<SnIndexerBatchListener> listeners) throws SnException
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerBatchListener listener : listeners)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Running {}.beforeIndexBatch ...", listener.getClass().getCanonicalName());
			}

			listener.beforeIndexBatch(indexerBatchContext);
		}
	}

	protected void executeAfterIndexBatchListeners(final SnIndexerBatchContext indexerBatchContext,
			final List<SnIndexerBatchListener> listeners) throws SnException
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerBatchListener listener : Lists.reverse(listeners))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Running {}.afterIndexBatch ...", listener.getClass().getCanonicalName());
			}

			listener.afterIndexBatch(indexerBatchContext);
		}
	}

	protected void executeAfterIndexBatchErrorListeners(final SnIndexerBatchContext indexerBatchContext,
			final List<SnIndexerBatchListener> listeners)
	{
		if (CollectionUtils.isEmpty(listeners))
		{
			return;
		}

		for (final SnIndexerBatchListener listener : Lists.reverse(listeners))
		{
			try
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Running {}.afterIndexBatchError ...", listener.getClass().getCanonicalName());
				}

				listener.afterIndexBatchError(indexerBatchContext);
			}
			catch (final SnIndexerException | RuntimeException exception)
			{
				indexerBatchContext.addException(exception);
			}
		}
	}

	protected abstract SnIndexerBatchResponse doExecute(final SnIndexerBatchContext indexerBatchContext,
			final String indexerBatchId) throws SnException, InterruptedException;

	protected SnIndexerBatchResponse createIndexerBatchResponse(final SnIndexerBatchContext indexerBatchContext,
			final Integer totalItems, final Integer processedItems, final SnIndexerOperationStatus status,
			final SnDocumentBatchRequest documentBatchRequest, final SnDocumentBatchResponse documentBatchResponse)
	{
		final DefaultSnIndexerBatchResponse indexerBatchResponse = new DefaultSnIndexerBatchResponse(
				indexerBatchContext.getIndexConfiguration(), indexerBatchContext.getIndexType());
		indexerBatchResponse.setTotalItems(totalItems);
		indexerBatchResponse.setProcessedItems(processedItems);
		indexerBatchResponse.setStatus(status);
		indexerBatchResponse.setDocumentBatchRequest(documentBatchRequest);
		indexerBatchResponse.setDocumentBatchResponse(documentBatchResponse);

		return indexerBatchResponse;
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

	public SnIndexerBatchContextFactory getSnIndexerBatchContextFactory()
	{
		return snIndexerBatchContextFactory;
	}

	@Required
	public void setSnIndexerBatchContextFactory(final SnIndexerBatchContextFactory snIndexerBatchContextFactory)
	{
		this.snIndexerBatchContextFactory = snIndexerBatchContextFactory;
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
}
