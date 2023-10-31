/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when the WebhookConfiguration contains an event type that is not supported.
 */
public class WebhookConfigEventNotSupportedException extends InterceptorException
{
	private static final String ERROR_MSG_UNSUPPORTED = "%s event type is not supported. Supported types are: %s.";

	public WebhookConfigEventNotSupportedException(final String notSupportedType, final String supportedType, Interceptor inter)
	{
		super(String.format(ERROR_MSG_UNSUPPORTED, notSupportedType, supportedType), inter);
	}

}
