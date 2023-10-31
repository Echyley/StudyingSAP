/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.Map;


/**
 * Generates cache keys to be used for hybris cache regions
 */
public interface CacheKeyGenerator
{
	/**
	 * Creates a cache key for the analytics data cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId);

	/**
	 * Creates a cache key for the price summary cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId);

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createConfigCacheKey(final String configId);

	/**
	 * Creates a cache key for the classification system CPQ attributes cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createClassificationSystemCPQAttributesCacheKey(final String productCode);

	/**
	 * Populates the map with all cache key components that depend on the current sessionContext, so a cache key for a
	 * given configId can re-created independently of the current session/thread context.
	 *
	 * @param ctxtAttributes
	 *           context attribute map to populate
	 */
	default void populateCacheKeyContextAttributes(final Map<String, String> ctxtAttributes)
	{
		// empty
	}

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param ctxtAttributes
	 *           context attributes
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	default ProductConfigurationCacheKey createConfigCacheKey(final String configId, final Map<String, String> ctxtAttributes)
	{
		return createConfigCacheKey(configId);
	}

	/**
	 * Creates a cache key for the price summary cache region
	 *
	 * @param ctxtAttributes
	 *           context attributes
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	default ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId,
			final Map<String, String> ctxtAttributes)
	{
		return createPriceSummaryCacheKey(configId);
	}

	/**
	 * Creates a cache key for the analytics data cache region
	 *
	 * @param ctxtAttributes
	 *           context attributes
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	default ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId,
			final Map<String, String> ctxtAttributes)
	{
		return createAnalyticsDataCacheKey(configId);
	}
}
