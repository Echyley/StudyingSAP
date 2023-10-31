/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

/**
 * An exception with localized message that is thrown by an interceptor.
 */
public abstract class LocalizedInterceptorException extends InterceptorException
{
	private static final long serialVersionUID = 3602865183967316099L;

	private final String[] parameters;

	/**
	 * @param message    The error message of this exception. This message is assigned in exceptions that extend this one and is used to clarify its meaning.
	 * @param parameters The parameters that are used for interpolation. The localized error messages returned by localization service
	 *                   may contain one or more placeholders and need to be evaluated with given parameters.
	 */
	protected LocalizedInterceptorException(final String message, final String... parameters)
	{
		super(message);
		this.parameters = Arrays.copyOf(parameters, parameters.length);
	}

	/**
	 * Returns parameters in the context of this exception. Each subclass can specify its own parameters that will be used for
	 * making localized messages.
	 *
	 * @return parameters associated with this exception or an empty array, if there are no parameters.
	 */
	@NotNull
	public Object[] getParameters()
	{
		return Arrays.copyOf(parameters, parameters.length);
	}
}
