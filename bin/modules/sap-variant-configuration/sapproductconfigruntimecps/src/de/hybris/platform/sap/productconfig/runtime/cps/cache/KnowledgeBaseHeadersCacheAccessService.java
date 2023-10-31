/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.List;


/**
 * Facilitates cache access for KB Header Lists
 */
public interface KnowledgeBaseHeadersCacheAccessService
{
	/**
	 * Retrieves knowledgebase headers for a specified product
	 *
	 * @param product
	 *           product code
	 * @return list of KB Header Info for product
	 */
	List<CPSMasterDataKBHeaderInfo> getKnowledgeBases(final String product);

	/**
	 * Access the Cache Region object
	 *
	 * @return CacheRegion object
	 */
	ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> getCache();

	/**
	 * Clears the cache
	 */
	void clearCache();

}
