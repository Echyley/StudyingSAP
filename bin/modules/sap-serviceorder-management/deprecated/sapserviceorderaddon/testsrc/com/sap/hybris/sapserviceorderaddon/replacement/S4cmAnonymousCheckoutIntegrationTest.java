/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderaddon.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.AnonymousCheckoutIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;


/**
 * Re-implements test {@link AnonymousCheckoutIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = AnonymousCheckoutIntegrationTest.class)
public class S4cmAnonymousCheckoutIntegrationTest extends AnonymousCheckoutIntegrationTest
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		insertExtraInformation();
	}

	/**
	 * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula for the used basestore.
	 *
	 * @throws ImpExException
	 */
	private void insertExtraInformation() throws ImpExException
	{
		importCsv("/sapserviceorderaddon/test/impex/replacement/replacement-add-formula-teststore.impex",
				"utf-8");
		importCsv("/sapserviceorderaddon/test/impex/replacement/replacement-us-regions.impex", "utf-8");
	}
}
