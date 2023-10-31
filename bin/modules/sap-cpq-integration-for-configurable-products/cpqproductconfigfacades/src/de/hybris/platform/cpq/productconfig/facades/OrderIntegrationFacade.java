/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades;

import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;

/**
 * CPQ facade for integration with order
 */
public interface OrderIntegrationFacade
{
	/**
	 * Retrieves configuration id for a order entry
	 *
	 * @param orderCode
	 *           order Code
	 * @param entryNumber
	 *           entry number
	 * @return configuration id
	 */
	ProductConfigData getConfigurationId(String orderCode, int entryNumber);

	/**
	 * Retrieves product code for a order entry
	 *
	 * @param orderCode
	 *           order code
	 * @param entryNumber
	 *           entry number
	 * @return product code
	 */
	String getProductCode(String orderCode, int entryNumber);
}
