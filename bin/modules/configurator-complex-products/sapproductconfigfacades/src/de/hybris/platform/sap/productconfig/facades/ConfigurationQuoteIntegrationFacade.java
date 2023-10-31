/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;


/**
 * Facade for integration of quote objects with ProductConfiguration
 */
@FunctionalInterface
public interface ConfigurationQuoteIntegrationFacade
{

	/**
	 * Retrieves ConfigurationOverviewData object for quote entry identified by code and entry number.
	 *
	 * @param code
	 *           code of the quote object
	 * @param entryNumber
	 *           entry number
	 * @return ConfigurationOverviewData object
	 */
	ConfigurationOverviewData getConfiguration(String code, int entryNumber);
}
