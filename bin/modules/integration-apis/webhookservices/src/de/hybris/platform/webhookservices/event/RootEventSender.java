/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import javax.validation.constraints.NotNull;


/**
 * A gateway for entering the Spring Integrations channels for processing the webhook events.
 */
public interface RootEventSender
{
	/**
	 * Sends the specified event into the Spring Integration channel associated with this gateway for processing
	 *
	 * @param event the {@link WebhookEvent} carrying details about the item change to notify the webhooks about
	 */
	void send(@NotNull WebhookEvent event);
}
