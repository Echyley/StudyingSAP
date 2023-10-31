/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

/**
 *  Passport service
 *
 */
public interface SapS4OMPassportService {

	/**
	 * Generates an SAP Passport based on the provided info.
	 * 
	 * @param action for generation of passport
	 * @return The passport representation.
	 */
	String generate(String action);
}
