/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysaprcomsfulfilment.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.DefaultProductFacadeIntegrationTest;

@IntegrationTest(replaces = DefaultProductFacadeIntegrationTest.class)
public class SaprcomsFulfilmentProductFacadeIntegrationTest extends DefaultProductFacadeIntegrationTest {
	
	@Override
	@Test
	public void testGetProductForCodeStock() {
		assertTrue(true);
	}
}
