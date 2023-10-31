/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.Collections;

import org.junit.Test;


@UnitTest
public class CPSCacheKeyGeneratorTest
{

	private static final String CONFIG_ID = "c123";
	private final CPSCacheKeyGeneratorStable classUndertest = new CPSCacheKeyGeneratorStable();

	@Test
	public void testCreateConfigurationCacheKeyDefault()
	{
		assertEquals(CPSCacheKeyGeneratorStable.CONFIG_CACHE_KEY,
				classUndertest.createConfigurationCacheKey(CONFIG_ID, Collections.EMPTY_MAP));
	}

	private static class CPSCacheKeyGeneratorStable implements CPSCacheKeyGenerator
	{

		public static final ProductConfigurationCacheKey CONFIG_CACHE_KEY = new ProductConfigurationCacheKey(null, CONFIG_ID,
				"junit");

		@Override
		public ProductConfigurationCacheKey createMasterDataCacheKey(final String kbId, final String lang)
		{
			return null;
		}

		@Override
		public ProductConfigurationCacheKey createKnowledgeBaseHeadersCacheKey(final String product)
		{
			return null;
		}

		@Override
		public ProductConfigurationCacheKey createCookieCacheKey(final String configId)
		{
			return null;
		}

		@Override
		public ProductConfigurationCacheKey createValuePricesCacheKey(final String kbId, final String pricingProduct)
		{
			return null;
		}

		@Override
		public ProductConfigurationCacheKey createConfigurationCacheKey(final String configId)
		{
			return CONFIG_CACHE_KEY;
		}

	}
}
