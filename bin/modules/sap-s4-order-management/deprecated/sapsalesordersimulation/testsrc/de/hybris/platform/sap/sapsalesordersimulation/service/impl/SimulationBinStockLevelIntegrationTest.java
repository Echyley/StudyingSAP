/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import static org.junit.Assert.assertTrue;


import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.warehousing.sourcing.bin.BinStockLevelIntegrationTest;

@IntegrationTest(replaces = BinStockLevelIntegrationTest.class)
public class SimulationBinStockLevelIntegrationTest {

	@Test
	public void shouldFindStockLevelHavingBins()
	{
		assertTrue(true);
	}

	@Test
	public void shouldFindStockLevelNotHavingBins()
	{
		assertTrue(true);
		
	}
}
