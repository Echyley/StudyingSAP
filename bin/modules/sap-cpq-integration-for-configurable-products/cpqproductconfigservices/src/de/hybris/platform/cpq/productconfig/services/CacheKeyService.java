/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;


/**
 * Generates cache keys to be used for hybris cache regions
 */
public interface CacheKeyService
{
	/**
	 * Creates a cache key for the authorization data cache region where the base site is set externally
	 *
	 * @param baseSite
	 *           base site
	 *
	 * @return the created cache key
	 */
	DefaultCacheKey createAuthorizationDataCacheKey(final String baseSite);

	/**
	 * Creates a cache key for the authorization data cache region
	 *
	 * @return the created cache key
	 */
	DefaultCacheKey createAuthorizationDataCacheKey();

	/**
	 * Creates a cache key for the configuration summary cache region
	 *
	 * @param configId
	 *           identifier of the configuration summary
	 *
	 * @return the created cache key
	 */
	DefaultCacheKey createConfigurationSummaryCacheKey(final String configId);
}
