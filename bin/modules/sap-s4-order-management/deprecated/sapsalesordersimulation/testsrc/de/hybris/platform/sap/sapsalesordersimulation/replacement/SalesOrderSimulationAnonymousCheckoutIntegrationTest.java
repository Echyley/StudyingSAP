/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.replacement;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.AnonymousCheckoutIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;

/**
 * Re-implements test {@link AnonymousCheckoutIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */

@IntegrationTest(replaces = AnonymousCheckoutIntegrationTest.class)
public class SalesOrderSimulationAnonymousCheckoutIntegrationTest extends AnonymousCheckoutIntegrationTest {

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		insertExtraInformation();
	}
	
	@Test
	@Override
	public void testAnonymousCheckout() throws Exception
	{
		super.testAnonymousCheckout();
	}
	
	public void insertExtraInformation() throws ImpExException {
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-us-regions.impex", "utf-8");
	}
	
	
	
}
