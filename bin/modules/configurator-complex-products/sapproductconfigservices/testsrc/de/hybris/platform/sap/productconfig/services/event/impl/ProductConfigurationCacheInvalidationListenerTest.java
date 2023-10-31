/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.event.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest()
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationCacheInvalidationListenerTest
{
	private static final String CONFIG_ID = "c123";
	private final Map<String, String> contextAttr = new HashMap<>();

	private final ProductConfigurationCacheInvalidationListener classUnderTest = new ProductConfigurationCacheInvalidationListenerForTest();

	@Mock
	private ProductConfigurationCacheAccessService mockedCacheAccessService;


	private final ProductConfigurationCacheInvalidationEvent event = new ProductConfigurationCacheInvalidationEvent(CONFIG_ID, contextAttr);

	@Test
	public void testOnEvent()
	{
		classUnderTest.onEvent(event);
		verify(mockedCacheAccessService).removeConfigAttributeState(CONFIG_ID, contextAttr);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testOnEventDefault()
	{
		new ProductConfigurationCacheInvalidationListener().onEvent(event);
	}

	public class ProductConfigurationCacheInvalidationListenerForTest extends ProductConfigurationCacheInvalidationListener
	{
		@Override
		protected ProductConfigurationCacheAccessService getCacheAccessService()
		{
			return mockedCacheAccessService;
		}
	}
}
