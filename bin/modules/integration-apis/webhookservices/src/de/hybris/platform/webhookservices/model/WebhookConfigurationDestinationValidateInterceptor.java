/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.model;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException;

/**
 * Validates consumed destination used with {@link WebhookConfigurationModel} instances being persisted.
 * Forbids webhook configuration persistence, if the destination is not associated with a WEBHOOKSERVICES destination target.
 */
public final class WebhookConfigurationDestinationValidateInterceptor implements ValidateInterceptor<WebhookConfigurationModel>
{
	@Override
	public void onValidate(final WebhookConfigurationModel config, final InterceptorContext context) throws InterceptorException
	{
		if (!isWebhookDestination(config.getDestination()))
		{
			throw new WebhookConfigInvalidChannelException(this);
		}
	}

	private boolean isWebhookDestination(final ConsumedDestinationModel destination)
	{
		return destination == null
				|| DestinationChannel.WEBHOOKSERVICES.equals(destination.getDestinationTarget().getDestinationChannel());
	}

}
