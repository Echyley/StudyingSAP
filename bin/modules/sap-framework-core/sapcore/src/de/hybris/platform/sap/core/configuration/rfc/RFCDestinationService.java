/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc;





/**
 * Interface to access SAP RFC destination data.
 */
public interface RFCDestinationService
{

	/**
	 * Returns the SAP RFC Destination for the given name.
	 * 
	 * @param destinationName
	 *           RFC Destination name
	 * @return list
	 */
	public RFCDestination getRFCDestination(String destinationName);

}
