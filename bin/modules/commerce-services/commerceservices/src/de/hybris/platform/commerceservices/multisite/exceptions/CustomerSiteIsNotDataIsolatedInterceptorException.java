/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite.exceptions;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;


public class CustomerSiteIsNotDataIsolatedInterceptorException extends InterceptorException
{
	public CustomerSiteIsNotDataIsolatedInterceptorException(final String message)
	{
		super(message);
	}
}
