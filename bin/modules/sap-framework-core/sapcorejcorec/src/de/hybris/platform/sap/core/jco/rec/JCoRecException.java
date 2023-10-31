/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;


/**
 * Exception for the JCoRecorder.
 */
@SuppressWarnings("serial")
public class JCoRecException extends Exception
{

	/**
	 * Calls {@code super()}.
	 */
	public JCoRecException()
	{
		super();
	}

	/**
	 * Calls {@code super(message cause)}.
	 * 
	 * @param message
	 *           the message of the exception.
	 * @param cause
	 *           the cause of the exception.
	 */
	public JCoRecException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Calls {@code super(message)}.
	 * 
	 * @param message
	 *           the message of the exception.
	 */
	public JCoRecException(final String message)
	{
		super(message);
	}

	/**
	 * Calls {@code super(cause)}.
	 * 
	 * @param cause
	 *           the cause of the exception.
	 */
	public JCoRecException(final Throwable cause)
	{
		super(cause);
	}
}
