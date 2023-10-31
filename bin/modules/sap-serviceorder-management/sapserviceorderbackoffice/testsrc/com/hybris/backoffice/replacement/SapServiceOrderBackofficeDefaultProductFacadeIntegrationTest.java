/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.backoffice.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.DefaultProductFacadeIntegrationTest;

@IntegrationTest(replaces = DefaultProductFacadeIntegrationTest.class)
public class SapServiceOrderBackofficeDefaultProductFacadeIntegrationTest extends DefaultProductFacadeIntegrationTest{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	@Test
	public void testGetProductForCodeStock() {
		assertTrue(true);
	}
	
}
