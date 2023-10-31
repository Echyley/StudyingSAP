/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.replacement;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.DefaultExpressCheckoutIntegrationCheckoutTest;
import de.hybris.platform.impex.jalo.ImpExException;

/**
 * Re-implements test {@link DefaultExpressCheckoutIntegrationCheckoutTest} to provide missing information required when
 * warehousing extensions is present
 */

@IntegrationTest(replaces = DefaultExpressCheckoutIntegrationCheckoutTest.class)
public class SalesOrderSimulationDefaultExpressCheckoutIntegrationCheckoutTest extends DefaultExpressCheckoutIntegrationCheckoutTest {

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		insertExtraInformation();
	}
	
	@Test
	@Override
	public void testExpressCheckoutForShippingCart() throws Exception
	{
		super.testExpressCheckoutForShippingCart();
	}
	
	private void insertExtraInformation() throws ImpExException
	{
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-express-checkout-test-data.impex", "utf-8");
	}
}
