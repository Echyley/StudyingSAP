/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;



/**
 * Event is triggered if a RFC Destination gets deleted.
 */
public class SAPRFCDestinationRemoveEvent extends SAPRFCDestinationEvent implements ClusterAwareEvent
{

	private static final long serialVersionUID = -1697399319711063686L;

	/**
	 * Default constructor.
	 */
	public SAPRFCDestinationRemoveEvent()
	{
		super();
	}

	/**
	 * Default constructor using RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 */
	public SAPRFCDestinationRemoveEvent(final String rfcDestinationName)
	{
		super(rfcDestinationName);
	}

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}
}
