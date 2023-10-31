/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

public class InvalidPropertyKeyFormatException extends RuntimeException
{

	public InvalidPropertyKeyFormatException(final String str)
	{
		super(str);
	}

	public InvalidPropertyKeyFormatException(final Throwable ex)
	{
		super(ex);
	}
}
