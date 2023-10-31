/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;


/**
 * Represents a the exception which occurs once the backend is switched offline (e.g. in RFC destination hmc/backoffice
 * maintenance).
 */
public class BackendOfflineException extends BackendCommunicationException
{

	private static final long serialVersionUID = 3596371100453759356L;

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BackendOfflineException(final String msg)
	{
		super(msg);
	}

}
