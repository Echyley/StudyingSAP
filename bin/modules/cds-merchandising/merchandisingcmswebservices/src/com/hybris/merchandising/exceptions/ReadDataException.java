/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.exceptions;


public class ReadDataException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ReadDataException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}

	public ReadDataException(final String message)
	{
		super(message);
	}
}
