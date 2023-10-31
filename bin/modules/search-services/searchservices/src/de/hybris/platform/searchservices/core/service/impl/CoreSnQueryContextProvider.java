/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.searchservices.core.service.SnQueryContextProvider;

import java.util.List;


/**
 * Default implementation of {@link SnQueryContextProvider}.
 */
public class CoreSnQueryContextProvider implements SnQueryContextProvider
{
	protected static final List<String> QUERY_CONTEXTS = List.of("DEFAULT");

	@Override
	public List<String> getQueryContexts()
	{
		return QUERY_CONTEXTS;
	}
}
