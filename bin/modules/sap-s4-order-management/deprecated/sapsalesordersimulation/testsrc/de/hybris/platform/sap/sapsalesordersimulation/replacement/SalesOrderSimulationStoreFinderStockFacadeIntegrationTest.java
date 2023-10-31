/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.replacement;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;

/**
 * Re-implements test {@link StoreFinderStockFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */

@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class SalesOrderSimulationStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest {

	@Override
	public void prepare() throws Exception
	{
		super.prepare();
		insertExtraInformation();
	}
	
	@Test
	@Override
	public void testProductSearchForPos()
	{
		super.testProductSearchForPos();
	}
	
	private void insertExtraInformation() throws ImpExException
	{
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
		importCsv("/sapsalesordersimulation/test/impex/replacement/replacement-store-finder-stock-test-data.impex", "utf-8");
	}
	
}
