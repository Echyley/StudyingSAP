/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import java.util.List;


/**
 * Facade for the pricing data of the product configuration
 *
 */
public interface ConfigurationPricingFacade
{
	/**
	 * Retrieves if present current total price, base price and selected options price
	 *
	 * @param configId
	 *           runtime id of the configuration
	 * @return pricing data object which contains the current base price, total and selected options price
	 */
	PricingData getPriceSummary(final String configId);

	/**
	 * Retrieves value prices for specified characteristics. Dependent on setting in backoffice it retrieves absolute
	 * value prices or delta prices
	 *
	 * @param csticUiKeys
	 *           Characteristics keys for which absolute value prices or delta prices are computed and attached to the
	 *           CsticValueModel csticUiKey is String formed as instanceId-instanceName.groupName.csticName
	 * @param configId
	 *           runtime id of the configuration
	 * @return list with absolute value prices or delta prices for required characteristic
	 */
	List<PriceValueUpdateData> getValuePrices(final List<String> csticUiKeys, final String configId);

	/**
	 * Indicates whether the underlying pricing service is active
	 *
	 * @return true if the underlying pricing service is active
	 */
	boolean isPricingServiceActive();

}
