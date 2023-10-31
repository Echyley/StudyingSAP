/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.impl;

import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;

import java.util.HashMap;
import java.util.Map;



/**
 * Default implementation which returns a RFC Destination.
 */
public class DefaultRFCDestinationService implements RFCDestinationService
{
	/**
	 * RFC Destinations.
	 */
	private Map<String, RFCDestination> rfcDestinations = new HashMap<String, RFCDestination>();

	/**
	 * Injection setter for RFCDestination map.
	 * 
	 * @param rfcDestinations
	 *           map of RFC destinations
	 */
	public void setRFCDestinations(final Map<String, RFCDestination> rfcDestinations)
	{
		this.rfcDestinations = rfcDestinations;
	}

	/**
	 * Add RFCDestination to map.
	 * 
	 * @param destinationName
	 *           destination name
	 * @param rfcDestination
	 *           RFC destination
	 */
	public void addRFCDestination(final String destinationName, final RFCDestination rfcDestination)
	{
		this.rfcDestinations.put(destinationName, rfcDestination);
	}

	/**
	 * Remove RFCDestination from map.
	 * 
	 * @param destinationName
	 *           destination name
	 */
	public void removeRFCDestination(final String destinationName)
	{
		this.rfcDestinations.remove(destinationName);
	}

	@Override
	public RFCDestination getRFCDestination(final String destinationName)
	{
		RFCDestination rfcDestination = rfcDestinations.get(destinationName);
		if (rfcDestination == null)
		{
			rfcDestination = new DefaultRFCDestination();
			((DefaultRFCDestination) rfcDestination).setRfcDestinationName(destinationName);
			rfcDestinations.put(destinationName, rfcDestination);
		}
		return rfcDestination;
	}

}
