/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;


/**
 * Failure during startup of backend server.
 */
public class BackendServerStartupException extends BackendCommunicationException
{


	private static final long serialVersionUID = -7289251566700402298L;

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BackendServerStartupException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public BackendServerStartupException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
