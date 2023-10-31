/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;

import java.util.List;
import java.util.Map;


/**
 * Handling of CPS specific attributes in hybris cache.
 */


public interface CPSCache
{

	/**
	 * Sets ValuePrice into cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param pricingProduct
	 *           productId of the pricing product
	 * @param valuePricesMap
	 *           map of value prices
	 *
	 */
	void setValuePricesMap(String kbId, String pricingProduct, Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap);


	/**
	 * Get ValuePrice from cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param pricingProduct
	 *           productId of the pricing product
	 * @return map of value prices
	 */
	Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getValuePricesMap(String kbId, String pricingProduct);

	/**
	 * Sets cookies per configId
	 *
	 * @param configId
	 *           ID of runtime configuration
	 * @param cookieList
	 */
	void setCookies(String configId, List<String> cookieList);


	/**
	 * @param configId
	 * @return List of cookies per configId
	 */
	List<String> getCookies(String configId);


	/**
	 * Removes cookies per configId
	 *
	 * @param configId
	 */
	void removeCookies(String configId);

	/**
	 * Retrieves the runtime configuration from cache.
	 *
	 * @param configId
	 *           runtime configuration id
	 * @return runtime configuration
	 */
	CPSConfiguration getConfiguration(String configId);

	/**
	 * Removes the specified configuration from cache
	 *
	 * @param configId
	 *           runtime configuration id
	 */
	void removeConfiguration(String configId);

	/**
	 * Removes the specified configuration from cache
	 *
	 * @param configId
	 *           runtime configuration id
	 * @param ctxtAttributes
	 *           context attributes
	 *
	 */
	default void removeConfiguration(final String configId, final Map<String, String> ctxtAttributes) {
		removeConfiguration(configId);
	}

	/**
	 * Sets the runtime configuration into the cache
	 *
	 * @param configId
	 *           runtime configuration id
	 * @param configuration
	 *           runtime configuration to be set into cache
	 *
	 */
	void setConfiguration(String configId, CPSConfiguration configuration);

}
