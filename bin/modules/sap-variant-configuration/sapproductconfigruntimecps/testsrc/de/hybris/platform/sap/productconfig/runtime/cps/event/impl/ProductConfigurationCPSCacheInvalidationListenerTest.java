/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.event.impl;


import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest()
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationCPSCacheInvalidationListenerTest
{
	private static final String CONFIG_ID = "c123";

	private final ProductConfigurationCPSCacheInvalidationListener classUnderTest = new ProductConfigurationCPSCacheInvalidationListenerForTest();

	@Mock
	private CPSCache mockedCPSCache;

	private final Map<String, String> map = new HashMap<>();
	private final ProductConfigurationCacheInvalidationEvent event = new ProductConfigurationCacheInvalidationEvent(CONFIG_ID,
			map);

	@Test
	public void testOnEvent()
	{
		classUnderTest.onEvent(event);
		verify(mockedCPSCache).removeConfiguration(CONFIG_ID, map);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testOnEventDefault()
	{
		new ProductConfigurationCPSCacheInvalidationListener().onEvent(event);
	}

	public class ProductConfigurationCPSCacheInvalidationListenerForTest extends ProductConfigurationCPSCacheInvalidationListener
	{
		@Override
		protected CPSCache getCPSCache()
		{
			return mockedCPSCache;
		}
	}
}
