/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapdpordermanagement.actions.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.DefaultProductFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;


/**
 * Re-implements test {@link DefaultProductFacadeIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = DefaultProductFacadeIntegrationTest.class)
public class DigitalPaymentProductFacadeIntegrationTest extends DefaultProductFacadeIntegrationTest
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
		importCsv("/ysapdpordermanagement/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
	}

}