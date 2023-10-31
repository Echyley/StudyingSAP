/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.MoreObjects;

/**
 * Event is triggered for every item being processed after the job is aborted. It indicates that an item was picked up off the queue
 * but was ignored and was not attempted to synchronize. Before the job is aborted {@link CompletedOutboundSyncEvent} is fired
 * for every item processed.
 */
@Immutable
public final class AbortedOutboundSyncEvent extends ItemGroupOutboundSyncEvent
{
	private static final long serialVersionUID = -1884814569880336371L;

	private final int changesProcessed;

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk primary key of the job that was aborted.
	 * @param group     changed items being reported by this event
	 */
	public AbortedOutboundSyncEvent(final PK cronJobPk, final OutboundItemDTOGroup group)
	{
		this(cronJobPk, Collections.singletonList(group));
	}

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk primary key of the job that was aborted.
	 * @param groups    List of changed items being reported by this event
	 */
	public AbortedOutboundSyncEvent(final PK cronJobPk, final Collection<OutboundItemDTOGroup> groups)
	{
		this(cronJobPk, groups, groups != null
				? groups.stream()
				        .map(OutboundItemDTOGroup::getItemsCount)
				        .reduce(0, Integer::sum)
				: 0);
	}

	/**
	 * Instantiates this event
	 *
	 * @param cronJobPk  primary key of the job that was aborted.
	 * @param changesCnt number of changes processed by the job for the context item ignored during synchronization.
	 */
	public AbortedOutboundSyncEvent(final PK cronJobPk, final int changesCnt)
	{
		this(cronJobPk, null, changesCnt);
	}

	private AbortedOutboundSyncEvent(final PK jobPk, final Collection<OutboundItemDTOGroup> group, final int changesCnt)
	{
		super(jobPk, group);
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
			final AbortedOutboundSyncEvent that = (AbortedOutboundSyncEvent) o;
			return new EqualsBuilder()
					.append(getCronJobPk(), that.getCronJobPk())
					.append(getChangesProcessed(), that.getChangesProcessed())
					.build();
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
