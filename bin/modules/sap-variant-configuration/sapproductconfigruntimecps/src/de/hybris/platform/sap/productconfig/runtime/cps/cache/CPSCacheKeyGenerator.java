/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.Map;


/**
 * Generates cache keys to be used for hybris cache regions in CPS context
 */
public interface CPSCacheKeyGenerator
{
	/**
	 * Creates a cache key for the master data cache region
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param lang
	 *           language
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createMasterDataCacheKey(final String kbId, final String lang);

	/**
	 * Creates a cache key for the knowledgebase headers cache region
	 *
	 * @param product
	 *           product for which knowledgebases are looked up
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createKnowledgeBaseHeadersCacheKey(final String product);

	/**
	 * Creates a cache key for the cookie cache region
	 *
	 * @param configId
	 *           configuration runtime id
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createCookieCacheKey(final String configId);

	/**
	 * Creates a cache key for the value prices cache region
	 *
	 * @param kbId
	 *           knowledgebase id for which all value prices are calculated
	 * @param pricingProduct
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createValuePricesCacheKey(final String kbId, String pricingProduct);

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param configId
	 *           configuration id
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createConfigurationCacheKey(String configId);

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param configId
	 *           configuration id
	 * @param ctxtAttributes
	 *           context attributes
	 * @return the created cache key
	 */
	default ProductConfigurationCacheKey createConfigurationCacheKey(final String configId, final Map<String, String> ctxtAttributes)
	{
		return createConfigurationCacheKey(configId);
	}
}
