/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public abstract class AbstractSearchQueryPerfTest extends AbstractIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(AbstractSearchQueryPerfTest.class);

	protected static final String TEST_CATALOG = "test";
	protected static final String TEST_CATALOG_VERSION = "default";

	public static final String PRODUCT_PREFIX = "product";

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchQueryPerfTest.csv");
	}

	protected void updateIndexType(final String indexedPropertyPrefix, final int indexedPropertyCount,
			final Consumer<SolrIndexedPropertyModel> action) throws Exception
	{
		LOG.info("Update index type started ...");

		final SolrIndexedTypeModel indexedType = getIndexedTypeModel();
		final List<SolrIndexedPropertyModel> indexedProperties = new ArrayList<>(indexedType.getSolrIndexedProperties());

		for (int index = 0; index < indexedPropertyCount; index++)
		{
			final SolrIndexedPropertyModel indexedProperty = new SolrIndexedPropertyModel();
			indexedProperty.setName(indexedPropertyPrefix + index);

			action.accept(indexedProperty);

			indexedProperties.add(indexedProperty);
		}

		indexedType.setSolrIndexedProperties(indexedProperties);

		modelService.save(indexedType);

		LOG.info("Update index type finished ...");
	}

	protected void createProducts(final int productCount, final int batchSize) throws Exception
	{
		LOG.info("Create products started ...");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_CATALOG, TEST_CATALOG_VERSION);
		int productIndex = 0;

		for (int batchIndex = 0; productIndex < productCount; batchIndex++)
		{
			final List<ProductModel> products = new ArrayList<>();

			LOG.info("Processing batch " + batchIndex + " ...");

			for (int requestIndex = 0; requestIndex < batchSize && productIndex < productCount; requestIndex++, productIndex++)
			{
				final ProductModel product = new ProductModel();
				product.setCode(PRODUCT_PREFIX + productIndex);
				product.setCatalogVersion(catalogVersion);

				products.add(product);
			}

			modelService.saveAll(products);
		}

		LOG.info("Create products finished ...");
	}

	protected void indexProducts() throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();

		indexerService.performFullIndex(facetSearchConfig);
	}

	protected SearchResult executeSearchQuery(final Consumer<SearchQuery> action) throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		action.accept(searchQuery);

		return facetSearchService.search(searchQuery);
	}

	protected void assertDocumentField(final Object expectedValue, final Document document, final String field)
	{
		assertEquals(expectedValue, document.getFields().get(field));
	}
}
