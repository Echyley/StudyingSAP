/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.http;



/**
 * Interface to access HTTP destination data.
 */
public interface HTTPDestinationService
{

	/**
	 * Returns the HTTP Destination for the given name.
	 * 
	 * @param destinationName
	 *           Name of the destination
	 * @return list
	 */
	public HTTPDestination getHTTPDestination(String destinationName);
}
