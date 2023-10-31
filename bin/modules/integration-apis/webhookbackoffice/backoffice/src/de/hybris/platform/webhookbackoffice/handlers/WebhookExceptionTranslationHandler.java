/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.handlers;

import de.hybris.platform.apiregistryservices.exceptions.DestinationCredNotMatchException;
import de.hybris.platform.integrationbackoffice.exceptionhandlers.IntegrationApiExceptionTranslationHandler;
import de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants;
import de.hybris.platform.webhookservices.exceptions.CannotDeleteIntegrationObjectLinkedWithWebhookConfigException;
import de.hybris.platform.webhookservices.exceptions.DestinationTargetNoSupportedEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotValidLocationException;

import java.util.Collection;
import java.util.Set;

/**
 * Handler that translates exceptions so they appear in the Backoffice
 */
public class WebhookExceptionTranslationHandler extends IntegrationApiExceptionTranslationHandler
{
	private static final Set<Class<? extends Throwable>> TARGETED_EXCEPTIONS = Set.of(
			DestinationCredNotMatchException.class,
			WebhookConfigNotValidLocationException.class,
			WebhookConfigInvalidChannelException.class,
			DestinationTargetNoSupportedEventConfigException.class,
			WebhookConfigNoEventConfigException.class,
			CannotDeleteIntegrationObjectLinkedWithWebhookConfigException.class);

	@Override
	protected final Collection<Class<? extends Throwable>> getTargetedExceptions()
	{
		return TARGETED_EXCEPTIONS;
	}

	@Override
	protected String getKeyPrefix()
	{
		return WebhookbackofficeConstants.EXTENSIONNAME;
	}
}
