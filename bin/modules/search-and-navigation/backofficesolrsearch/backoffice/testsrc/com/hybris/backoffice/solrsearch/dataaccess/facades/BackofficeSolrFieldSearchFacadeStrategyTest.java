/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.dataaccess.facades;

import com.hybris.backoffice.search.daos.ItemModelSearchDAO;
import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.backoffice.solrsearch.converters.FullTextSearchDataConverter;
import com.hybris.backoffice.solrsearch.dataaccess.BackofficeSearchQuery;
import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchService;
import com.hybris.cockpitng.search.data.AutosuggestionQueryData;
import com.hybris.cockpitng.search.data.FullTextSearchData;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.facet.FacetData;
import com.hybris.cockpitng.search.data.pageable.FullTextSearchPageable;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.pageable.PageableList;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.search.Breadcrumb;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.suggester.SolrAutoSuggestService;
import de.hybris.platform.solrfacetsearch.suggester.SolrSuggestion;
import de.hybris.platform.solrfacetsearch.suggester.exceptions.SolrAutoSuggestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeSolrFieldSearchFacadeStrategyTest {

	@Mock
	private BackofficeFacetSearchService facetSearchService;

	@Mock
	private BackofficeFacetSearchConfigService facetSearchConfigService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private ItemModelSearchDAO itemModelSearchDAO;

	@Mock
	private SolrAutoSuggestService solrAutoSuggestService;
	@Mock
	private FullTextSearchDataConverter fullTextSearchDataConverter;


	@InjectMocks
	private BackofficeSolrFieldSearchFacadeStrategy solrSearchStrategy;
	private static final String TYPE_CODE = "Product";


	@Test
	public void shouldReturnEmptyPageIfQueryDataIsNull() {
		final Pageable result = solrSearchStrategy.search(null);
		assertThat(result).isNotNull();
		assertThat(result instanceof PageableList).isTrue();
		assertThat(result.getAllResults()).isEmpty();
	}

	@Test
	public void shouldReturnCorrectPage() {
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(searchQueryData.getPageSize()).thenReturn(2);
		final Pageable result = solrSearchStrategy.search(searchQueryData);
		assertThat(result).isNotNull();
		assertThat(result.getTypeCode()).isEqualTo(TYPE_CODE);
		assertThat(result.getPageSize()).isEqualTo(2);
	}

	@Test
	public void shouldGetEmptySuggestionIfIndexTypeNotExist() {
		final AutosuggestionQueryData queryData = mock(AutosuggestionQueryData.class);
		when(queryData.getSearchType()).thenReturn(TYPE_CODE);
		when(facetSearchConfigService.getIndexedTypeModel(TYPE_CODE)).thenReturn(null);

		final Map<String, Collection<String>> result = solrSearchStrategy.getAutosuggestionsForQuery(queryData, null);

		assertThat(result).isEmpty();
	}

	@Test
	public void shouldGetExpectedSuggestions() throws SolrAutoSuggestException {
		final AutosuggestionQueryData queryData = mock(AutosuggestionQueryData.class);
		final SolrSuggestion solrSuggestion = mock(SolrSuggestion.class);
		final SolrIndexedTypeModel indexedType = mock(SolrIndexedTypeModel.class);
		final LanguageModel language = mock(LanguageModel.class);
		final String queryText = "test";
		final Map<String, Collection<String>> suggestion = new HashMap<>();
		suggestion.put("test", Arrays.asList("test1", "test2"));

		when(solrSuggestion.getSuggestions()).thenReturn(suggestion);
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		when(queryData.getSearchType()).thenReturn(TYPE_CODE);
		when(facetSearchConfigService.getIndexedTypeModel(TYPE_CODE)).thenReturn(indexedType);
		when(queryData.getQueryText()).thenReturn(queryText);
		when(solrAutoSuggestService.getAutoSuggestionsForQuery(language, indexedType, queryText)).thenReturn(solrSuggestion);

		final Map<String, Collection<String>> result = solrSearchStrategy.getAutosuggestionsForQuery(queryData, null);

		assertThat(result).isEqualTo(suggestion);
	}

	@Test
	public void shouldGetEmptyResultIfSolrReturnEmptyPK() throws FacetSearchException {
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final IndexedType indexedType = mock(IndexedType.class);
		final SearchResult searchResult = mock(SearchResult.class);
		final String autocorrection = "autocorrection";
		final Breadcrumb breadcrumb = mock(Breadcrumb.class);
		final Facet facet = mock(Facet.class);
		final List<Facet> facets = Arrays.asList(facet);
		final List<Breadcrumb> breadcrumbs = Arrays.asList(breadcrumb);
		final FacetData facetData = mock(FacetData.class);
		final Collection<FacetData> facetDatas = Arrays.asList(facetData);
		final BackofficeSearchQuery solrSearchQuery = mock(BackofficeSearchQuery.class);

		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(searchQueryData.getPageSize()).thenReturn(2);
		when(facetSearchService.createBackofficeSolrSearchQuery(searchQueryData)).thenReturn(solrSearchQuery);
		when(solrSearchQuery.getIndexedType()).thenReturn(indexedType);
		when(facetSearchService.search(solrSearchQuery)).thenReturn(searchResult);
		when(searchResult.getSpellingSuggestion()).thenReturn(autocorrection);
		when(searchResult.getBreadcrumbs()).thenReturn(breadcrumbs);
		when(searchResult.getFacets()).thenReturn(facets);
		when(searchResult.getResultPKs()).thenReturn(Collections.emptyList());
		when(fullTextSearchDataConverter.convertFacets(facets, breadcrumbs, indexedType)).thenReturn(facetDatas);

		final FullTextSearchPageable pageable = (FullTextSearchPageable) solrSearchStrategy.search(searchQueryData);
		final List searchResults = pageable.getCurrentPage();
		final FullTextSearchData fullTextSearchData = pageable.getFullTextSearchData();

		assertThat(fullTextSearchData.getFacets()).isEqualTo(facetDatas);
		assertThat(fullTextSearchData.getAutocorrection()).isEqualTo(autocorrection);
		assertThat(searchResults).isEmpty();
	}

	@Test
	public void shouldGetCorrectResults() throws FacetSearchException {
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final IndexedType indexedType = mock(IndexedType.class);
		final SearchResult searchResult = mock(SearchResult.class);
		final String autocorrection = "autocorrection";
		final Breadcrumb breadcrumb = mock(Breadcrumb.class);
		final Facet facet = mock(Facet.class);
		final List<Facet> facets = Arrays.asList(facet);
		final List<Breadcrumb> breadcrumbs = Arrays.asList(breadcrumb);
		final FacetData facetData = mock(FacetData.class);
		final Collection<FacetData> facetDatas = Arrays.asList(facetData);
		final BackofficeSearchQuery solrSearchQuery = mock(BackofficeSearchQuery.class);
		final PK pk = PK.createUUIDPK(10);
		final List<PK> pks = Arrays.asList(pk);
		final ItemModel itemModel = mock(ItemModel.class);
		final List<ItemModel> itemModels = Arrays.asList(itemModel);

		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(searchQueryData.getPageSize()).thenReturn(2);
		when(facetSearchService.createBackofficeSolrSearchQuery(searchQueryData)).thenReturn(solrSearchQuery);
		when(solrSearchQuery.getIndexedType()).thenReturn(indexedType);
		when(facetSearchService.search(solrSearchQuery)).thenReturn(searchResult);
		when(searchResult.getSpellingSuggestion()).thenReturn(autocorrection);
		when(searchResult.getBreadcrumbs()).thenReturn(breadcrumbs);
		when(searchResult.getFacets()).thenReturn(facets);

		when(searchResult.getResultPKs()).thenReturn(pks);
		when(searchResult.getOffset()).thenReturn(1);
		when(searchResult.getNumberOfResults()).thenReturn(Long.valueOf(123));
		when(itemModelSearchDAO.findAll(eq(TYPE_CODE), anyList())).thenReturn(itemModels);
		when(fullTextSearchDataConverter.convertFacets(facets, breadcrumbs, indexedType)).thenReturn(facetDatas);

		final FullTextSearchPageable pageable = (FullTextSearchPageable) solrSearchStrategy.search(searchQueryData);
		final List searchResults = pageable.getCurrentPage();
		final FullTextSearchData fullTextSearchData = pageable.getFullTextSearchData();

		assertThat(fullTextSearchData.getFacets()).isEqualTo(facetDatas);
		assertThat(fullTextSearchData.getAutocorrection()).isEqualTo(autocorrection);
		assertThat(searchResults).isEqualTo(itemModels);
		assertThat(pageable.getTotalCount()).isEqualTo(123);
	}

}
