/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.service;

import de.hybris.platform.searchservices.search.SnSearchException;


/**
 * Listener for search operations.
 */
public interface SnSearchListener
{
	/**
	 * Handles a notification that the search operation is about to begin execution.
	 *
	 * @param context
	 *           - the search context
	 *
	 * @throws SnSearchException
	 *            if an error occurs
	 */
	default void beforeSearch(final SnSearchContext context) throws SnSearchException
	{
	}

	/**
	 * Handles a notification that the search operation has just completed.
	 *
	 * @param context
	 *           - the search context
	 *
	 * @throws SnSearchException
	 *            if an error occurs
	 */
	default void afterSearch(final SnSearchContext context) throws SnSearchException
	{
	}

	/**
	 * Handles a notification that the search operation failed (this may also be due to listeners failing).
	 *
	 * @param context
	 *           - the search context
	 *
	 * @throws SnSearchException
	 *            if an error occurs
	 */
	default void afterSearchError(final SnSearchContext context) throws SnSearchException
	{
	}
}
