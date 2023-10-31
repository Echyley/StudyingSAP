/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the WebhookConfiguration's eventType is not set.
 */
public class WebhookConfigNoEventTypeException extends InterceptorException
{
	private static final String ERROR_MSG_NO_EVENTTYPE = "WebhookConfigurationModel is misconfigured: event type is not set.";

	public WebhookConfigNoEventTypeException(Interceptor inter)
	{
		super(ERROR_MSG_NO_EVENTTYPE, inter);
	}

}
