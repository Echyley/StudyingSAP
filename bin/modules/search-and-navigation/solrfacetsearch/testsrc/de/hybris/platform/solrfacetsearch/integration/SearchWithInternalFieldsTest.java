/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.solrfacetsearch.constants.SolrfacetsearchConstants;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Test;


public class SearchWithInternalFieldsTest extends AbstractSearchQueryTest
{
	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchWithInternalFieldsTest.csv");
	}

	@Test
	public void searchOnCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());
		final ProductModel product1 = productService.getProductForCode(onlineCatalogVersion, PRODUCT1_CODE);
		final ProductModel product2 = productService.getProductForCode(onlineCatalogVersion, PRODUCT2_CODE);

		// when
		final SearchResult searchResult = executeSearchQuery(
				searchQuery -> searchQuery.setCatalogVersions(Arrays.asList(onlineCatalogVersion)));

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, "code");
		assertDocumentField(PRODUCT1_CODE, document1, "pk");
		assertDocumentField(PRODUCT1_CODE, document1, "catalogId");
		assertDocumentField(PRODUCT1_CODE, document1, "catalogVersion");
		assertDocumentField(product1.getPk().getLongValue(), document1, SolrfacetsearchConstants.PK_FIELD);
		assertDocumentField(HW_CATALOG + getTestId(), document1, SolrfacetsearchConstants.CATALOG_ID_FIELD);
		assertDocumentField(ONLINE_CATALOG_VERSION + getTestId(), document1, SolrfacetsearchConstants.CATALOG_VERSION_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, "code");
		assertDocumentField(PRODUCT2_CODE, document2, "pk");
		assertDocumentField(PRODUCT2_CODE, document2, "catalogId");
		assertDocumentField(PRODUCT2_CODE, document2, "catalogVersion");
		assertDocumentField(product2.getPk().getLongValue(), document2, SolrfacetsearchConstants.PK_FIELD);
		assertDocumentField(HW_CATALOG + getTestId(), document2, SolrfacetsearchConstants.CATALOG_ID_FIELD);
		assertDocumentField(ONLINE_CATALOG_VERSION + getTestId(), document2, SolrfacetsearchConstants.CATALOG_VERSION_FIELD);
	}
}
