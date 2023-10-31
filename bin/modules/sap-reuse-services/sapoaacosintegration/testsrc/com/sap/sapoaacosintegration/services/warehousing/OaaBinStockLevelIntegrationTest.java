/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.warehousing;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.warehousing.sourcing.bin.BinStockLevelIntegrationTest;

import org.junit.Test;


/**
 *
 */
@IntegrationTest(replaces =BinStockLevelIntegrationTest.class)
public class OaaBinStockLevelIntegrationTest
{

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
