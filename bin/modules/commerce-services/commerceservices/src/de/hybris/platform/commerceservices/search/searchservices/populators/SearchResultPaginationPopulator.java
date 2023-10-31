/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.searchservices.data.SnSearchResultConverterData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSearchResult;


/**
 * Populates search result pagination settings
 */
public class SearchResultPaginationPopulator implements
		Populator<SnSearchResultConverterData<SnSearchQuery, SnSearchResult>, ProductCategorySearchPageData<SolrSearchQueryData, SnSearchResult, CategoryModel>>
{
	@Override
	public void populate(final SnSearchResultConverterData<SnSearchQuery, SnSearchResult> source,
			final ProductCategorySearchPageData<SolrSearchQueryData, SnSearchResult, CategoryModel> target)
	{
		final SnSearchQuery searchQuery = source.getSnSearchQuery();
		final SnSearchResult searchResult = source.getSnSearchResult();

		if (searchResult != null)
		{
			final Integer top = searchQuery.getTop() != null ? searchQuery.getTop() : 0;
			final Integer skip = searchQuery.getSkip() != null ? searchQuery.getSkip() : 0;
			final Integer count = searchResult.getCount() != null ? searchResult.getCount() : 0;

			final PaginationData pagination = new PaginationData();
			pagination.setCurrentPage(top <= 0 ? 0 : (skip / top));
			pagination.setNumberOfPages(top <= 0 ? 0 : ((count + top - 1) / top));
			pagination.setPageSize(top);
			pagination.setTotalNumberOfResults(count);

			if (searchResult.getSort() != null)
			{
				pagination.setSort(searchResult.getSort().getId());
			}

			target.setPagination(pagination);
		}
	}
}
