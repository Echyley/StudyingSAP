/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.extensibility.request.RequestContext;
import de.hybris.platform.sap.core.extensibility.request.RequestContextContainer;

import java.util.EventObject;


/**
 * This class provides information on a connection event The BackendContext and Request Context can be used to exchange
 * data with other layers.
 * 
 * @see de.hybris.platform.sap.core.extensibility.request.RequestContext RequestContext
 */

@SuppressWarnings("serial")
public class ConnectionEvent extends EventObject
{

	/**
	 * Constructs an event.
	 * 
	 * @param source
	 *           the source of the event
	 */
	public ConnectionEvent(final Object source)
	{
		super(source);
	}


	/**
	 * Returns the request context. This context can be used to pass data between layers of the framework. The context
	 * has the scope of an request (HTTP request).
	 * <p/>
	 * <b>NOTE:</b><br/>
	 * This context MUST NOT be used in standard development. It is a tool for quickly extending the functionality of the
	 * standard in CUSTOMER projects. <br/>
	 * 
	 * @return the request context
	 */
	public RequestContext getRequestContext()
	{
		return RequestContextContainer.getInstance().getRequestContext();
	}

}
