/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundservices.event.EventType;

import java.io.Serializable;

/**
 * An event that triggers webhook notification
 */
public interface WebhookEvent extends Serializable
{
	/**
	 * Retrieves the changed item pk
	 *
	 * @return pk
	 */
	PK getPk();

	/**
	 * Retrieves the type of the event.
	 *
	 * @return {@link EventType}
	 */
	EventType getEventType();

	/**
	 * Indicates whether this event was created from a child event
	 *
	 * @return {@code true} if it's created from a child event, otherwise, {@code false}
	 * When {@code true}, then getPk() returns PK of root item instead of the changed item PK.
	 * When {@code false}, then it's PK of the changed item.
	 */
	default boolean isCreatedFromNestedItemEvent()
	{
		return false;
	}
}
