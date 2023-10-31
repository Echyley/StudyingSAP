/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services;

/**
 * Sap pricing enablement service. 
 */
public interface SapPricingEnablementService {
	
	/**
	 * Method to check if cart pricing is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCartPricingEnabled();
	
	/**
	 * Method to check if catalog pricing is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCatalogPricingEnabled();

}
