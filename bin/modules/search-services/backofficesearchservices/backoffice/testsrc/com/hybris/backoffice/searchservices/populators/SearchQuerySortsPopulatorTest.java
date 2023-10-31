/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSort;
import de.hybris.platform.searchservices.search.data.SnSortExpression;

import java.util.List;

import org.junit.Test;

import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.SortData;


public class SearchQuerySortsPopulatorTest
{

	private static final String SORT_ATTR = "SORT_ATTR";
	private final SearchQuerySortsPopulator searchQuerySortsPopulator = new SearchQuerySortsPopulator();

	@Test
	public void shouldSetQueryTextWhenSearchQueryDataIsNotNull()
	{
		final SortData sortData = new SortData(SORT_ATTR, true);
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSortData()).thenReturn(sortData);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, 10, 1);
		final SnSearchQuery snSearchQuery = new SnSearchQuery();
		searchQuerySortsPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		final SnSort snSort = snSearchQuery.getSort();
		assertThat(snSort).isNotNull();
		assertThat(snSort.getId()).isEqualTo(SORT_ATTR);
		assertThat(snSort.getName()).isEqualTo(SORT_ATTR);
		final List<SnSortExpression> expressions = snSort.getExpressions();
		assertThat(expressions).hasSize(1);
		assertThat(expressions.get(0).getExpression()).isEqualTo(SORT_ATTR);
		assertThat(expressions.get(0).getAscending()).isTrue();

	}
}
