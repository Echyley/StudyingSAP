/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing;

import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;

import java.util.Collection;


/**
 * Retrieves hybris and CPS specific data relevant for the pricing service.
 */
public interface PricingConfigurationParameterCPS extends PricingConfigurationParameter
{

	/**
	 * Retrieves the pricing procedure used for pricing.
	 *
	 * @return the pricing procedure
	 */
	String getPricingProcedure();

	/**
	 * Retrieves the pricing details mode used for pricing.
	 *
	 * @return the pricing details mode
	 */
	SAPProductConfigPricingDetailsMode getPricingDetailsMode();

	/**
	 * Retrieves the condition function for the base price.
	 *
	 * @return the target for the base price
	 */
	String getTargetForBasePrice();

	/**
	 * Retrieves the condition function for the option price.
	 *
	 * @return the target for the option price
	 */
	String getTargetForSelectedOptions();

	/**
	 * Retrieves the subTotal name for the base price.
	 *
	 * @return the subTotal name for the base price
	 */
	String getSubTotalForBasePrice();

	/**
	 * Retrieves the subTotal name for the option price.
	 *
	 * @return the subTotal name for the option price
	 */
	String getSubTotalForSelectedOptions();

	/**
	 * Retrieves collection of the condition types relevant for the base price calculation.
	 *
	 * @return collection of the condition types for the base price
	 */
	Collection<String> getConditionTypesForBasePrice();

	/**
	 * Retrieves collection of the condition types relevant for the option price calculation.
	 *
	 * @return collection of the condition types for the option price
	 */
	Collection<String> getConditionTypesForSelectedOptions();

}
