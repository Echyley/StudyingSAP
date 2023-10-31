/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;

import org.springframework.messaging.Message;


/**
 * A webhook event router to route events to the appropriate Spring integration channel.
 */
public interface WebhookEventRouter
{
	/**
	 * Routes events to be the appropriate Spring integration channel
	 *
	 * @param message the event message to route
	 * @return the Spring integration channel name
	 */
	String route(Message<EventSourceData> message);
}
