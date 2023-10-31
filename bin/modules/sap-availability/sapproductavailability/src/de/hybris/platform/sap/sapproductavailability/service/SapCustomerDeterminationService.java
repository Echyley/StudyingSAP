/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.service;

/**
 * Interface for sap customer Determination
 */
public interface SapCustomerDeterminationService {

	/**
	 * This method reads SapCustomerId
	 * 
	 * @return SapCustomerId in String value
	 */
	public String readSapCustomerID();

}
