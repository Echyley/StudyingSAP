/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import javax.validation.constraints.NotNull;

/**
 * An exception indicating that the DestinationTarget is invalid.
 */
public class DestinationTargetValidationException extends InterceptorException
{
	/**
	 * Constructor to create DestinationTargetValidationException
	 *
	 * @param message a message to be used for this exception
	 */
	public DestinationTargetValidationException(@NotNull final String message)
	{
		super(message);
	}
}

