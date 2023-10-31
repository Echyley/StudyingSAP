/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Map;


/**
 * Accessing the cache to set, read and remove the cached data
 */
public interface ProductConfigurationCacheAccessService
{

	/**
	 * Sets analytic data into the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @param analyticsDocument
	 *           analytics document to be stored
	 */
	void setAnalyticData(String configId, AnalyticsDocument analyticsDocument);

	/**
	 * Retrieves analytic data from the cached map
	 *
	 * @param configId
	 *           id of the configuration
	 * @return anlytics document
	 */
	AnalyticsDocument getAnalyticData(String configId);

	/**
	 * Retrieves the price summary for a given runtime configuration, specified via its runtime id
	 *
	 * @param configId
	 *           id of the configuration
	 * @return price summary model
	 */
	PriceSummaryModel getPriceSummaryState(String configId);

	/**
	 * Puts the given price summary model into the price summary model state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param priceSummaryModel
	 *           model to cache
	 */
	void setPriceSummaryState(String configId, PriceSummaryModel priceSummaryModel);

	/**
	 * Retrieves the configuration model engine state
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Configuration model
	 */
	ConfigModel getConfigurationModelEngineState(String configId);

	/**
	 * Puts the given config model into the engine state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param configModel
	 *           model to cache
	 */
	void setConfigurationModelEngineState(String configId, ConfigModel configModel);


	/**
	 * Removes cached config, prices and analytics data from caches
	 *
	 * @param configId
	 *           associated configuration runtime id
	 */
	void removeConfigAttributeState(final String configId);


	/**
	 * Removes cached config, prices and analytics data from caches.<br>
	 * Instead of extracting context attributes, such as userId, from the current thread, they will be obtained from the
	 * provided context map.
	 *
	 * @param configId
	 *           configId to release
	 * @param ctxtAttributes
	 *           context attributes for cache key creation
	 */
	default void removeConfigAttributeState(final String configId, final Map<String, String> ctxtAttributes) {
		removeConfigAttributeState(configId);
	}


	/**
	 * Retrieves a map of names from the Hybris classification system
	 *
	 * @param productCode
	 *           The productCode for the classification system to retrieve
	 * @return Map of names from the Hybris classification system
	 */
	Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(String productCode);

	/**
	 * populates the map with all cache key components that depend on the current sessionContext, so a cache key for a
	 * given configId can re-created independently of the current session/threat context.
	 *
	 * @param ctxtAttributes
	 *           context attribute map to populate
	 */
	default void populateCacheKeyContextAttributes(final Map<String, String> ctxtAttributes) {
		//empty
	}

}
