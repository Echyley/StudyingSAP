/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationCPQCacheInvalidationListenerTest
{
	private static final String CONFIG_ID = "abc";

	private final ProductConfigurationCPQCacheInvalidationListener classUnderTest = new ProductConfigurationCPQCacheInvalidationListener();
	private final ProductConfigurationCPQCacheInvalidationListener classUnderTestForOnEvent = new ProductConfigurationCPQCacheInvalidationListenerForTest();

	@Mock
	private CacheAccessService cpqCache;
	@Mock
	private CacheKeyService cacheKeyService;

	private final ProductConfigurationCPQCacheInvalidationEvent event = new ProductConfigurationCPQCacheInvalidationEvent(
			CONFIG_ID);

	@Test
	public void testOnEvent()
	{
		classUnderTestForOnEvent.onEvent(event);
		verify(cpqCache).remove(cacheKeyService.createConfigurationSummaryCacheKey(CONFIG_ID));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetCpqCache()
	{
		classUnderTest.getCPQCache();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetCacheKeyService()
	{
		classUnderTest.getCacheKeyService();
	}

	private class ProductConfigurationCPQCacheInvalidationListenerForTest extends ProductConfigurationCPQCacheInvalidationListener
	{
		@Override
		protected CacheAccessService getCPQCache()
		{
			return cpqCache;
		}

		@Override
		protected CacheKeyService getCacheKeyService()
		{
			return cacheKeyService;
		}
	}

}
