/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


@UnitTest()
public class CacheKeyGeneratorTest
{
	private static final String CONFIG_ID = "CONFIG_ID";
	private static ProductConfigurationCacheKey ANALYTICS_KEY = new ProductConfigurationCacheKey(null, "duumy", "dummy");
	private static ProductConfigurationCacheKey CONFIG_KEY = new ProductConfigurationCacheKey(null, "duumy", "dummy");
	private static ProductConfigurationCacheKey PRICING_KEY = new ProductConfigurationCacheKey(null, "duumy", "dummy");

	private final CacheKeyGeneratorStable classUnderTest = new CacheKeyGeneratorStable();


	@Test
	public void testPopulateCacheKeyContextAttributesDefault()
	{
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		assertTrue(map.isEmpty()); //default implementation is empty
	}

	@Test
	public void testCreateAnalyticsDataCacheKeyDefault()
	{
		assertSame(ANALYTICS_KEY, classUnderTest.createAnalyticsDataCacheKey(CONFIG_ID, null));
	}

	@Test
	public void testCreateConfigCacheKeyDefault()
	{
		assertSame(CONFIG_KEY, classUnderTest.createConfigCacheKey(CONFIG_ID, null));
	}

	@Test
	public void testCreatePriceSummaryCacheKeyDefault()
	{
		assertSame(PRICING_KEY, classUnderTest.createPriceSummaryCacheKey(CONFIG_ID, null));
	}

	private static class CacheKeyGeneratorStable implements CacheKeyGenerator
	{

		@Override
		public ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId)
		{
			return ANALYTICS_KEY;
		}

		@Override
		public ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId)
		{
			return PRICING_KEY;
		}

		@Override
		public ProductConfigurationCacheKey createConfigCacheKey(final String configId)
		{
			return CONFIG_KEY;
		}

		@Override
		public ProductConfigurationCacheKey createClassificationSystemCPQAttributesCacheKey(final String productCode)
		{
			return null;
		}

	}
}
