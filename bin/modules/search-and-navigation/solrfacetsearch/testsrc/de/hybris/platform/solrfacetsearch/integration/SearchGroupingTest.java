/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SearchResultGroup;
import de.hybris.platform.solrfacetsearch.search.SearchResultGroupCommand;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchGroupingTest extends AbstractIntegrationTest
{
	public static final String GROUP_FIELD = "manufacturerName";
	public static final String MANUFACTURER_A = "manA";
	public static final String MANUFACTURER_B = "manB";
	public static final String MANUFACTURER_C = "manC";

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchGroupingTest.csv");
	}

	@Test
	public void testWithoutGrouping() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.addSort(GROUP_FIELD);

		final FacetField facetField = new FacetField(GROUP_FIELD);
		facetField.setPriority(100);
		searchQuery.addFacet(facetField);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(6, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(GROUP_FIELD);
		assertNotNull("Facet not found: " + GROUP_FIELD, facet);
		assertThat(facet.getFacetValues()).contains(new FacetValue(MANUFACTURER_A, 3, false),
				new FacetValue(MANUFACTURER_B, 2, false));
	}

	@Test
	public void testWithGrouping() throws Exception
	{
		// given
		importConfig("/test/integration/SearchGroupingTest_enableGrouping.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.addSort(GROUP_FIELD);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(3, searchResult.getNumberOfResults());

		final SearchResultGroupCommand groupCommand = searchResult.getGroupCommandResult();
		assertEquals(GROUP_FIELD, groupCommand.getName());
		assertEquals(3, groupCommand.getNumberOfGroups());
		assertEquals(6, groupCommand.getNumberOfMatches());
		assertThat(groupCommand.getGroups()).hasSize(3);

		final SearchResultGroup group1 = groupCommand.getGroups().get(0);
		assertEquals(MANUFACTURER_A, group1.getGroupValue());
		assertThat(group1.getDocuments()).hasSize(3);

		final SearchResultGroup group2 = groupCommand.getGroups().get(1);
		assertEquals(MANUFACTURER_B, group2.getGroupValue());
		assertThat(group2.getDocuments()).hasSize(2);

		final SearchResultGroup group3 = groupCommand.getGroups().get(2);
		assertNull(group3.getGroupValue());
		assertThat(group3.getDocuments()).hasSize(1);
	}

	@Test
	public void testWithGroupingAndGroupLimit() throws Exception
	{
		// given
		importConfig("/test/integration/SearchGroupingTest_enableGrouping.csv");
		importConfig("/test/integration/SearchGroupingTest_setGroupLimitTo1.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.addSort(GROUP_FIELD);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(3, searchResult.getNumberOfResults());

		final SearchResultGroupCommand groupCommand = searchResult.getGroupCommandResult();
		assertEquals(GROUP_FIELD, groupCommand.getName());
		assertEquals(3, groupCommand.getNumberOfGroups());
		assertEquals(6, groupCommand.getNumberOfMatches());
		assertThat(groupCommand.getGroups()).hasSize(3);

		final SearchResultGroup group1 = groupCommand.getGroups().get(0);
		assertEquals(MANUFACTURER_A, group1.getGroupValue());
		assertThat(group1.getDocuments()).hasSize(1);

		final SearchResultGroup group2 = groupCommand.getGroups().get(1);
		assertEquals(MANUFACTURER_B, group2.getGroupValue());
		assertThat(group2.getDocuments()).hasSize(1);

		final SearchResultGroup group3 = groupCommand.getGroups().get(2);
		assertNull(group3.getGroupValue());
		assertThat(group3.getDocuments()).hasSize(1);
	}

	@Test
	public void testWithGroupingAndGroupFacets() throws Exception
	{
		// given
		importConfig("/test/integration/SearchGroupingTest_enableGrouping.csv");
		importConfig("/test/integration/SearchGroupingTest_enableGroupFacets.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.addSort(GROUP_FIELD);

		final FacetField facetField = new FacetField(GROUP_FIELD);
		facetField.setPriority(100);
		searchQuery.addFacet(facetField);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(3, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(GROUP_FIELD);
		assertNotNull("Facet not found: " + GROUP_FIELD, facet);
		assertThat(facet.getFacetValues()).contains(new FacetValue(MANUFACTURER_A, 1, false),
				new FacetValue(MANUFACTURER_B, 1, false));
	}
}
