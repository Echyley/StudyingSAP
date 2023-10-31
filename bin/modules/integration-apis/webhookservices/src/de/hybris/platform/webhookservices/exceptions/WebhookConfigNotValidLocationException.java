/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the WebhookConfiguration's filter location doesn't meet the pattern models.
 */
public class WebhookConfigNotValidLocationException extends InterceptorException
{
	private static final String ERROR_MSG_LOCATION = "WebhookConfigurationModel is misconfigured: Filter location '%s' provided does not meet the pattern model://<script_code>";

	public WebhookConfigNotValidLocationException(final String filterLocation, Interceptor inter)
	{
		super(String.format(ERROR_MSG_LOCATION, filterLocation), inter);
	}

}
