/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;

import java.io.IOException;


/**
 * Re-implements test {@link StoreFinderStockFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */
@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class CentralOrderStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest
{
	@Override
	public void prepare() throws Exception
	{
		super.prepare();
		insertExtraInformation();
	}

	/**
	 * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula
	 * for the used basestore.
	 *
	 * @throws IOException
	 * @throws ImpExException
	 */
	private void insertExtraInformation() throws IOException, ImpExException
	{
		importCsv("/sapcentralorderservices/test/impex/replacement/replacement-store-finder-stock-test-data.impex", "utf-8");
		importCsv("/sapcentralorderservices/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
	}
}