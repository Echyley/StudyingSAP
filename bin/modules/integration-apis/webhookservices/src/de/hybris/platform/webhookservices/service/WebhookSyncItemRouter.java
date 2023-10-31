/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service;

import de.hybris.platform.webhookservices.event.WebhookEvent;

import java.util.Collection;

/**
 * A router for directing the items change processing into an appropriate Spring Integration channel.
 */
public interface WebhookSyncItemRouter
{
	/**
	 * Method that routes events to be sent out via webhook
	 *
	 * @param events the collection of events to be routed
	 */
	void route(Collection<WebhookEvent> events);
}
