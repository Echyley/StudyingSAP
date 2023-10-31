/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the DestinationTarget of the WebhookConfiguration's
 * ConsumedDestination contains no {@link EventConfigurationModel}.
 */
public class WebhookConfigNoEventConfigException extends InterceptorException
{
	private static final String ERROR_MSG_NO_EVENTCONFIG = "WebhookConfigurationModel is misconfigured: No Event Configurations linked with the destination target in use.";

	public WebhookConfigNoEventConfigException(Interceptor inter)
	{
		super(ERROR_MSG_NO_EVENTCONFIG, inter);
	}

}
