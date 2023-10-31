/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;

/**
 * Event is triggered in case of change JCoTrace level.
 */
public class SAPRFCDestinationJCoTraceEvent extends SAPRFCDestinationEvent implements ClusterAwareEvent
{

	private static final long serialVersionUID = -405191119466683723L;

	private String jcoTracePath;

	/**
	 * Default Constructor.
	 */
	public SAPRFCDestinationJCoTraceEvent()
	{
	}

	/**
	 * Constructor.
	 * 
	 * @param traceLevel
	 *           trace level
	 */
	public SAPRFCDestinationJCoTraceEvent(final String traceLevel)
	{
		super(traceLevel);
	}

	/**
	 * Returns the path where the JCo trace will be written.
	 * 
	 * @return the jcoTracePath
	 */
	public String getJCoTracePath()
	{
		return jcoTracePath;
	}

	/**
	 * Sets the path for the JCo trace.
	 * 
	 * @param jcoTracePath
	 *           the jcoTracePath to set
	 */
	public void setJCoTracePath(final String jcoTracePath)
	{
		this.jcoTracePath = jcoTracePath;
	}
	
	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}

}
