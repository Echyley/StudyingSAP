/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;

import com.google.common.base.MoreObjects;

/**
 * Event indicates that a single item change was not processed. This is because it happened to an item that is related to the
 * root item in the integration object but itself is not included in the integration object; or, if the outbound sync does not
 * process this kind of changes, e.g. DELETE.
 */
@Immutable
public final class IgnoredOutboundSyncEvent extends OutboundSyncEvent
{
	private static final long serialVersionUID = 4012327689562441386L;

	private final transient OutboundItemDTO changedItem;

	/**
	 * Instantiates this event
	 *
	 * @param item item change that has been ignored
	 */
	public IgnoredOutboundSyncEvent(@NotNull final OutboundItemDTO item)
	{
		this(item.getCronJobPK(), item);
	}

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk primary key of the job, for which the change was ignored
	 */
	public IgnoredOutboundSyncEvent(@NotNull final PK cronJobPk)
	{
		this(cronJobPk, null);
	}

	private IgnoredOutboundSyncEvent(@NotNull final PK jobPk, final OutboundItemDTO item)
	{
		super(jobPk);
		changedItem = item;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o != null && getClass() == o.getClass())
		{
			final IgnoredOutboundSyncEvent that = (IgnoredOutboundSyncEvent) o;
			return getCronJobPk().equals(that.getCronJobPk());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getCronJobPk().hashCode();
	}

	@Override
	public String toString()
	{
		final Long itemPK = changedItem == null ? null : changedItem.getItem().getPK();
		return MoreObjects.toStringHelper(this)
		                  .add("cronJobPk", getCronJobPk())
		                  .add("itemPK", itemPK)
		                  .toString();
	}
}
