/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.indexer.SnIndexerException;


/**
 * Listener for indexer batch operations.
 */
public interface SnIndexerBatchListener
{
	/**
	 * Handles a notification that the indexer batch operation is about to begin execution.
	 *
	 * @param context
	 *           - the indexer context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 *
	 * @deprecated Replaced by {@link #beforeIndexBatch(SnIndexerBatchContext)}.
	 */
	@Deprecated(since = "2211")
	default void beforeIndexBatch(final SnIndexerContext context) throws SnIndexerException
	{
	}

	/**
	 * Handles a notification that the indexer batch operation is about to begin execution.
	 *
	 * @param context
	 *           - the indexer batch context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	default void beforeIndexBatch(final SnIndexerBatchContext context) throws SnIndexerException
	{
		beforeIndexBatch((SnIndexerContext) context);
	}

	/**
	 * Handles a notification that the indexer batch operation has just completed.
	 *
	 * @param context
	 *           - the indexer context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 *
	 * @deprecated Replaced by {@link #afterIndexBatch(SnIndexerBatchContext)}.
	 */
	@Deprecated(since = "2211")
	default void afterIndexBatch(final SnIndexerContext context) throws SnIndexerException
	{
	}

	/**
	 * Handles a notification that the indexer batch operation has just completed.
	 *
	 * @param context
	 *           - the indexer batch context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	default void afterIndexBatch(final SnIndexerBatchContext context) throws SnIndexerException
	{
		afterIndexBatch((SnIndexerContext) context);
	}

	/**
	 * Handles a notification that the indexer batch operation failed (this may also be due to listeners failing).
	 *
	 * @param context
	 *           - the indexer context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 *
	 * @deprecated Replaced by {@link #afterIndexBatchError(SnIndexerBatchContext)}.
	 */
	@Deprecated(since = "2211")
	default void afterIndexBatchError(final SnIndexerContext context) throws SnIndexerException
	{
	}

	/**
	 * Handles a notification that the indexer batch operation failed (this may also be due to listeners failing).
	 *
	 * @param context
	 *           - the indexer context
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	default void afterIndexBatchError(final SnIndexerBatchContext context) throws SnIndexerException
	{
		afterIndexBatchError((SnIndexerContext) context);
	}
}
