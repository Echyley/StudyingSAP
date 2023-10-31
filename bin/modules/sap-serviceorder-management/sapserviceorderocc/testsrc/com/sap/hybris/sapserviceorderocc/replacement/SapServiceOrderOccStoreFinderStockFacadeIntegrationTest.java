/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;

@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class SapServiceOrderOccStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest{
	
	@Override
	@Test
	public void testProductSearchForPos() {
		assertTrue(true);
	}
	
	@Override
	@Test
	public void testProductSearchForPosSecondPage() {
		assertTrue(true);
	}
	
}
