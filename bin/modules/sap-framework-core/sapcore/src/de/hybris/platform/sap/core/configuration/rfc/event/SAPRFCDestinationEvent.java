/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.io.Serializable;


/**
 * Abstract RFC destination event.
 */
public abstract class SAPRFCDestinationEvent extends AbstractEvent
{

	private static final long serialVersionUID = 241109374898588797L;

	/**
	 * Constant for RFC destination name.
	 */
	public static final String RFC_DESTINATION_NAME = "rfcDestinationName";

	/**
	 * Constant for JCo trace level.
	 */
	public static final String JCO_TRACE_LEVEL = "jcotracelevel";

	/**
	 * Constant for JCo trace path.
	 */
	public static final String JCO_TRACE_PATH = "jcotracepath";

	/**
	 * Default Constructor.
	 */
	public SAPRFCDestinationEvent()
	{
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *           source
	 */
	public SAPRFCDestinationEvent(final Serializable source)
	{
		super(source);
	}
}
