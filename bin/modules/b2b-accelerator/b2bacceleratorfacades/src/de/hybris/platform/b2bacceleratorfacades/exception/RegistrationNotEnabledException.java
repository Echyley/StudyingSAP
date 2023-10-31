/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.exception;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Thrown when registration is not enabled
 */
public class RegistrationNotEnabledException extends BusinessException
{
	public RegistrationNotEnabledException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public RegistrationNotEnabledException(final String message)
	{
		super(message);
	}

	public RegistrationNotEnabledException(final Throwable cause)
	{
		super(cause);

	}
}
