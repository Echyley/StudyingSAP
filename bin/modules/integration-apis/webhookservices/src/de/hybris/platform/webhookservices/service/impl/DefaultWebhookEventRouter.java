/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.service.WebhookEventRouter;

import org.springframework.messaging.Message;


/**
 * The default webhook event router to route events based on the event type.
 */
public class DefaultWebhookEventRouter implements WebhookEventRouter
{
	private static final String DELETE_EVENT_CHANNEL = "deleteEventChannel";
	private static final String PERSISTENCE_EVENT_CHANNEL = "persistenceEventChannel";

	@Override
	public String route(final Message<EventSourceData> message)
	{
		return isDeleteItemEvent(message) ? DELETE_EVENT_CHANNEL : PERSISTENCE_EVENT_CHANNEL;
	}

	private boolean isDeleteItemEvent(final Message<EventSourceData> message)
	{
		final var eventSourceData = message.getPayload();
		return eventSourceData.getEvent() instanceof ItemDeletedEvent;
	}
}


