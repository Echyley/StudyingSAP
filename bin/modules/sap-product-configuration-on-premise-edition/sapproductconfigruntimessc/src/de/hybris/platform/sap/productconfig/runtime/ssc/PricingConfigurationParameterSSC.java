/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;


/**
 * Retrieves hybris and SSC specific data relevant for the configuration and pricing engine.
 */
public interface PricingConfigurationParameterSSC extends PricingConfigurationParameter
{

	/**
	 * Retrieves the pricing procedure used for pricing.
	 *
	 * @return the pricing procedure
	 */
	String getPricingProcedure();

	/**
	 * Retrieves the target for the base price. This is the purpose assigned to the condition function relevant for
	 * determining the base price of configurable products
	 *
	 * @return the target for the base price
	 */
	String getTargetForBasePrice();

	/**
	 * Retrieves the target for the option price. This is the purpose assigned to the condition function relevant for
	 * determining the total of the price-relevant options selected.
	 *
	 * @return the target for the option price
	 */
	String getTargetForSelectedOptions();

}
