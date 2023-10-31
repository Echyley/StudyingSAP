/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

public class DestinationCredNotMatchException extends InterceptorException
{
	private static final String DESTINATION_CREDENTIAL_ERROR_MSG = "Invalid credential type. Consumed credential is incorrectly assigned to exposed destination " +
			"or exposed credential to a consumed destination.";

	public DestinationCredNotMatchException(final Interceptor inter)
	{
		super(DESTINATION_CREDENTIAL_ERROR_MSG, inter);
	}
}
