/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.dataaccess.facades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.searchservices.search.SnSearchException;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSearchResult;
import de.hybris.platform.searchservices.search.service.SnSearchRequest;
import de.hybris.platform.searchservices.search.service.SnSearchResponse;
import de.hybris.platform.searchservices.search.service.SnSearchService;
import de.hybris.platform.searchservices.suggest.SnSuggestException;
import de.hybris.platform.searchservices.suggest.data.SnSuggestHit;
import de.hybris.platform.searchservices.suggest.data.SnSuggestResult;
import de.hybris.platform.searchservices.suggest.service.SnSuggestRequest;
import de.hybris.platform.searchservices.suggest.service.SnSuggestResponse;
import de.hybris.platform.searchservices.suggest.service.SnSuggestService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

import com.hybris.backoffice.search.dataaccess.facades.AbstractBackofficeSearchPageable;
import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.backoffice.searchservices.populators.SnSearchQueryConverterData;
import com.hybris.backoffice.searchservices.populators.SnSearchResultConverterData;
import com.hybris.backoffice.searchservices.populators.SnSearchResultSourceData;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.search.data.AutosuggestionQueryData;
import com.hybris.cockpitng.search.data.FullTextSearchData;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.pageable.PageableList;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSearchServicesFieldSearchFacadeStrategyTest
{
	@Mock
	private SnSearchService snSearchService;

	@Mock
	private SnSuggestService snSuggestService;

	@Mock
	private BackofficeFacetSearchConfigService facetSearchConfigService;

	@Mock
	private Converter<SnSearchQueryConverterData, SnSearchQuery> searchQueryConverter;

	@Mock
	private Converter<SnSearchResultSourceData, SnSearchResultConverterData> searchResultConverter;

	@InjectMocks
	private DefaultSearchServicesFieldSearchFacadeStrategy defaultSearchServicesFieldSearchFacadeStrategy;

	private static final String TYPE_CODE = "testTypeCode";
	private static final String QUERY_TEXT = "testQueryText";
	private static final String INDEX_TYPE_ID = "testIndexTypeId";
	private static final int PAGE_SIZE = 10;
	private static final String MODEL_ID = "1";

	@Before
	public void setup()
	{
		defaultSearchServicesFieldSearchFacadeStrategy.setSearchQueryConverter(searchQueryConverter);
		defaultSearchServicesFieldSearchFacadeStrategy.setSearchResultConverter(searchResultConverter);
	}

	@Test
	public void shouldGetEmptyPageableWhenQueryDataIsNull()
	{
		final Pageable<PageableList> result = defaultSearchServicesFieldSearchFacadeStrategy.search(null);
		assertThat(result.getAllResults()).isEmpty();
	}

	@Test
	public void shouldGetPageableWhenQueryDataIsNotNull()
	{
		final SearchQueryData queryData = mock(SearchQueryData.class);
		when(queryData.getSearchType()).thenReturn(TYPE_CODE);
		when(queryData.getPageSize()).thenReturn(PAGE_SIZE);

		final Pageable<?> result = defaultSearchServicesFieldSearchFacadeStrategy.search(queryData);
		assertThat(result.getTypeCode()).isEqualTo(TYPE_CODE);
		assertThat(result.getPageSize()).isEqualTo(PAGE_SIZE);
	}

	@Test
	public void shouldGetPageableItemListAfterSearch() throws SnSearchException
	{
		final SearchQueryData queryData = mock(SearchQueryData.class);
		when(queryData.getSearchType()).thenReturn(TYPE_CODE);
		when(queryData.getPageSize()).thenReturn(PAGE_SIZE);
		final SnIndexTypeModel indexTypeModel = mock(SnIndexTypeModel.class);
		when(indexTypeModel.getId()).thenReturn(INDEX_TYPE_ID);
		when(facetSearchConfigService.getIndexedTypeModel(TYPE_CODE)).thenReturn(indexTypeModel);
		final SnSearchRequest snSearchRequest = mock(SnSearchRequest.class);
		when(snSearchService.createSearchRequest(eq(INDEX_TYPE_ID), any())).thenReturn(snSearchRequest);
		final SnSearchResponse snSearchResponse = mock(SnSearchResponse.class);
		when(snSearchService.search(snSearchRequest)).thenReturn(snSearchResponse);
		final SnSearchResult snSearchResult = mock(SnSearchResult.class);
		when(snSearchResponse.getSearchResult()).thenReturn(snSearchResult);

		final SnSearchQuery snSearchQuery = new SnSearchQuery();
		when(searchQueryConverter.convert(any())).thenReturn(snSearchQuery);
		final SnSearchResultConverterData snResultData = new SnSearchResultConverterData();
		final FullTextSearchData fullTextSearchData = mock(FullTextSearchData.class);
		snResultData.setFullTextSearchData(fullTextSearchData);
		final ItemModel item = mock(ItemModel.class);
		snResultData.setItemModels(Arrays.asList(item));
		when(searchResultConverter.convert(any())).thenReturn(snResultData);

		final AbstractBackofficeSearchPageable result = (AbstractBackofficeSearchPageable) defaultSearchServicesFieldSearchFacadeStrategy
				.search(queryData);
		final List currentPage = result.getCurrentPage();
		assertThat(currentPage).hasSize(1);
		assertThat(currentPage).contains(item);
	}

	@Test
	public void shouldGetAutoSuggestionsWhenGetQueryData() throws SnSuggestException
	{
		final AutosuggestionQueryData queryData = mock(AutosuggestionQueryData.class);
		when(queryData.getQueryText()).thenReturn(QUERY_TEXT);
		when(queryData.getSearchType()).thenReturn(TYPE_CODE);

		final SnIndexTypeModel indexTypeModel = mock(SnIndexTypeModel.class);
		when(indexTypeModel.getId()).thenReturn(MODEL_ID);
		when(facetSearchConfigService.getIndexedTypeModel(TYPE_CODE)).thenReturn(indexTypeModel);

		final SnSuggestRequest snSuggestRequest = mock(SnSuggestRequest.class);
		final SnSuggestResponse snSuggestResponse = mock(SnSuggestResponse.class);
		when(snSuggestService.suggest(snSuggestRequest)).thenReturn(snSuggestResponse);
		when(snSuggestService.createSuggestRequest(eq(MODEL_ID), any())).thenReturn(snSuggestRequest);

		final SnSuggestHit hit1 = new SnSuggestHit();
		hit1.setQuery("Hit1");
		final SnSuggestHit hit2 = new SnSuggestHit();
		hit2.setQuery("Hit2");
		final List<SnSuggestHit> snSuggestHitList = Arrays.asList(hit1, hit2);
		final SnSuggestResult snSuggestResult = mock(SnSuggestResult.class);
		when(snSuggestResult.getSuggestHits()).thenReturn(snSuggestHitList);
		when(snSuggestResponse.getSuggestResult()).thenReturn(snSuggestResult);

		defaultSearchServicesFieldSearchFacadeStrategy.setFacetSearchConfigService(facetSearchConfigService);
		final Map<String, Collection<String>> autosuggestionsForQuery = defaultSearchServicesFieldSearchFacadeStrategy
				.getAutosuggestionsForQuery(queryData, mock(Context.class));
		assertThat(autosuggestionsForQuery).hasSize(1);
		assertThat(autosuggestionsForQuery.get(QUERY_TEXT.toLowerCase().trim())).isEqualTo(Arrays.asList("Hit1", "Hit2"));
	}

}
