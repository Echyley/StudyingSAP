/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.impl;

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.searchservices.core.service.SnQueryContextProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Commerce implementation of {@link SnQueryContextProvider}.
 */
public class CommerceSnQueryContextProvider implements SnQueryContextProvider
{
	protected static final List<String> QUERY_CONTEXTS = Arrays.stream(SearchQueryContext.values()).map(SearchQueryContext::name)
			.collect(Collectors.toUnmodifiableList());

	@Override
	public List<String> getQueryContexts()
	{
		return QUERY_CONTEXTS;
	}
}
