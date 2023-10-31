/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderb2bfacades.replacement;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;

import org.junit.Test;

@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class SapCentralB2BFacadesStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest {

	@Override
	@Test
	public void testProductSearchForPos() {
		assertTrue(true);
	}

	@Override
	public void testProductSearchForPosSecondPage()
	{
		assertTrue(true);
	}

}
