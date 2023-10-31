/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util;

import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;


/**
 * Prepares configuration and configuration overview messages for OCC layer
 */
public interface MessageHandler
{

	/**
	 * Prepares configuration messages for OCC layer
	 *
	 * @param configuration
	 *                         OCC configuration
	 */
	void processConfiguration(ConfigurationWsDTO configuration);

	/**
	 * Prepares configuration overview messages for OCC layer
	 *
	 * @param configurationOverview
	 *                                 OCC configuration overview
	 */
	void processConfigurationOverview(ConfigurationOverviewWsDTO configurationOverview);

}
