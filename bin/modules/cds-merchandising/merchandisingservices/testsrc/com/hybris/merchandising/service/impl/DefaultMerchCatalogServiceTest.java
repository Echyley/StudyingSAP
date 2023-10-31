/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;


import static com.hybris.merchandising.service.impl.DefaultMerchCatalogService.CATALOG_VERSION;
import static com.hybris.merchandising.service.impl.DefaultMerchCatalogService.CATALOG_VERSION_SEPARATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver;
import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.MerchSnSynchContext;
import com.hybris.merchandising.model.Product;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

/**
 * DefaultMerchCatalogServiceTest is a test suite for {@link DefaultMerchCatalogService}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMerchCatalogServiceTest
{
	@InjectMocks
	DefaultMerchCatalogService merchCatalogService;
	@Mock
	BaseSiteModel baseSite;
	@Mock
	BaseSiteService baseSiteService;
	@Mock
	DefaultCategoryModelUrlResolver urlResolver;

	public static final String APPAREL_UK = "apparel-uk";
	public static final String CATALOG_ID = "123";
	public static final String VERSION = "live";
	public static final String CATALOG_VERSION_FIELD_VALUE = CATALOG_ID + CATALOG_VERSION_SEPARATOR + VERSION;
	public static final String BASE_CAT_URL = "https://hybris.com";

	@Mock
	MerchSnConfigModel merchConfig;
	@Mock
	SnIndexTypeModel snIndexType;
	@Mock
	CatalogModel catalog;
	@Mock
	CatalogVersionModel stageCatalogVersion1;
	@Mock
	CatalogVersionModel onlineCatalogVersion1;
	@Mock
	CatalogVersionModel stageCatalogVersion2;
	@Mock
	CatalogVersionModel onlineCatalogVersion2;

	@Mock
	SnIndexerBatchContext snIndexerBatchContext;
	@Mock
	SnIndexerBatchResponse batchResponse;
	@Mock
	SnDocumentBatchRequest batchRequest;
	@Mock
	SnDocumentBatchOperationRequest batchOperationRequest;
	@Mock
	SnDocument indexedDocument;
	@Mock
	MerchSnSynchContext merchContext;
	@Mock
	Converter<MerchSnDocumentContainer, Product> merchSnProductConverter;

	@Before
	public void setUp()
	{
		when(baseSiteService.getBaseSiteForUID(APPAREL_UK)).thenReturn(baseSite);

		final List<CatalogModel> mockedCatalog = new ArrayList<>();
		mockedCatalog.add(getMockCatalogModel());
		when(baseSiteService.getProductCatalogs(Mockito.any(BaseSiteModel.class))).thenReturn(mockedCatalog);

		when(urlResolver.resolve(Mockito.any())).thenReturn("");
	}

	@Test
	public void testGetCategories()
	{
		//when
		final List<CategoryHierarchy> retrievedValue = merchCatalogService.getCategories(APPAREL_UK, CATALOG_ID, VERSION, BASE_CAT_URL);

		//then
		verifyCategories(retrievedValue);
	}

	private void verifyCategories(final List<CategoryHierarchy> retrievedValue)
	{
		assertNotNull("Expected retrieved result to not be null", retrievedValue);
		Assert.assertEquals("Expected retrieved value to contain 1 root category", 1, retrievedValue.size());

		final CategoryHierarchy root = retrievedValue.get(0);
		Assert.assertEquals("Expected root category to contain 2 children", 2, root.getSubcategories().size());
		final CategoryHierarchy subCat1 = root.getSubcategories().get(0);
		Assert.assertEquals("Expected child category to contain 1 child", 1, subCat1.getSubcategories().size());
	}

	@Test
	public void testGetBaseSiteService()
	{
		final BaseSiteService service = merchCatalogService.getBaseSiteService();
		assertNotNull("Expected configured base site service to not be null", service);
		Assert.assertEquals("Expected baseSiteService to be the same as injected", baseSiteService, service);
	}

	@Test
	public void testGetCatalogVersionsToSynch()
	{
		//given
		when(merchConfig.getSnIndexType()).thenReturn(snIndexType);
		when(snIndexType.getCatalogs()).thenReturn(List.of(catalog));
		when(catalog.getCatalogVersions()).thenReturn(Set.of(stageCatalogVersion1, onlineCatalogVersion1));
		when(onlineCatalogVersion1.getActive()).thenReturn(Boolean.TRUE);
		when(stageCatalogVersion1.getActive()).thenReturn(Boolean.FALSE);
		when(snIndexType.getCatalogVersions()).thenReturn(List.of(onlineCatalogVersion2, stageCatalogVersion2));
		when(onlineCatalogVersion2.getActive()).thenReturn(Boolean.TRUE);
		when(stageCatalogVersion2.getActive()).thenReturn(Boolean.FALSE);

		//when
		final List<CatalogVersionModel> result = merchCatalogService.getCatalogVersionsToSynch(merchConfig);

		//then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(onlineCatalogVersion1) && result.contains(onlineCatalogVersion2));
	}

	@Test
	public void testGetCategoriesForMerchSnConfig()
	{
		//given
		when(merchConfig.getBaseSite()).thenReturn(baseSite);
		when(merchConfig.getBaseCatalogPageUrl()).thenReturn(BASE_CAT_URL);

		//when
		final List<CategoryHierarchy> result = merchCatalogService.getCategories(merchConfig);

		//then
		verifyCategories(result);
	}

	@Test
	public void testGetProductsForMerchSnConfig()
	{
		//given
		final Product product = new Product();
		when(merchSnProductConverter.convert(any())).thenReturn(product);
		when(merchConfig.getSnIndexType()).thenReturn(snIndexType);
		mockIndexTypeCatalogVersions();
		mockIndexerBatchContext();
		mockIndexedDocument();

		//when
		final List<Product> result = merchCatalogService.getProducts(snIndexerBatchContext, merchConfig, merchContext);

		//then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(product, result.get(0));
		assertEquals("CREATE", product.getAction());
	}

	@Test
	public void testGetProductsForMerchSnConfigWhenInputDocumentIsNull()
	{
		//given
		when(merchConfig.getSnIndexType()).thenReturn(snIndexType);
		mockIndexTypeCatalogVersions();
		mockIndexerBatchContext();

		//when
		final List<Product> result = merchCatalogService.getProducts(snIndexerBatchContext, merchConfig, merchContext);

		//then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testIsToSynchronize()
	{
		//given
		final List<CatalogVersionModel> catalogVersions = List.of(onlineCatalogVersion1);
		when(onlineCatalogVersion1.getVersion()).thenReturn(VERSION);
		when(onlineCatalogVersion1.getCatalog()).thenReturn(catalog);
		when(catalog.getId()).thenReturn(CATALOG_ID);
		mockIndexedDocument();

		//when
		final boolean result = merchCatalogService.isToSynchronize(catalogVersions, indexedDocument);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsToSynchronizeWhenNoCatalogVersionField()
	{
		//given
		final List<CatalogVersionModel> catalogVersions = List.of(onlineCatalogVersion1);
		when(indexedDocument.getFields()).thenReturn(Map.of());

		//when
		final boolean result = merchCatalogService.isToSynchronize(catalogVersions, indexedDocument);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsToSynchronizeWhenDocumentIsNull()
	{
		//given
		final List<CatalogVersionModel> catalogVersions = List.of(onlineCatalogVersion1);

		//when
		final boolean result = merchCatalogService.isToSynchronize(catalogVersions, (SnDocument) null);

		//then
		assertFalse(result);
	}

	private void mockIndexerBatchContext()
	{
		when(snIndexerBatchContext.getIndexerBatchResponse()).thenReturn(batchResponse);
		when(batchResponse.getDocumentBatchRequest()).thenReturn(batchRequest);
		when(batchRequest.getRequests()).thenReturn(List.of(batchOperationRequest));
	}

	private void mockIndexedDocument()
	{
		when(batchOperationRequest.getDocument()).thenReturn(indexedDocument);
		when(indexedDocument.getFields()).thenReturn(Map.of(CATALOG_VERSION, CATALOG_VERSION_FIELD_VALUE));
		when(batchOperationRequest.getOperationType()).thenReturn(SnDocumentOperationType.CREATE);
	}

	private void mockIndexTypeCatalogVersions()
	{
		when(snIndexType.getCatalogs()).thenReturn(List.of(catalog));
		when(catalog.getCatalogVersions()).thenReturn(Set.of(stageCatalogVersion1, onlineCatalogVersion1));
		when(catalog.getId()).thenReturn(CATALOG_ID);
		when(onlineCatalogVersion1.getActive()).thenReturn(Boolean.TRUE);
		when(onlineCatalogVersion1.getCatalog()).thenReturn(catalog);
		when(onlineCatalogVersion1.getVersion()).thenReturn(VERSION);
		when(stageCatalogVersion1.getActive()).thenReturn(Boolean.FALSE);
		when(snIndexType.getCatalogVersions()).thenReturn(List.of(onlineCatalogVersion2, stageCatalogVersion2));
		when(onlineCatalogVersion2.getActive()).thenReturn(Boolean.TRUE);
		when(stageCatalogVersion2.getActive()).thenReturn(Boolean.FALSE);
	}


	private CatalogModel getMockCatalogModel()
	{
		final CatalogModel mockedCatalogModel = Mockito.mock(CatalogModel.class);
		final Set<CatalogVersionModel> catVersion = new HashSet<>();
		final CatalogVersionModel cvm = getMockCatalogVersionModel(Boolean.TRUE);
		catVersion.add(cvm);
		final CatalogVersionModel nonLive = getMockCatalogVersionModel(Boolean.FALSE);
		when(mockedCatalogModel.getCatalogVersions()).thenReturn(catVersion);
		return mockedCatalogModel;
	}

	private CatalogVersionModel getMockCatalogVersionModel(final Boolean live)
	{
		final CatalogVersionModel mockedCatalogVersionModel = Mockito.mock(CatalogVersionModel.class);

		final CategoryModel subCatL2 = getCategoryModel("subCatL2", "subCatL2", null);
		final CategoryModel subCatL1 = getCategoryModel("subCatL1", "subCatL1", Arrays.asList(new CategoryModel[]{ subCatL2 }));
		final CategoryModel subCatL1_2 = getCategoryModel("subCatL1_2", "subCatL1_2", null);
		final CategoryModel rootCategory = getCategoryModel("root", "root", Arrays.asList(new CategoryModel[]{ subCatL1, subCatL1_2 }));
		when(mockedCatalogVersionModel.getActive()).thenReturn(live);
		when(mockedCatalogVersionModel.getRootCategories()).thenReturn(Arrays.asList(new CategoryModel[]{ rootCategory }));
		return mockedCatalogVersionModel;
	}

	private CategoryModel getCategoryModel(final String code, final String name, final List<CategoryModel> categories)
	{
		final CategoryModel mockedCategoryModel = Mockito.mock(CategoryModel.class);
		when(mockedCategoryModel.getCode()).thenReturn("code");
		when(mockedCategoryModel.getName()).thenReturn("name");
		if (categories != null)
		{
			when(mockedCategoryModel.getCategories()).thenReturn(categories);
		}
		return mockedCategoryModel;
	}

}
