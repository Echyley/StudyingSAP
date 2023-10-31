/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;

/**
 * Runtime-Exception for the JCoRecorder.
 */
@SuppressWarnings("serial")
public class JCoRecRuntimeException extends RuntimeException
{
	/**
	 * Calls {@code super()}.
	 */
	public JCoRecRuntimeException()
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
	public JCoRecRuntimeException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Calls {@code super(message)}.
	 * 
	 * @param message
	 *           the message of the exception.
	 */
	public JCoRecRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * Calls {@code super(cause)}.
	 * 
	 * @param cause
	 *           the cause of the exception.
	 */
	public JCoRecRuntimeException(final Throwable cause)
	{
		super(cause);
	}
}
