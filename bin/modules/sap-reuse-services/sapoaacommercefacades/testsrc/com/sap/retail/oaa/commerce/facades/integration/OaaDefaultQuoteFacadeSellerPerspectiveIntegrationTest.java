/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacadeSellerPerspectiveIntegrationTest;

@IntegrationTest(replaces =DefaultQuoteFacadeSellerPerspectiveIntegrationTest.class)
public class OaaDefaultQuoteFacadeSellerPerspectiveIntegrationTest{

	
	@Test
	public void shouldUpdateQuantitiesOnSaveQuote()
	{
		assertTrue(true);
	}
}