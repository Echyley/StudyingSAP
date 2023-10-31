/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.exceptions;

import de.hybris.platform.inboundservices.model.IntegrationClientCredentialsDetailsModel;
import de.hybris.platform.integrationservices.exception.LocalizedInterceptorException;

/**
 * An exception indicating an {@link IntegrationClientCredentialsDetailsModel}
 * cannot be created if {@link IntegrationClientCredentialsDetailsModel#getUser()}'s uid is admin
 */
public class CannotCreateIntegrationClientCredentialsDetailWithAdminException extends LocalizedInterceptorException
{
	private static final long serialVersionUID = -682577597947553913L;
	private static final String ERROR_MSG = "Cannot create IntegrationClientCredentialsDetails with admin user.";

	/**
	 * Instantiates this exception
	 */
	public CannotCreateIntegrationClientCredentialsDetailWithAdminException()
	{
		super(ERROR_MSG);
	}
}
