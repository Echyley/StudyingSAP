/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.service;

import de.hybris.platform.searchservices.search.SnSearchException;


/**
 * Implementations of this interface are responsible for creating instances of {@link SnSearchContext}.
 */
public interface SnSearchContextFactory
{
	/**
	 * Creates a new instance of {@link SnSearchContext}.
	 *
	 * @param searchRequest
	 *           - the search request
	 *
	 * @return the new instance of {@link SnSearchContext}
	 *
	 * @throws SnSearchException
	 *            if an error occurs
	 */
	SnSearchContext createSearchContext(SnSearchRequest searchRequest) throws SnSearchException;
}
