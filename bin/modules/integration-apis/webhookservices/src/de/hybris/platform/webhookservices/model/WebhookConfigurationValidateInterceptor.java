/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.model;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.integrationservices.scripting.LogicLocation;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.webhookservices.event.ItemCreatedEvent;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigEventNotSupportedException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventTypeException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotRegisteredEventException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotValidLocationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * This ValidateInterceptor validates the {@link WebhookConfigurationModel} is proper before saving it
 */
public final class WebhookConfigurationValidateInterceptor implements ValidateInterceptor<WebhookConfigurationModel>
{
	private static final List<String> SUPPORTED_EVENTS = List.of(ItemSavedEvent.class.getCanonicalName(),
			ItemCreatedEvent.class.getCanonicalName(),
			ItemUpdatedEvent.class.getCanonicalName(),
			ItemDeletedEvent.class.getCanonicalName());

	@Override
	public void onValidate(final WebhookConfigurationModel config, final InterceptorContext context) throws InterceptorException
	{
		if (!isEventTypeExisting(config))
		{
			throw new WebhookConfigNoEventTypeException(this);
		}
		if (!isEventConfigExisting(config))
		{
			throw new WebhookConfigNoEventConfigException(this);
		}
		if (!isSupportedEvent(config))
		{
			final var supportedEvent = String.join(",", SUPPORTED_EVENTS);
			throw new WebhookConfigEventNotSupportedException(config.getEventType(), supportedEvent, this);
		}
		if (!isRegisteredEvent(config))
		{
			throw new WebhookConfigNotRegisteredEventException(config.getEventType(), this);
		}
		if (!isValidScriptLocation(config))
		{
			throw new WebhookConfigNotValidLocationException(config.getFilterLocation(), this);
		}
	}

	private boolean isEventTypeExisting(final WebhookConfigurationModel config)
	{
		return config.getEventType() != null && !"".equals(config.getEventType());
	}

	private boolean isEventConfigExisting(final WebhookConfigurationModel config)
	{
		return !getEventConfigurationsFromWebhookConfig(config).isEmpty();
	}

	private boolean isSupportedEvent(final WebhookConfigurationModel config)
	{
		return SUPPORTED_EVENTS.contains(config.getEventType());
	}

	private boolean isRegisteredEvent(final WebhookConfigurationModel config)
	{
		return getEventConfigurationsFromWebhookConfig(config)
				.stream()
				.map(EventConfigurationModel::getEventClass)
				.anyMatch(clName -> clName.equals(config.getEventType()));
	}

	private boolean isValidScriptLocation(final WebhookConfigurationModel config)
	{
		return StringUtils.isEmpty(config.getFilterLocation()) || LogicLocation.isValid(config.getFilterLocation());
	}

	private Collection<EventConfigurationModel> getEventConfigurationsFromWebhookConfig(final WebhookConfigurationModel config)
	{
		return Optional.of(config)
		               .map(WebhookConfigurationModel::getDestination)
		               .map(ConsumedDestinationModel::getDestinationTarget)
		               .map(DestinationTargetModel::getEventConfigurations)
		               .orElseGet(Collections::emptyList);
	}
}
