/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.service;

/**
 * C4CConsumedDestinationService to check whether the destination exist or not.
 */

public interface C4CConsumedDestinationService {
/**
 *  Method to find if destination exists
 * @param destinationId The destination id which should be checked
 * @return returns whether the destinations exists or not
 */
	public boolean checkIfDestinationExists(String destinationId);
}
