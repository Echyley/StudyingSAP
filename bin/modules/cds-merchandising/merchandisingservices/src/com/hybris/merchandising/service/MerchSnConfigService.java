/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service;

import de.hybris.platform.searchservices.model.SnIndexTypeModel;

import java.util.Optional;

import com.hybris.merchandising.model.MerchSnConfigModel;

/**
 * Service that provides basic functionality for merchandising configurations.
 */
public interface MerchSnConfigService
{
	/**
	 * Returns the merchandising configuration for a specific index type.
	 *
	 * @param indexType - the index type identifier
	 * @return the merchandising configuration
	 */
	Optional<MerchSnConfigModel> getMerchConfigForIndexedType(String indexType);

	/**
	 * Returns the merchandising configuration for a specific index type.
	 *
	 * @param indexType - the index type model
	 * @return the merchandising configuration
	 */
	Optional<MerchSnConfigModel> getMerchConfigForIndexedType(SnIndexTypeModel indexType);

	/**
	 * Returns the merchandising  configuration being used by the current base site.
	 *
	 * @return the product directory configuration being used by the current base site.
	 */
	Optional<MerchSnConfigModel> getMerchConfigForCurrentBaseSite();

	/**
	 * Updates persistence for provided {@link MerchSnConfigModel}.
	 *
	 * @param merchSnConfig the product directory config model to update.
	 */
	void updateMerchConfig(MerchSnConfigModel merchSnConfig);

	/**
	 * Method deleting product directory related to given merchandising configuration (send delete request to catalog service)
	 *
	 * @param merchSnConfig - merchandising configuration
	 */
	void deleteProductDirectory(MerchSnConfigModel merchSnConfig);

	/**
	 * Method creating or updating product directory related to given merchandising configuration (send request to catalog service)
	 *
	 * @param merchSnConfig merchandising configuration
	 * @param saveModel     flag defining configuration model should be saved (after setting product directory identifier for it)
	 */
	void createOrUpdateProductDirectory(MerchSnConfigModel merchSnConfig, boolean saveModel);
}
