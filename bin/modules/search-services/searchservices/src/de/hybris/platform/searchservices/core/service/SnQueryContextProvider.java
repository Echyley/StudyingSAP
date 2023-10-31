/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service;

import java.util.List;


/**
 * Provider for query contexts.
 */
public interface SnQueryContextProvider
{
	/**
	 * Returns the query contexts.
	 *
	 * @return the query contexts
	 */
	List<String> getQueryContexts();
}
