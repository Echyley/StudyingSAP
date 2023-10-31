/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class DummyConfigurationsTest
{

	@Test
	public void testDragonCar()
	{
		assertNotNull(new DummyConfigurationWecDragonCarImpl());
	}

	@Test
	public void testKD990Sol()
	{
		assertNotNull(new DummyConfigurationKD990SolImpl());
	}
}
