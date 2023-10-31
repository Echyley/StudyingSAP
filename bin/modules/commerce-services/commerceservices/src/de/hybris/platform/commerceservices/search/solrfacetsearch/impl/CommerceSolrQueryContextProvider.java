/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.impl;

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.solrfacetsearch.common.SolrQueryContextProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Commerce implementation of {@link SolrQueryContextProvider}.
 */
public class CommerceSolrQueryContextProvider implements SolrQueryContextProvider
{
	protected static final List<String> QUERY_CONTEXTS = Arrays.stream(SearchQueryContext.values()).map(SearchQueryContext::name)
			.collect(Collectors.toUnmodifiableList());

	@Override
	public List<String> getQueryContexts()
	{
		return QUERY_CONTEXTS;
	}
}
