/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the WebhookConfiguration's DestinationTarget
 * is not associated with {@link DestinationChannel}.WEBHOOKSERVICES.
 */
public class WebhookConfigInvalidChannelException extends InterceptorException
{
	private static final String ERROR_MSG_CHANNEL = String.format(
			"WebhookConfigurationModel is misconfigured: the WebhookConfiguration's DestinationTarget is not associated with %s",
			DestinationChannel.WEBHOOKSERVICES.getCode());

	public WebhookConfigInvalidChannelException(Interceptor inter)
	{
		super(ERROR_MSG_CHANNEL, inter);
	}

}
