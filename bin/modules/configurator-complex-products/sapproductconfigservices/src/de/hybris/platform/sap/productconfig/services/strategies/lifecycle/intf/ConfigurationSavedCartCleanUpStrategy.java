/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

/**
 * Clean strategy for configurable product, to eliminate the product link to product configuration before saving cart.
 */
public interface ConfigurationSavedCartCleanUpStrategy
{
	/**
	 * Cleans up the cart with regards to its product configuration relevant aspects
	 */
	void cleanUpCart();


}
