/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service;

public interface SapCpqSbConsumedDestinationService {
	
	/**
	 *  Method to find if destination exists
	 * @param destinationId
	 * @return
	 */
	public boolean checkIfDestinationExists(String destinationId);
}
