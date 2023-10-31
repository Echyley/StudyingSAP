/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the WebhookConfiguration's eventType is not contained
 * in the eventConfiguration list of the DestinationTarget of the WebhookConfiguration.
 */
public class WebhookConfigNotRegisteredEventException extends InterceptorException
{
	private static final String ERROR_MSG_UNREGISTERED = "WebhookConfigurationModel is misconfigured: %s is not registered with the destination target";

	public WebhookConfigNotRegisteredEventException(final String notRegisteredEvent, Interceptor inter)
	{
		super(String.format(ERROR_MSG_UNREGISTERED, notRegisteredEvent), inter);
	}

}
