/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.search.impl.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FacetSearchQueryFacetsPopulatorTest
{
	public static final String FIELD = "field";
	public static final String TRANSLATED_FIELD = "translatedField";

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private FacetSearchQueryFacetsPopulator facetSearchQueryFacetsPopulator;
	private SearchQueryConverterData searchQueryConverterData;

	@Before
	public void setUp()
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		facetSearchQueryFacetsPopulator = new FacetSearchQueryFacetsPopulator();
		facetSearchQueryFacetsPopulator.setFieldNameTranslator(fieldNameTranslator);
		facetSearchQueryFacetsPopulator.setConfigurationService(configurationService);

		searchQueryConverterData = new SearchQueryConverterData();
		searchQueryConverterData.setSearchQuery(searchQuery);

		when(fieldNameTranslator.translate(searchQuery, FIELD, FieldType.INDEX)).thenReturn(TRANSLATED_FIELD);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(FacetSearchQueryFacetsPopulator.FACET_MIN_COUNT_PROPERTY,
				FacetSearchQueryFacetsPopulator.FACET_MIN_COUNT_DEFAULT_VALUE))
						.thenReturn(FacetSearchQueryFacetsPopulator.FACET_MIN_COUNT_DEFAULT_VALUE);
		when(configuration.getInteger(FacetSearchQueryFacetsPopulator.FACET_LIMIT_PROPERTY,
				FacetSearchQueryFacetsPopulator.FACET_LIMIT_DEFAULT_VALUE))
						.thenReturn(FacetSearchQueryFacetsPopulator.FACET_LIMIT_DEFAULT_VALUE);
	}

	@Test
	public void populateWithEmptyFacets()
	{
		// given
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getParameterNamesIterator())
				.filteredOn(paramName -> paramName.startsWith(FacetSearchQueryFacetsPopulator.FACET_PARAM_NAME_PREFIX)).isEmpty();
	}

	@Test
	public void populateWithRefineFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.REFINE);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getParameterNamesIterator())
				.filteredOn(
						paramName -> paramName.startsWith(FacetSearchQueryFacetsPopulator.FACET_PARAM_NAME_PREFIX + TRANSLATED_FIELD))
				.isNotEmpty();
	}

	@Test
	public void populateWithMultiSelectAndFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.MULTISELECTAND);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getParameterNamesIterator())
				.filteredOn(
						paramName -> paramName.startsWith(FacetSearchQueryFacetsPopulator.FACET_PARAM_NAME_PREFIX + TRANSLATED_FIELD))
				.isNotEmpty();
	}

	@Test
	public void populateWithMultiSelectOrFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.MULTISELECTOR);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getParameterNamesIterator())
				.filteredOn(
						paramName -> paramName.startsWith(FacetSearchQueryFacetsPopulator.FACET_PARAM_NAME_PREFIX + TRANSLATED_FIELD))
				.isNotEmpty();
	}
}
