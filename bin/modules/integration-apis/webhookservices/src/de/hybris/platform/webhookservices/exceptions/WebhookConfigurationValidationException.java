/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.exceptions;

import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * Indicates a problem with the {@link WebhookConfigurationModel} when a destinationTarget is used to create webhookConfig.
 */
public class WebhookConfigurationValidationException extends Exception
{
    public WebhookConfigurationValidationException(final String message)
    {
        super(message);
    }

    public WebhookConfigurationValidationException(final String message, final Throwable t)
    {
        super(message, t);
    }
}