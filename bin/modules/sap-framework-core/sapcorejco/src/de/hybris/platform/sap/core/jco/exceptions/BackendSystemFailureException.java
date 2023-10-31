/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;


/**
 * System failure of underlying backend system.
 */
public class BackendSystemFailureException extends BackendCommunicationException
{


	private static final long serialVersionUID = 5247149100970183992L;

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BackendSystemFailureException(final String msg)
	{
		super(msg);
	}
}
