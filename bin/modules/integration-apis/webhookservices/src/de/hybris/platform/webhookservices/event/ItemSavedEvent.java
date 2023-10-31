/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.tx.AfterSaveEvent;

import java.util.Objects;

/**
 * A saved event implementation of {@link WebhookEvent} and wraps the {@link AfterSaveEvent}
 */
public class ItemSavedEvent extends AbstractEvent implements WebhookEvent
{
	private static final long serialVersionUID = -5622198499441971419L;

	private final WebhookEvent event;
	private final boolean createdFromNestedItemEvent;

	/**
	 * Instantiates an ItemSavedEvent
	 *
	 * @param event The {@link AfterSaveEvent} that is wrapped.
	 */
	public ItemSavedEvent(final AfterSaveEvent event)
	{
		this(event, false);
	}

	/**
	 * Instantiates an ItemSavedEvent
	 *
	 * @param event                      The {@link AfterSaveEvent} that is wrapped.
	 * @param createdFromNestedItemEvent boolean indicating if this is a parent event the was created by a nested child item
	 */
	public ItemSavedEvent(final AfterSaveEvent event, final boolean createdFromNestedItemEvent)
	{
		this.event = convertAfterSaveEvent(event);
		this.createdFromNestedItemEvent = createdFromNestedItemEvent;
	}

	@Override
	public boolean isCreatedFromNestedItemEvent()
	{
		return createdFromNestedItemEvent;
	}

	@Override
	public PK getPk()
	{
		return event.getPk();
	}

	@Override
	public EventType getEventType()
	{
		return event.getEventType();
	}

	private WebhookEvent convertAfterSaveEvent(final AfterSaveEvent type)
	{
		final WebhookEvent unKnownEvent = new UnknownWebhookEvent(type);

		if (type != null)
		{
			switch (type.getType())
			{
				case AfterSaveEvent.UPDATE:
					return new ItemUpdatedEvent(type);
				case AfterSaveEvent.CREATE:
					return new ItemCreatedEvent(type);
				default:
					return unKnownEvent;
			}
		}
		return unKnownEvent;
	}

	@Override
	public String toString()
	{
		return "ItemSavedEvent{pk='" + this.getPk() + "', type='" + this.event.getEventType()
				+ "', createdFromNestedItemEvent=" + this.isCreatedFromNestedItemEvent() + "}";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final ItemSavedEvent that = (ItemSavedEvent) o;
		return this.event.getEventType().equals(that.event.getEventType()) && this.getPk()
		                                                                          .equals(that.getPk()) && this.isCreatedFromNestedItemEvent() == that.isCreatedFromNestedItemEvent();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(event, getPk(), createdFromNestedItemEvent);
	}

	private static class UnknownWebhookEvent extends BaseWebhookEvent
	{
		UnknownWebhookEvent(final AfterSaveEvent event)
		{
			super(event.getPk(), DefaultEventType.UNKNOWN);
		}
	}
}
