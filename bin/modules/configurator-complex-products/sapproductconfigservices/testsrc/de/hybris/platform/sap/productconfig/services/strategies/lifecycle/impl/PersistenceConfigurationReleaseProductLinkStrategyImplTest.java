/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link PersistenceConfigurationReleaseProductLinkStrategyImpl}
 */
@UnitTest
public class PersistenceConfigurationReleaseProductLinkStrategyImplTest
{
	private static final String PRODUCT_CODE = "productCode";


	private PersistenceConfigurationReleaseProductLinkStrategyImpl classUnderTest;

	@Mock
	private ProductConfigurationModel productConfigModel;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionAccessService sessionAccessServiceMock;
	@Mock
	private ProductModel productModel;

	private final Collection<ProductModel> products = new ArrayList<>();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationReleaseProductLinkStrategyImpl();
		classUnderTest.setModelService(modelService);

		given(productModel.getCode()).willReturn(PRODUCT_CODE);
		products.add(productModel);

		classUnderTest.setSessionAccessService(sessionAccessServiceMock);

		given(productConfigModel.getProduct()).willReturn(products);
		given(orderEntry.getProductConfiguration()).willReturn(productConfigModel);

	}

	@Test
	public void testModelService()
	{
		assertEquals(modelService, classUnderTest.getModelService());
	}

	@Test
	public void testSessionAccessService()
	{
		assertEquals(sessionAccessServiceMock, classUnderTest.getSessionAccessService());
	}

	@Test
	public void testReleaseCartEntryProductRelation()
	{
		classUnderTest.releaseCartEntryProductRelation(orderEntry);
		verify(sessionAccessServiceMock).removeUiStatusForProduct(PRODUCT_CODE);
		verify(modelService).save(productConfigModel);
	}

	@Test
	public void testReleaseCartEntryProductRelationNoProductsAtConfiguration()
	{
		products.clear();
		classUnderTest.releaseCartEntryProductRelation(orderEntry);
		verify(sessionAccessServiceMock, never()).removeUiStatusForProduct(PRODUCT_CODE);
		verify(modelService, never()).save(productConfigModel);
	}

	@Test(expected = NullPointerException.class)
	public void testReleaseCartEntryProductRelationNoConfig()
	{
		given(orderEntry.getProductConfiguration()).willReturn(null);
		classUnderTest.releaseCartEntryProductRelation(orderEntry);
	}


}
