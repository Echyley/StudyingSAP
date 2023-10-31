/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for CPQ configurable checker integration
 */
@IntegrationTest
public class CPQConfigurableCheckerIntegrationTest extends CPQServiceLayerTest
{
	@Resource(name = "sapProductConfigDefaultCPQConfigurableChecker")
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Resource(name = "productService")
	private ProductService productService;

	private ProductModel cpqProductModel;
	private ProductModel nonCpqProductModel;

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();

		cpqProductModel = productService.getProductForCode(PRODUCT_CODE_CPQ_HOME_THEATER);
		nonCpqProductModel = productService.getProductForCode(PRODUCT_CODE_YSAP_NOCFG);
	}

	@Test
	public void testProductIsCPQConfigurable()
	{
		assertTrue(cpqConfigurableChecker.isCPQConfigurableProduct(cpqProductModel));
	}

	@Test
	public void testProductIsNotCPQConfigurable()
	{
		assertFalse(cpqConfigurableChecker.isCPQConfigurableProduct(nonCpqProductModel));
	}
}
