/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.exception;

/**
 * An exception indicating an Integration Object {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 * cannot be deleted because an item is referencing it.
 * @deprecated use {@link LocalizedInterceptorException} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public abstract class CannotDeleteIntegrationObjectException extends LocalizedInterceptorException
{
	protected CannotDeleteIntegrationObjectException(final String message, final String... parameters)
	{
		super(message, parameters);
	}
}
