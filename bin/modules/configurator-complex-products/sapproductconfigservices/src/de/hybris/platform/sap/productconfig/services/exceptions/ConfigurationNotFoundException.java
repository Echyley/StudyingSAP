/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.exceptions;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

public class ConfigurationNotFoundException extends ModelNotFoundException
{
	public ConfigurationNotFoundException(final String message)
	{
		super(message);
	}

	public ConfigurationNotFoundException(final Throwable cause)
	{
		super(cause);
	}

	public ConfigurationNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
