/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.platform.searchservices.search.data.SnSearchQuery;

import org.junit.Test;

import com.hybris.cockpitng.search.data.SearchQueryData;


public class SearchQueryPaginationPopulatorTest
{

	private static final int PAGE_SIZE = 10;
	private static final int OFFSET = 1;
	private final SearchQueryPaginationPopulator searchQueryPaginationPopulator = new SearchQueryPaginationPopulator();

	@Test
	public void shouldSetQueryPaginationWhenGetSearchQueryData()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final SnSearchQueryConverterData snSearchQueryConverterData = new SnSearchQueryConverterData(searchQueryData, PAGE_SIZE, OFFSET);
		final SnSearchQuery snSearchQuery = new SnSearchQuery();

		searchQueryPaginationPopulator.populate(snSearchQueryConverterData, snSearchQuery);

		assertThat(snSearchQuery.getLimit()).isEqualTo(PAGE_SIZE);
		assertThat(snSearchQuery.getOffset()).isEqualTo(OFFSET);
	}
}
