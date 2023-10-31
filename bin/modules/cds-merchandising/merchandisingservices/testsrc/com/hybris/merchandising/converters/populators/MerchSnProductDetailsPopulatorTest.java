/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static de.hybris.platform.testframework.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.Product;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MerchSnProductDetailsPopulatorTest extends AbstractMerchSnPopulatorTest
{
	protected static String CATEGORY_1 = "category1";
	protected static String CATEGORY_2 = "category2";
	protected static String BASE_URL = "baseUrl/";
	protected static String URL_VALUE = "productUrl";
	protected static String ID_VALUE = "productID";

	protected MerchSnProductDetailsPopulator productDetailsPopulator;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		productDetailsPopulator = new MerchSnProductDetailsPopulator();
	}

	@Test
	public void testPopulateListOfCategories()
	{
		//given
		final List categories = List.of(CATEGORY_1, CATEGORY_2);
		when(snDocument.getFields()).thenReturn(Map.of(CATEGORIES_FIELD, categories));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertTrue(CollectionUtils.isEqualCollection(categories, target.getCategories()));
	}

	@Test
	public void testPopulateCategory()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(CATEGORIES_FIELD, CATEGORY_1));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		final List result = target.getCategories();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(CATEGORY_1, result.get(0));
	}

	@Test
	public void testPopulatePageUrl()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(URL_FIELD, URL_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertEquals(URL_VALUE, target.getPageUrl());
	}

	@Test
	public void testPopulatePageUrlWithBaseUrl()
	{
		//given
		when(merchConfig.getBaseCatalogPageUrl()).thenReturn(BASE_URL);
		when(snDocument.getFields()).thenReturn(Map.of(URL_FIELD, URL_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertTrue(target.getPageUrl().startsWith(BASE_URL));
		assertTrue(target.getPageUrl().endsWith(URL_VALUE));
	}

	@Test
	public void testPopulateReportingGroup()
	{
		//given
		when(merchConfig.getRollUpStrategyField()).thenReturn(BASE_PRODUCT);
		when(snDocument.getFields()).thenReturn(Map.of(BASE_PRODUCT, ID_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertEquals(ID_VALUE, target.getReportingGroup());
	}

	@Test
	public void testPopulateDefaultReportingGroup()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(CODE_FIELD, ID_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertEquals(ID_VALUE, target.getReportingGroup());
	}

	@Test
	public void testPopulateProductDetails()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(CODE_FIELD, ID_VALUE, STOCK_LEVEL_STATUS, STOCK_LEVEL_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		final Map productDetails = target.getProductDetails();
		assertNotNull(productDetails);
		assertEquals(2, productDetails.size());
		assertEquals(ID_VALUE, productDetails.get(Product.ID));
		assertEquals(STOCK_LEVEL_VALUE, productDetails.get(STOCK_LEVEL_STATUS));
	}

	@Test
	public void testPopulatePriceMap()
	{
		//given
		when(currencyModel.getIsocode()).thenReturn(DEFAULT_CURRENCY_ISO);
		when(snDocument.getFields()).thenReturn(Map.of(PRICE_FIELD, PRICE_MAP_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		final Map productDetails = target.getProductDetails();
		assertNotNull(productDetails);
		assertEquals(PRICE_VALUE, productDetails.get(Product.PRICE));
	}

	@Test
	public void testPopulatePrice()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(PRICE_FIELD, PRICE_VALUE));

		//when
		productDetailsPopulator.populate(source, target);

		//then
		final Map productDetails = target.getProductDetails();
		assertNotNull(productDetails);
		assertEquals(PRICE_VALUE, productDetails.get(Product.PRICE));
	}

	@Test
	public void testPopulateWhenNoValues()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of());

		//when
		productDetailsPopulator.populate(source, target);

		//then
		assertTrue(StringUtils.isEmpty(target.getPageUrl()));
		assertTrue(StringUtils.isEmpty(target.getReportingGroup()));
		assertNotNull(target.getCategories());
		assertTrue(target.getCategories().isEmpty());
		final Map productDetails = target.getProductDetails();
		assertNotNull(productDetails);
		assertEquals(productDetailsFields.size(), productDetails.size());
		productDetails.values().forEach(v -> StringUtils.isEmpty(v.toString()));
	}

}
