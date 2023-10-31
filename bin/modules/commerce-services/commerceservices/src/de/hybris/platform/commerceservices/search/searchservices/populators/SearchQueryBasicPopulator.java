/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.populators;

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.searchservices.data.SnSearchQueryConverterData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;


/**
 * Populates search query
 */
public class SearchQueryBasicPopulator implements Populator<SnSearchQueryConverterData, SnSearchQuery>
{
	@Override
	public void populate(final SnSearchQueryConverterData source, final SnSearchQuery target)
	{
		final SolrSearchQueryData searchQuery = source.getSearchQuery();
		final SearchQueryContext queryContext = searchQuery.getSearchQueryContext() != null ? searchQuery.getSearchQueryContext()
				: SearchQueryContext.DEFAULT;
		final String query = searchQuery.getFreeTextSearch();

		target.getQueryContexts().add(queryContext.name());
		target.setQuery(query);
	}
}
