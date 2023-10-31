/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.dto;

import de.hybris.platform.webhookservices.event.WebhookEvent;


/**
 * The webhook event aggregator's correlation strategy.
 */
public class EventRootItemCorrelationStrategy
{
	/**
	 * Provides correlation key based on which {@link WebhookEvent}s can be grouped.
	 *
	 * @param webhookEvent - an event to calculate the correlation key for.
	 * @return - correlation key corresponding to the provided event.
	 */
	public String correlationKey(final WebhookEvent webhookEvent)
	{
		return webhookEvent == null ? "" : String.valueOf(webhookEvent.getPk().getLong());
	}
}
