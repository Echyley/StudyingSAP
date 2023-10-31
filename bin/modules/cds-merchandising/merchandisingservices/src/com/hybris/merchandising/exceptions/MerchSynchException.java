/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.exceptions;

/**
 * Exception thrown when synchronization process fails
 */
public class MerchSynchException extends RuntimeException
{
	public MerchSynchException(final String message)
	{
		super(message);
	}

	public MerchSynchException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
