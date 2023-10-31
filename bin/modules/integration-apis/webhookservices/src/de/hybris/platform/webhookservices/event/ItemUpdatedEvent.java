/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.tx.AfterSaveEvent;

import java.util.Objects;

/**
 * A {@link WebhookEvent}  that indicates an item update in the platform
 */
public class ItemUpdatedEvent extends BaseWebhookEvent
{

	/**
	 * Instantiates an ItemUpdatedEvent
	 *
	 * @param event The {@link AfterSaveEvent} that is wrapped.
	 */
	public ItemUpdatedEvent(final AfterSaveEvent event)
	{
		super(event.getPk(), DefaultEventType.UPDATED);
	}

	/**
	 * Instantiates an ItemUpdatedEvent
	 *
	 * @param event                      The {@link AfterSaveEvent} that is wrapped.
	 * @param createdFromNestedItemEvent boolean indicating if this is a parent event the was created by a nested child event
	 */
	public ItemUpdatedEvent(final AfterSaveEvent event, final boolean createdFromNestedItemEvent)
	{
		super(event.getPk(), DefaultEventType.UPDATED, createdFromNestedItemEvent);
	}

	@Override
	public String toString()
	{
		return "ItemUpdatedEvent{pk='" + this.getPk() + "', type='" + this.getEventType()
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

		final ItemUpdatedEvent that = (ItemUpdatedEvent) o;
		return this.getPk()
		           .equals(that.getPk()) && this.isCreatedFromNestedItemEvent() == that.isCreatedFromNestedItemEvent();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getEventType(), getPk(), isCreatedFromNestedItemEvent());
	}
}

