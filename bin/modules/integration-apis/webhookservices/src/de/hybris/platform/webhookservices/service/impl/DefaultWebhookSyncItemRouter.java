/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.event.EventSender;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;
import de.hybris.platform.webhookservices.service.WebhookSyncItemRouter;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link WebhookSyncItemRouter} to route only {@link WebhookEvent}s which are also {@link AbstractEvent}
 */
public class DefaultWebhookSyncItemRouter implements WebhookSyncItemRouter
{
	private static final Log LOGGER = Log.getLogger(DefaultWebhookSyncItemRouter.class);
	private final EventSender eventSender;

	/**
	 * Instantiates a {@link DefaultWebhookSyncItemRouter}
	 *
	 * @param eventSender an EventSender to send each event
	 */
	public DefaultWebhookSyncItemRouter(@NotNull final EventSender eventSender)
	{
		Preconditions.checkArgument(eventSender != null, "eventSender cannot be null");
		this.eventSender = eventSender;
	}

	@Override
	public void route(final Collection<WebhookEvent> events)
	{
		if (CollectionUtils.isNotEmpty(events))
		{
			events.forEach(this::send);
		}
	}

	private void send(final WebhookEvent event)
	{
		final String eventInfo = String.valueOf(event);

		if (event instanceof AbstractEvent)
		{
			LOGGER.trace("Sending {}.", eventInfo);
			eventSender.sendEvent((AbstractEvent) event);
		}
		else
		{
			LOGGER.trace("Skipping {} because it is not an AbstractEvent.", eventInfo);
		}
	}
}
