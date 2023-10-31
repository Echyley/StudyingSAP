/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * This strategy manages the caching of the product configuration model itself.
 */
public interface ConfigurationModelCacheStrategy
{

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
	 * purges the caches
	 */
	void purge();

	/**
	 * Removes the given configuration engine state and price summary model from read cache for engine state
	 *
	 * @param configId
	 *           unique config id
	 */
	void removeConfigAttributeState(String configId);

}
