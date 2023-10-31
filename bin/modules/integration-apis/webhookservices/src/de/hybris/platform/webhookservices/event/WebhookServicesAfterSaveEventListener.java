/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.event;

import de.hybris.platform.core.Registry;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.integrationservices.util.lifecycle.TenantLifecycle;
import de.hybris.platform.servicelayer.event.EventSender;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import de.hybris.platform.webhookservices.service.WebhookConfigurationService;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * An AfterSaveEvent listener that converts the {@link AfterSaveEvent} to {@link WebhookEvent}s
 * before sending it to the {@link EventSender}
 */
public class WebhookServicesAfterSaveEventListener implements AfterSaveListener
{
	private static final Logger LOGGER = Log.getLogger(WebhookServicesAfterSaveEventListener.class);
	private final TenantLifecycle tenantLifecycle;
	private final WebhookEventFactory webhookEventFactory;
	private final RootEventSender rootItemEventSender;

	/**
	 * Instantiates a {@link WebhookServicesAfterSaveEventListener}
	 *
	 * @param eventSender         An event sender that will send {@link de.hybris.platform.servicelayer.event.events.AbstractEvent}
	 * @param tenantLifecycle     TenantLifecycle to regulate the operation of this listener
	 * @param webhookEventFactory A factory to create {@link WebhookEvent}s
	 * @deprecated Since 2205. Use the new constructor instead.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public WebhookServicesAfterSaveEventListener(@NotNull final EventSender eventSender,
	                                             @NotNull final TenantLifecycle tenantLifecycle,
	                                             @NotNull final WebhookEventFactory webhookEventFactory)
	{
		this(Registry.getApplicationContext().getBean("rootEventSender", RootEventSender.class), tenantLifecycle, webhookEventFactory);
	}

	/**
	 * Instantiates a {@link WebhookServicesAfterSaveEventListener}
	 *
	 * @param tenantLifecycle     TenantLifecycle to regulate the operation of this listener
	 * @param webhookEventFactory A factory to create {@link WebhookEvent}s
	 * @param rootEventSender An event sender that will send {@link WebhookEvent}
	 */
	public WebhookServicesAfterSaveEventListener(@NotNull final RootEventSender rootEventSender,
	                                             @NotNull final TenantLifecycle tenantLifecycle,
	                                             @NotNull final WebhookEventFactory webhookEventFactory)
	{
		Preconditions.checkArgument(tenantLifecycle != null, "tenantLifecycle cannot be null");
		Preconditions.checkArgument(webhookEventFactory != null, "webhookEventFactory cannot be null");
		Preconditions.checkArgument(rootEventSender != null, "rootEventSender cannot be null");
		this.tenantLifecycle = tenantLifecycle;
		this.webhookEventFactory = webhookEventFactory;
		this.rootItemEventSender = rootEventSender;
	}

	/**
	 * {@inheritDoc}
	 * Converts each {@link AfterSaveEvent} into {@link WebhookEvent}s
	 *
	 * @param events A collection of {@link AfterSaveEvent}
	 */
	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		if (tenantLifecycle.isOperational())
		{
			events.forEach(this::convertAndSendEvent);
		}
	}


	private void convertAndSendEvent(final AfterSaveEvent event)
	{
		LOGGER.trace("Event {}", event);
		webhookEventFactory.create(event)
		                   .forEach(rootItemEventSender::send);
	}

	/**
	 * Sets the WebhookConfigurationService
	 *
	 * @param webhookConfigurationService WebhookConfigurationService to search for WebhookConfigurations
	 * @deprecated Since 2205. No longer used.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setWebhookConfigurationService(final WebhookConfigurationService webhookConfigurationService)
	{
		//kept for backwards compatibility
	}

	/**
	 * Sets the ModelService
	 *
	 * @param modelService ModelService to search for items
	 * @deprecated Since 2205. No longer used.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setModelService(final ModelService modelService)
	{
		//kept for backwards compatibility
	}
}

