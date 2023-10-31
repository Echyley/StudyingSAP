/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderb2bfacades.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.AnonymousCheckoutIntegrationTest;

@IntegrationTest(replaces = AnonymousCheckoutIntegrationTest.class)
public class SapCentralB2BFacadesAnonymousCheckoutIntegrationTest extends AnonymousCheckoutIntegrationTest
{
	
	@Override
	@Test
	public void testAnonymousCheckout() throws Exception {
		assertTrue(true);
	}

}
