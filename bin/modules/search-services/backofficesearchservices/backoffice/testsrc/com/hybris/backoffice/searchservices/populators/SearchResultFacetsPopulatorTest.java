/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.populators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.searchservices.search.data.AbstractSnBucketsFacetResponse;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetResponse;
import de.hybris.platform.searchservices.search.data.SnFacetFilterMode;
import de.hybris.platform.searchservices.search.data.SnSearchResult;
import de.hybris.platform.searchservices.search.data.SnTermBucketResponse;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetResponse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.searchservices.providers.impl.CategoryFacetValueDisplayNameProvider;
import com.hybris.cockpitng.i18n.CockpitLocaleService;


@RunWith(MockitoJUnitRunner.class)
public class SearchResultFacetsPopulatorTest
{
	@Mock
	private CockpitLocaleService cockpitLocaleService;

	@Mock
	private CategoryFacetValueDisplayNameProvider categoryFacetValueDisplayNameProvider;

	@InjectMocks
	private SearchResultFacetsPopulator searchResultFacetsPopulator;

	private final SnSearchResultSourceData snSearchResultSourceData = mock(SnSearchResultSourceData.class);
	private final SnSearchResultConverterData snSearchResultConverterData = mock(SnSearchResultConverterData.class);
	private static final String RES_NAME = "response_name";
	private static final String RES_ID = "1";

	@Test
	public void shouldNotSetFullTextDataWhenGotNullResponse()
	{
		final SnSearchResult snSearchResult = mock(SnSearchResult.class);
		final SnTermBucketResponse snTermBucketResponse = mock(SnTermBucketResponse.class);
		final SnTermBucketsFacetResponse facetResponse = mock(SnTermBucketsFacetResponse.class);
		final List<AbstractSnFacetResponse> facets = Arrays.asList(facetResponse);

		when(facetResponse.getFilterMode()).thenReturn(SnFacetFilterMode.REFINE);
		when(facetResponse.getId()).thenReturn(RES_ID);
		when(facetResponse.getName()).thenReturn(RES_NAME);
		when(facetResponse.getSelectedBuckets()).thenReturn(Arrays.asList(snTermBucketResponse));
		when(snSearchResultSourceData.getSnSearchResult()).thenReturn(snSearchResult);
		when(snSearchResult.getFacets()).thenReturn(facets);

		searchResultFacetsPopulator.populate(snSearchResultSourceData, snSearchResultConverterData);
		verify(snSearchResultConverterData).setFullTextSearchData(any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGotUnexpectedResponse()
	{
		final SnSearchResult snSearchResult = mock(SnSearchResult.class);
		final AbstractSnBucketsFacetResponse facetResponse = mock(AbstractSnBucketsFacetResponse.class);
		final List<AbstractSnFacetResponse> facets = Arrays.asList(facetResponse);
		when(facetResponse.getFilterMode()).thenReturn(SnFacetFilterMode.REFINE);
		when(facetResponse.getId()).thenReturn(RES_ID);
		when(facetResponse.getName()).thenReturn(RES_NAME);
		when(snSearchResultSourceData.getSnSearchResult()).thenReturn(snSearchResult);
		when(snSearchResult.getFacets()).thenReturn(facets);
		searchResultFacetsPopulator.populate(snSearchResultSourceData, snSearchResultConverterData);
	}
}
