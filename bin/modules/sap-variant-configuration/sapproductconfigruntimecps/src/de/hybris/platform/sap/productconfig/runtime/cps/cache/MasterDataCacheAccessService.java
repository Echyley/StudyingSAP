/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;


/**
 * Facilitates cache access for master data
 */
public interface MasterDataCacheAccessService
{
	/**
	 * Retrieves the knowledgebase container for a given id and language
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param language
	 *           language code
	 * @return knowledgebase for id and language code
	 */
	CPSMasterDataKnowledgeBaseContainer getKbContainer(final String kbId, final String language);

	/**
	 * Access the Cache Region object
	 *
	 * @return ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> object
	 */
	ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> getCache();

	/**
	 * Clears the cache
	 */
	void clearCache();

	/**
	 * Removes knowledgebase from cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param language
	 *           language code
	 */
	void removeKbContainer(String kbId, final String language);

}
