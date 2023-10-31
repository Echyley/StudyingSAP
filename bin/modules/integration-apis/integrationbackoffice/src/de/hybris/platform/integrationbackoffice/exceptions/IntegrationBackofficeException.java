/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

/**
 * The base exception for integration backoffice exceptions with localized messages.
 */
public abstract class IntegrationBackofficeException extends RuntimeException
{
	private static final long serialVersionUID = -6787859746108190255L;
	private final String[] parameters;

	/**
	 * @param throwable  The cause of this exception
	 * @param message    The error message of this exception
	 * @param parameters The error message's parameters.
	 */
	protected IntegrationBackofficeException(final Throwable throwable, final String message, final String... parameters)
	{
		super(MessageFormat.format(message, (Object[]) parameters), throwable);
		this.parameters = Arrays.copyOf(parameters, parameters.length);
	}

	/**
	 * @param message    The error message of this exception
	 * @param parameters The error message's parameters.
	 */
	protected IntegrationBackofficeException(final String message, final String... parameters)
	{
		this(null, message, parameters);
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
