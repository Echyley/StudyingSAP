/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.indexer.SnIndexerException;


/**
 * Implementations of this interface are responsible for creating instances of {@link SnIndexerContext}.
 */
public interface SnIndexerContextFactory
{
	/**
	 * Creates a new instance of {@link SnIndexerContext}.
	 *
	 * @param indexerRequest
	 *           - the indexer request
	 *
	 * @return the new instance of {@link SnIndexerContext}
	 *
	 * @throws SnIndexerException
	 *            if an error occurs
	 */
	SnIndexerContext createIndexerContext(SnIndexerRequest indexerRequest) throws SnIndexerException;
}
