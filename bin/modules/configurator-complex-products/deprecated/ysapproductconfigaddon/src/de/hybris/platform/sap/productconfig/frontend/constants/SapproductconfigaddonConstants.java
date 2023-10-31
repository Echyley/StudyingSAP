/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.constants;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;


/**
 * Global class for all ysapproductconfigaddon constants.
 */
public class SapproductconfigaddonConstants extends GeneratedSapproductconfigaddonConstants
{
	/**
	 * name of the ysapproductconfigaddon extension
	 */
	public static final String EXTENSIONNAME = "ysapproductconfigaddon";
	/**
	 * If this method is available at the OrderEntryDTO, we assume that the UI is prepared to render the configuration
	 * link
	 *
	 * @see OrderEntryData
	 */
	public static final String CONFIGURABLE_SOM_DTO_METHOD = "isConfigurable";

	/**
	 * view attribute name for the {@link ConfigurationData} DTO
	 */
	public static final String CONFIG_ATTRIBUTE = "config";

	private SapproductconfigaddonConstants()
	{
		//empty
	}
}
