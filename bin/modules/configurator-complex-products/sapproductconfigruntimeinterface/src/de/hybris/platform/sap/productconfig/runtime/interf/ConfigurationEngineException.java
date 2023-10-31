/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Checked exception for errors in the configuration runtime
 */
public class ConfigurationEngineException extends Exception
{
	/**
	 * default constructor
	 */
	public ConfigurationEngineException()
	{
		super();
	}

	/**
	 * Constructor with message
	 *
	 * @param string
	 */
	public ConfigurationEngineException(final String message)
	{
		super(message);
	}


	/**
	 * Constructor with message and cause
	 *
	 * @param message
	 *           message of the exception
	 * @param cause
	 *           cause of the exception
	 */
	public ConfigurationEngineException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
