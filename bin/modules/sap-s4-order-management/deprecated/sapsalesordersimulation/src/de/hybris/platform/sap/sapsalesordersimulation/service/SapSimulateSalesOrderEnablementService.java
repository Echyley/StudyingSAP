/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service;

/**
 * Sap pricing enablement service. 
 */
public interface SapSimulateSalesOrderEnablementService {
	
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
	
	/**
	 * Method to check if credit check is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCreditCheckActive();
	
	/**
	 * Method to check if ATP check is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isATPCheckActive();
}
