/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import de.hybris.platform.store.BaseStoreModel;

/**
 * Sap S4 Synchronous Order management service to read the configuration. 
 */
public interface SapS4OrderManagementConfigService {
	
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
	
	/**
	 * Method to check if synchronous order is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isS4SynchronousOrderEnabled();
	
	/**
	 * Method to check if synchronous order history is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isS4SynchronousOrderHistoryEnabled();
	
	/**
	 * Method to check if requested retrieval date feature is enabled.
	 * 
	 * @param baseStore base store
	 * @return boolean true if enabled otherwise false
	 */
	boolean isRequestedRetrievalDateFeatureEnabled( BaseStoreModel baseStore);
	
}
