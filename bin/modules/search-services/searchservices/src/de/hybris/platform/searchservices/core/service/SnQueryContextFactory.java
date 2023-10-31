/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service;

import java.util.List;


/**
 * Collects the query contexts from the {@link SnQueryContextProvider} implementations.
 */
public interface SnQueryContextFactory
{
	/**
	 * Returns the query contexts.
	 *
	 * @return the query contexts
	 */
	List<String> getQueryContexts();
}
