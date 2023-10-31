/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */

package com.hybris.backoffice.searchservices.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.searchservices.search.data.SnSearchQuery;

import org.junit.Test;

import com.hybris.cockpitng.search.data.SearchQueryData;


public class SearchQueryBasicPopulatorTest
{
	private static final String QUERY_TEXT = "queryText";
	private static final String PRE_QUERY_TEXT = "preQueryText";
	private static final int PAGE_SIZE = 10;
	private static final int OFFSET = 1;
	private final SearchQueryBasicPopulator searchQueryBasicPopulator = new SearchQueryBasicPopulator();

	@Test
	public void shouldSetQueryTextWhenSearchQueryDataContainsQueryText()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchQueryText()).thenReturn(QUERY_TEXT);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		final SnSearchQuery snSearchQuery = new SnSearchQuery();
		searchQueryBasicPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getQuery()).isEqualTo(QUERY_TEXT);
	}

	@Test
	public void shouldUpdateQueryTextWhenSearchQueryDataContainsQueryText()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchQueryText()).thenReturn(QUERY_TEXT);

		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		final SnSearchQuery snSearchQuery = new SnSearchQuery();
		snSearchQuery.setQuery(PRE_QUERY_TEXT);
		searchQueryBasicPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getQuery()).isEqualTo(QUERY_TEXT);
	}

	@Test
	public void shouldKeepQueryWhenSearchQueryDataIsNull()
	{
		final SnSearchQueryConverterData snSearchQueryConverterData = mock(SnSearchQueryConverterData.class);
		when(snSearchQueryConverterData.getSearchQueryData()).thenReturn(null);

		final SnSearchQuery snSearchQuery = new SnSearchQuery();
		snSearchQuery.setQuery(PRE_QUERY_TEXT);
		searchQueryBasicPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getQuery()).isEqualTo(PRE_QUERY_TEXT);
	}

}
