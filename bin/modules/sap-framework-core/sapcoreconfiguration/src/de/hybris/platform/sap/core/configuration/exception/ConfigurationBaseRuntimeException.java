/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.exception;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;


/**
 * Core base runtime exception.
 */
public class ConfigurationBaseRuntimeException extends CoreBaseRuntimeException
{

	private static final long serialVersionUID = 8730135682117510547L;

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with no detail message.
	 */
	public ConfigurationBaseRuntimeException()
	{
		super();
	}

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with the specified detail message.
	 * 
	 * @param message
	 *           the detail message.
	 */
	public ConfigurationBaseRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * Standard constructor for ConfigurationBaseRuntimeException with the specified detail message and root cause.
	 * 
	 * @param message
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ConfigurationBaseRuntimeException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}

}
