/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.DefaultProductFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;

/**
 * Re-implements test {@link DefaultProductFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */

@IntegrationTest(replaces = DefaultProductFacadeIntegrationTest.class)
public class SalesOrderSimulationDefaultProductFacadeIntegrationTest extends DefaultProductFacadeIntegrationTest{

	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		insertExtraInformation();
	}
	
	@Test
	@Override
	public void testGetProductForCodeBasic()
	{
		super.testGetProductForCodeBasic();
	}

	public void insertExtraInformation() throws ImpExException {
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
	}
}
