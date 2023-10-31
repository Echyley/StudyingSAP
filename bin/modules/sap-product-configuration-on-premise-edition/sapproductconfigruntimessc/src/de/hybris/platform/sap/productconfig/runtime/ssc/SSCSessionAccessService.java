/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;


/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
public interface SSCSessionAccessService
{
	/**
	 * get the configuration provider for this session
	 *
	 * @return Configuration provider
	 */
	ConfigurationProvider getConfigurationProvider();

	/**
	 * cache the pricing provider in this session
	 *
	 * @param provider
	 *           provider to cache
	 */
	void setConfigurationProvider(ConfigurationProvider provider);
}
