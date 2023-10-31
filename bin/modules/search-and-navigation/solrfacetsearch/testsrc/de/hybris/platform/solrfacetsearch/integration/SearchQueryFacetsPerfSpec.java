/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.provider.impl.RandomStringValueResolver;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.assertj.core.data.Percentage;
import org.junit.Test;
import org.springframework.util.StopWatch;


@PerformanceTest
public class SearchQueryFacetsPerfSpec extends AbstractSearchQueryPerfTest
{
	private static final Logger LOG = Logger.getLogger(SearchQueryFacetsPerfSpec.class);

	protected static final int BATCH_SIZE = 1000;
	protected static final int PRODUCT_COUNT = 100000;

	protected static final String SINGLE_VALUED_FACET_PROPERTY_PREFIX = "singleValuedFacetProperty";
	protected static final int SINGLE_VALUED_FACET_COUNT = 10;

	protected static final String MULTI_VALUED_FACET_PROPERTY_PREFIX = "multiValuedFacetProperty";
	protected static final int MULTI_VALUED_FACET_COUNT = 10;
	protected static final int MULTI_VALUED_FACET_VALUE_COUNT = 25;

	protected static final int FACET_DISTINCT_VALUE_COUNT = 100;

	protected static final String GROUP_PROPERTY_PREFIX = "groupProperty";
	protected static final boolean GROUP = true;
	protected static final boolean GROUP_FACETS = true;
	protected static final int GROUP_DISTINCT_VALUE_COUNT = Math.max(1, PRODUCT_COUNT / 50);

	protected static final int SEARCH_DURATION_MILLIS = 60000;

	@Test
	public void searchQueryFacetsPerfTest() throws Exception
	{
		// setup phase

		final StopWatch setupTimer = new StopWatch("setup");
		setupTimer.start();

		createProperties();
		createProducts(PRODUCT_COUNT, BATCH_SIZE);
		indexProducts();

		setupTimer.stop();

		// search phase

		final List<FacetField> facets = buildSearchQueryFacets();
		final SearchResult searchResult = executeSearchQuery(facets);

		final StopWatch queryTimer = new StopWatch("search");
		queryTimer.start();

		int searchQueryCount = 0;

		for (final long startTime = System.currentTimeMillis(); System.currentTimeMillis()
				- startTime < SEARCH_DURATION_MILLIS; searchQueryCount++)
		{
			executeSearchQuery(facets);
		}

		queryTimer.stop();

		final float totalSetupTimeSeconds = ((float) setupTimer.getTotalTimeMillis()) / 1000;
		final float totalSearchTimeSeconds = ((float) queryTimer.getTotalTimeMillis()) / 1000;
		final float searchQueriesPerSecond = (searchQueryCount) / (((float) queryTimer.getTotalTimeMillis()) / 1000);
		final float avgTimePerSearchQuery = searchQueryCount == 0 ? 0
				: ((float) queryTimer.getTotalTimeMillis()) / searchQueryCount;

		LOG.info("**************************************************");
		LOG.info("productCount: " + PRODUCT_COUNT);
		LOG.info("");
		LOG.info("singleValuedFacetCount: " + SINGLE_VALUED_FACET_COUNT);
		LOG.info("multiValuedFacetCount: " + MULTI_VALUED_FACET_COUNT);
		LOG.info("multiValuedFacetValueCount: " + MULTI_VALUED_FACET_VALUE_COUNT);
		LOG.info("facetDistinctValueCount: " + FACET_DISTINCT_VALUE_COUNT);
		LOG.info("");
		LOG.info("group: " + GROUP);
		LOG.info("groupFacets: " + GROUP_FACETS);
		LOG.info("distinctGroupCount: " + GROUP_DISTINCT_VALUE_COUNT);
		LOG.info("");
		LOG.info("totalSetupTimeSeconds(s): " + totalSetupTimeSeconds);
		LOG.info("totalSearchTimeSeconds(s): " + totalSearchTimeSeconds);
		LOG.info("searchQueryCount: " + searchQueryCount);
		LOG.info("searchQueries/s: " + searchQueriesPerSecond);
		LOG.info("avgTimePerSearchQuery(ms): " + avgTimePerSearchQuery);
		LOG.info("**************************************************");

		if (GROUP)
		{
			assertThat(searchResult.getNumberOfResults()).isCloseTo(GROUP_DISTINCT_VALUE_COUNT, Percentage.withPercentage(1));
		}
		else
		{
			assertThat(searchResult.getNumberOfResults()).isEqualTo(PRODUCT_COUNT);
		}

		assertThat(searchResult.getFacets()).hasSize(SINGLE_VALUED_FACET_COUNT + MULTI_VALUED_FACET_COUNT);
	}

	protected void createProperties() throws Exception
	{
		updateIndexType(SINGLE_VALUED_FACET_PROPERTY_PREFIX, SINGLE_VALUED_FACET_COUNT, indexedProperty -> {
			indexedProperty.setType(SolrPropertiesTypes.STRING);
			indexedProperty.setMultiValue(false);
			indexedProperty.setFieldValueProvider(RandomStringValueResolver.ID);
			indexedProperty.setValueProviderParameters(Map.ofEntries( //
					entry(RandomStringValueResolver.VALUE_COUNT_PARAM, String.valueOf(1)),
					entry(RandomStringValueResolver.DISTINCT_VALUE_COUNT_PARAM, String.valueOf(FACET_DISTINCT_VALUE_COUNT))));
		});
		updateIndexType(MULTI_VALUED_FACET_PROPERTY_PREFIX, MULTI_VALUED_FACET_COUNT, indexedProperty -> {
			indexedProperty.setType(SolrPropertiesTypes.STRING);
			indexedProperty.setMultiValue(true);
			indexedProperty.setFieldValueProvider(RandomStringValueResolver.ID);
			indexedProperty.setValueProviderParameters(Map.ofEntries( //
					entry(RandomStringValueResolver.VALUE_COUNT_PARAM, String.valueOf(MULTI_VALUED_FACET_VALUE_COUNT)),
					entry(RandomStringValueResolver.DISTINCT_VALUE_COUNT_PARAM, String.valueOf(FACET_DISTINCT_VALUE_COUNT))));
		});

		if (GROUP)
		{
			updateIndexType(GROUP_PROPERTY_PREFIX, 1, indexedProperty -> {
				indexedProperty.setType(SolrPropertiesTypes.STRING);
				indexedProperty.setMultiValue(false);
				indexedProperty.setFieldValueProvider(RandomStringValueResolver.ID);
				indexedProperty.setValueProviderParameters(Map.ofEntries( //
						entry(RandomStringValueResolver.VALUE_COUNT_PARAM, String.valueOf(1)),
						entry(RandomStringValueResolver.DISTINCT_VALUE_COUNT_PARAM, String.valueOf(GROUP_DISTINCT_VALUE_COUNT))));
			});
		}
	}

	protected List<FacetField> buildSearchQueryFacets()
	{
		final List<FacetField> facets = new ArrayList<>();

		for (int facetIndex = 0; facetIndex < SINGLE_VALUED_FACET_COUNT; facetIndex++)
		{
			facets.add(new FacetField(SINGLE_VALUED_FACET_PROPERTY_PREFIX + facetIndex));
		}

		for (int facetIndex = 0; facetIndex < MULTI_VALUED_FACET_COUNT; facetIndex++)
		{
			facets.add(new FacetField(MULTI_VALUED_FACET_PROPERTY_PREFIX + facetIndex));
		}

		return facets;
	}

	protected SearchResult executeSearchQuery(final List<FacetField> facets) throws Exception
	{
		return executeSearchQuery(searchQuery -> {
			searchQuery.getFacets().addAll(facets);

			if (GROUP)
			{
				searchQuery.addGroupCommand(GROUP_PROPERTY_PREFIX + 0);
				searchQuery.setGroupFacets(GROUP_FACETS);
			}
		});
	}
}
