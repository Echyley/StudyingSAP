/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Retrieves hybris data relevant for the configuration and pricing engine in the B2B scenario.
 */
public interface ConfigurationParameterB2B
{
	/**
	 * Retrieves the value of the flag for activating of sending the b2b relevant data to the configuration and pricing
	 * engine. If inactive, no b2b scenario relevant data are sent to the pricing engine.
	 * 
	 * @return the value of the flag for activating of of sending the b2b relevant data to the configuration and pricing
	 *         engine
	 */
	boolean isSupported();

	/**
	 * Retrieves the customer number for the logged on user.
	 * 
	 * @return the customer number
	 */
	String getCustomerNumber();

	/**
	 * Retrieves the SAP country code for the logged on user.
	 * 
	 * @return the the SAP country code
	 */
	String getCountrySapCode();

	/**
	 * Retrieves the customer price group for the logged on user.
	 * 
	 * @return the customer price group
	 */
	String getCustomerPriceGroup();
}
