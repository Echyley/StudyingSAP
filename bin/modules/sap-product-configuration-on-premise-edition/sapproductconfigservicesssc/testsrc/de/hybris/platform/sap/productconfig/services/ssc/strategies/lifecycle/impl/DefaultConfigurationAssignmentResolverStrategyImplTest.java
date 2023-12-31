/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationAssignmentResolverStrategyImplTest
{

	private static final String CONFIG_ID = "123";
	private static final String ENTRY_KEY = "entry key";

	private final DefaultConfigurationAssignmentResolverStrategyImpl classUnderTest = new DefaultConfigurationAssignmentResolverStrategyImpl();;

	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private SearchResult<Object> searchResult;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testRetrieveRelatedObjectType()
	{
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, classUnderTest.retrieveRelatedObjectType(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedObjectTypeByOrder()
	{
		final CartModel cart = new CartModel();
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, classUnderTest.retrieveRelatedObjectType(cart));
	}

	@Test
	public void testRetrieveCreationDateForRelatedEntry()
	{
		assertNull(classUnderTest.retrieveCreationDateForRelatedEntry(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedProductCodeFromAbstractOrderEntry()
	{
		final String productCode = "PRODUCT_CODE";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		final AbstractOrderEntryModel entry = prepareEntry();
		entry.setProduct(productModel);
		assertEquals(productCode, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedProductCodeFromProducty()
	{
		final String productCode = "PRODUCT_CODE";
		given(sessionAccessService.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(null);
		given(sessionAccessService.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);
		given(sessionAccessService.getProductForConfigId(CONFIG_ID)).willReturn(productCode);
		assertEquals(productCode, classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}

	@Test
	public void testRetrieveRelatedProductCodeNull()
	{
		given(sessionAccessService.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(null);
		given(sessionAccessService.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);
		given(sessionAccessService.getProductForConfigId(CONFIG_ID)).willReturn(null);
		assertNull(classUnderTest.retrieveRelatedProductCode(CONFIG_ID));
	}

	@Test
	public void testRetrieveOrderEntry()
	{
		final AbstractOrderEntryModel entry = prepareEntry();
		assertEquals(entry, classUnderTest.retrieveOrderEntry(CONFIG_ID));
	}

	@Test
	public void testRetrieveOrderEntryNull()
	{
		given(sessionAccessService.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(null);
		given(sessionAccessService.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);
		assertNull(classUnderTest.retrieveOrderEntry(CONFIG_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveOrderEntryQueryReturnsTooMany()
	{
		final String productCode = "PRODUCT_CODE";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		final AbstractOrderEntryModel entry = prepareEntry();
		entry.setProduct(productModel);
		given(searchResult.getCount()).willReturn(2);
		classUnderTest.retrieveRelatedProductCode(CONFIG_ID);

	}

	public AbstractOrderEntryModel prepareEntry()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		given(sessionAccessService.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(ENTRY_KEY);

		given(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).willReturn(searchResult);
		given(searchResult.getCount()).willReturn(1);
		given(searchResult.getResult()).willReturn(Arrays.asList(entry));

		return entry;
	}

}
