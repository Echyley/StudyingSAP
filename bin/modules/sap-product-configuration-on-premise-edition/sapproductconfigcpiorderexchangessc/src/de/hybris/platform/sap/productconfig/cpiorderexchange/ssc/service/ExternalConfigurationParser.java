/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service;

import com.sap.sce.kbrt.ext_configuration;


/**
 * Parser for the SSC generated product configuration XML
 */
public interface ExternalConfigurationParser
{
	/**
	 * Create external Configuration from XML string.
	 *
	 * @param str
	 *           XML holding configuration
	 * @return external configuration
	 */
	ext_configuration readExternalConfigFromString(String str);


}
