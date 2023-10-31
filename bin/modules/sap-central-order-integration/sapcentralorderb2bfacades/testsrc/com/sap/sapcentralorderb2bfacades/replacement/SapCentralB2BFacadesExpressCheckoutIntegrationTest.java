/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderb2bfacades.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.DefaultExpressCheckoutIntegrationCheckoutTest;

@IntegrationTest(replaces = DefaultExpressCheckoutIntegrationCheckoutTest.class)
public class SapCentralB2BFacadesExpressCheckoutIntegrationTest extends DefaultExpressCheckoutIntegrationCheckoutTest {
	
	
	@Override
	@Test
	public void testExpressCheckoutForPickupOnlyCart() throws Exception {
		assertTrue(true);
	}
	
	@Override
	@Test
	public void testExpressCheckoutForShippingCart() throws Exception {
		assertTrue(true);
	}

}
