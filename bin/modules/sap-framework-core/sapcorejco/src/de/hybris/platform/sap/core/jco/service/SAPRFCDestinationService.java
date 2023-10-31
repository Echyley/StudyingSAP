/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.service;

import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;


/**
 * Service for SAP RFC Destinations.
 */
public interface SAPRFCDestinationService
{

	/**
	 * Get an RFC Destination by destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 * @return The selected RFC Destination.
	 */
	public RFCDestination getRFCDestination(String rfcDestinationName);

}
