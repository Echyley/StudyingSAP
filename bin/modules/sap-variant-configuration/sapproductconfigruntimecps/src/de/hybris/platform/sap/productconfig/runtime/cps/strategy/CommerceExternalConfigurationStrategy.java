/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.strategy;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;


/**
 * Defines the transition from {@link CPSExternalConfiguration} and {@link CPSCommerceExternalConfiguration} and vice
 * versa. We need to enrich the external format provided by the engine with additional commerce relevant information
 * like the SAP unit codes
 */
public interface CommerceExternalConfigurationStrategy
{

	/**
	 * Extracts the engine format from the commerce representation of the external configuration
	 *
	 * @param commerceExternalConfiguration
	 * @return External configuration in CPS engine format
	 */
	CPSExternalConfiguration extractCPSFormatFromCommerceRepresentation(
			CPSCommerceExternalConfiguration commerceExternalConfiguration);

	/**
	 * Creates a commerce format of the external configuration from the CPS engine format
	 * 
	 * @param externalConfiguration
	 * @return External configuration in commerce format
	 */
	CPSCommerceExternalConfiguration createCommerceFormatFromCPSRepresentation(CPSExternalConfiguration externalConfiguration);

}
