/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.MoreObjects;

/**
 * Event is triggered after a systemic error occurred. This event signals the processing of more
 * items should be stopped. There is a systemic error, and there is no reason to continue.
 */
@Immutable
public final class SystemErrorOutboundSyncEvent extends ItemGroupOutboundSyncEvent
{
	private static final long serialVersionUID = 8076316675438085883L;

	private final int changesProcessed;

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk  primary key of the job that was aborted.
	 * @param changesCnt number of changes processed by the job for the context item ignored during synchronization.
	 */
	public SystemErrorOutboundSyncEvent(@NotNull final PK cronJobPk, final int changesCnt)
	{
		this(cronJobPk, null, changesCnt);
	}

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk primary key of the job that was aborted.
	 * @param group     changed items being reported by this event
	 */
	public SystemErrorOutboundSyncEvent(@NotNull final PK cronJobPk, final OutboundItemDTOGroup group)
	{
		this(cronJobPk, Collections.singletonList(group), group != null ? group.getItemsCount() : 0);
	}

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk primary key of the job that was aborted.
	 * @param groups    list of changed items being reported by this event
	 */
	public SystemErrorOutboundSyncEvent(@NotNull final PK cronJobPk, final Collection<OutboundItemDTOGroup> groups)
	{
		this(cronJobPk, groups, groups != null
				? groups.stream()
				        .map(OutboundItemDTOGroup::getItemsCount)
				        .reduce(0, Integer::sum)
				: 0);
	}

	private SystemErrorOutboundSyncEvent(final PK jobPk, final Collection<OutboundItemDTOGroup> groups, final int changesCnt)
	{
		super(jobPk, groups);
		changesProcessed = changesCnt;
	}

	/**
	 * Retrieves number of changes related to the item ignored.
	 *
	 * @return number of changes related to the item ignored. At a minimum this value is expected to be 1, but the number can be
	 * greater when multiple items related to the item being synchronized changed. For example, if Customer is
	 * being synchronized because two of its Addresses changed, this value will be 2.
	 */
	public int getChangesProcessed()
	{
		return changesProcessed;
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
			final SystemErrorOutboundSyncEvent that = (SystemErrorOutboundSyncEvent) o;
			return getCronJobPk().equals(that.getCronJobPk())
					&& getChangesProcessed() == that.getChangesProcessed();
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder()
				.append(getCronJobPk())
				.append(getChangesProcessed())
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
		                  .add("cronJobPk", getCronJobPk())
		                  .add("itemsProcessed", getChangesProcessed())
		                  .add("itemPKs", getGroupItemPKs())
		                  .toString();
	}
}
