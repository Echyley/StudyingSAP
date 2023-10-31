/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */
package de.hybris.platform.b2b.occ.exceptions;

public class B2BRegistrationException extends RuntimeException
{
	public B2BRegistrationException(final String message)
	{
		super(message);
	}

	public B2BRegistrationException(final String message, final Throwable t)
	{
		super(message, t);
	}
}
