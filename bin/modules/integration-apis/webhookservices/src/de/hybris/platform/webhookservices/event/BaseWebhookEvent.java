/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import javax.validation.constraints.NotNull;

/**
 * Base event that implements {@link WebhookEvent}
 */
public class BaseWebhookEvent extends AbstractEvent implements WebhookEvent
{
	private static final long serialVersionUID = -1301182040061363987L;

	private final PK pk;
	private final EventType eventType;
	private final boolean createdFromNestedItemEvent;

	/**
	 * Instantiates an BaseWebhookEvent
	 *
	 * @param pk        {@link PK} of the found item.
	 * @param eventType event type of {@link EventType} that is wrapped.
	 */
	protected BaseWebhookEvent(@NotNull final PK pk, @NotNull final EventType eventType)
	{
		this(pk, eventType, false);
	}

	/**
	 * Instantiates an BaseWebhookEvent
	 *
	 * @param pk                         {@link PK} of the found item.
	 * @param eventType                  event type of {@link EventType} that is wrapped.
	 * @param createdFromNestedItemEvent boolean indicating if this is a parent event the was created by a nested child event
	 */
	protected BaseWebhookEvent(@NotNull final PK pk, @NotNull final EventType eventType, final boolean createdFromNestedItemEvent)
	{
		this.pk = pk;
		this.eventType = eventType;
		this.createdFromNestedItemEvent = createdFromNestedItemEvent;
	}

	@Override
	public PK getPk()
	{
		return pk;
	}

	@Override
	public EventType getEventType()
	{
		return eventType;
	}

	@Override
	public boolean isCreatedFromNestedItemEvent()
	{
		return createdFromNestedItemEvent;
	}
}