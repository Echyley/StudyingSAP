/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.tx.AfterSaveEvent;

import java.util.List;

/**
 *  A factory to create list of {@link WebhookEvent}s
 */
public interface WebhookEventFactory
{
	/**
	 * The method will create list of {@link WebhookEvent}s based on {@link AfterSaveEvent} event type
	 * @param event {@link AfterSaveEvent} event type
	 * @return {@link WebhookEvent}s if event exists, otherwise empty list
	 */
	List<WebhookEvent> create(AfterSaveEvent event);
}
