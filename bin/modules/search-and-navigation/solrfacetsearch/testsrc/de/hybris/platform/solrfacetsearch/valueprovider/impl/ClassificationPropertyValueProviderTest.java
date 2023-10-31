/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.valueprovider.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Test;


public class ClassificationPropertyValueProviderTest extends AbstractIntegrationTest
{
	private static final String COMPACT_EN = "Digital compact camera";
	private static final String MIRROR_EN = "Digital SLR";

	private static final String COMPACT_DE = "Digitale Kompaktkamera";
	private static final String MIRROR_DE = "Digitale Spiegelreflexkamera";

	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_ONLINE = "Online";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/ClassificationPropertyValueProviderTest.csv");
		importCsv("/test/solrClassificationSystemOnline.csv", "utf-8");
	}

	/**
	 * This case tests proper localization of classification attributes. Related issue: #GEN-180
	 */
	@Test
	public void testClassificationSystemAttributesInEnglish() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		indexerService.performFullIndex(facetSearchConfig);

		// when
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		query.setLanguage("en");
		query.addFacetValue("categoryName", "Photography_online_en");

		final SearchResult searchResult = facetSearchService.search(query);
		final List<String> typeNames = searchResult.getFacet("type").getFacetValues().stream().map(FacetValue::getName)
				.collect(Collectors.toList());

		assertTrue("Resulting facets has improper names", typeNames.contains(COMPACT_EN));
		assertTrue("Resulting facets has improper names", typeNames.contains(MIRROR_EN));
	}

	/**
	 * This case tests proper localization of classification attributes. Related issue: #GEN-180
	 */
	@Test
	public void testClassificationSystemAttributesInGerman() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		indexerService.performFullIndex(facetSearchConfig);

		// when
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		query.setLanguage("de");
		query.addFacetValue("categoryName", "Fotografie_online_de");

		final SearchResult searchResult = facetSearchService.search(query);
		final List<String> typeNames = searchResult.getFacet("type").getFacetValues().stream().map(FacetValue::getName)
				.collect(Collectors.toList());

		assertTrue("Resulting facets has improper names", typeNames.contains(COMPACT_DE));
		assertTrue("Resulting facets has improper names", typeNames.contains(MIRROR_DE));
	}
}
