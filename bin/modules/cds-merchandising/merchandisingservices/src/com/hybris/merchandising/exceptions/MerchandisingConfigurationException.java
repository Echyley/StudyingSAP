/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.exceptions;

/**
 * Exception thrown when some configuration required by merchandising is missing
 */
public class MerchandisingConfigurationException extends RuntimeException
{
	public MerchandisingConfigurationException(final String message)
	{
		super(message);
	}

	public MerchandisingConfigurationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
