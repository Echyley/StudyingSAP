/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;


/**
 * Facade for integration of saved carts with ProductConfiguration
 */
@FunctionalInterface
public interface ConfigurationSavedCartIntegrationFacade
{

	/**
	 * Retrieves ConfigurationOverviewData object for saved cart entry identified by code and entry number.
	 *
	 * @param code
	 *           code of the saved cart object
	 * @param entryNumber
	 *           entry number
	 * @return ConfigurationOverviewData object
	 * @throws CommerceSaveCartException
	 *            in case cart is not found
	 */
	ConfigurationOverviewData getConfiguration(String code, int entryNumber) throws CommerceSaveCartException;
}
