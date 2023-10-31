/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event;

import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.tx.AfterSaveEvent;

import java.util.Objects;

import com.google.common.base.Preconditions;


/**
 * A {@link WebhookEvent} that indicates an item deleted in the platform.
 */
public class ItemDeletedEvent extends BaseWebhookEvent
{
	/**
	 * Instantiates an ItemDeletedEvent.
	 *
	 * @param event The {@link AfterSaveEvent} that is wrapped.
	 */
	public ItemDeletedEvent(final AfterSaveEvent event)
	{
		super(event.getPk(), DefaultEventType.DELETED);
		Preconditions.checkArgument(event.getType() == AfterSaveEvent.REMOVE, "AfterSaveEvent must have REMOVE type.");
	}

	@Override
	public String toString()
	{
		return "ItemDeletedEvent{pk='" + this.getPk() + "', type='" + this.getEventType() + "'}";
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

		final ItemDeletedEvent that = (ItemDeletedEvent) o;
		return this.getPk().equals(that.getPk());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getClass(), getPk());
	}
}
