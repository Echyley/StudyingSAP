/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;


/**
 * Exceptions which is thrown if the backend determination of the business object fails.
 */
public class BackendDeterminationRuntimeException extends BORuntimeException
{

	private static final long serialVersionUID = 5299225184712667677L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendDeterminationRuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public BackendDeterminationRuntimeException()
	{
		super();
	}

	/**
	 * Standard constructor for BackendDeterminationRuntimeException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public BackendDeterminationRuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
