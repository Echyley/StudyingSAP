/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;


public interface ImageHandler
{

	/**
	 * Converts image data
	 *
	 * @param configData
	 *           Source configuration data
	 * @param configurationWs
	 *           Target configuationWs DTO
	 */
	void convertImages(final ConfigurationData configData, final ConfigurationWsDTO configurationWs);

}
