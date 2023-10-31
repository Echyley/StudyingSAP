/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.MoreObjects;

/**
 * Event triggered for every item, for which synchronization has been attempted during a cron job run.
 */
@Immutable
public final class CompletedOutboundSyncEvent extends ItemGroupOutboundSyncEvent
{
	private static final long serialVersionUID = 7774753575770188141L;
	private static final boolean SUCCESSFUL_SYNC = true;

	private final boolean success;
	private final int changesCompleted;

	/**
	 * Instantiates this event.
	 *
	 * @param cronJobPk  primary key of the job that processed the item synchronization
	 * @param success    {@code true}, if synchronization was successful; {@code false} otherwise.
	 * @param changesCnt number of changes processed during the item synchronization.
	 */
	public CompletedOutboundSyncEvent(final PK cronJobPk, final boolean success, final int changesCnt)
	{
		this(cronJobPk, success, null, changesCnt);
	}

	/**
	 * Instantiates this event.
	 *
	 * @param group   a group of items processed by the outbound sync and reported in this event.
	 * @param success {@code true}, if synchronization of the items was successful; {@code false} otherwise.
	 */
	public CompletedOutboundSyncEvent(@NotNull final OutboundItemDTOGroup group, final boolean success)
	{
		this(group.getCronJobPk(), success, group, group.getItemsCount());
	}

	/**
	 * Instantiates this event.
	 *
	 * @param groups  a Collection of groups of items processed by the outbound sync and reported in this event.
	 * @param success {@code true}, if synchronization of the items was successful; {@code false} otherwise.
	 */
	public CompletedOutboundSyncEvent(@NotNull final Collection<OutboundItemDTOGroup> groups, final boolean success)
	{

		this(getCronJobPk(groups), success,
				groups.stream()
				      .map(OutboundItemDTOGroup::getItemsCount)
				      .reduce(0, Integer::sum));
	}

	private static PK getCronJobPk(final Collection<OutboundItemDTOGroup> groups)
	{
		final Optional<OutboundItemDTOGroup> firstGroup = groups.stream().findFirst();
		return firstGroup.isPresent() ? firstGroup.get().getCronJobPk() : null;
	}

	private CompletedOutboundSyncEvent(final PK cronJobPk, final boolean flag, final OutboundItemDTOGroup group, final int cnt)
	{
		super(cronJobPk, group);
		success = flag;
		changesCompleted = cnt;
	}

	/**
	 * Creates an instance of the {@code CompletedOutboundSyncEvent} for successful synchronization of a single item.
	 *
	 * @param item an item that was successfully synchronized with an external system.
	 * @return a new intance of the event for successful synchronization of a single item.
	 */
	public static CompletedOutboundSyncEvent successfulSync(final OutboundItemDTO item)
	{
		final var numberOfItems = 1;
		return new CompletedOutboundSyncEvent(item.getCronJobPK(), SUCCESSFUL_SYNC, numberOfItems);
	}

	/**
	 * Determines whether the item was synchronized successfully or not.
	 *
	 * @return {@code true}, if synchronization was successful; {@code false} otherwise.
	 */
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * Retrieves number of changes processed for the item. It's possible that single item synchronizes multiple changes, which is
	 * especially true in cases when items related to the root integration object item change. For example, in case of multiple order
	 * entries changes, only one order entry item will be synchronized for all those changes.
	 *
	 * @return number of changes processed for the item.
	 */
	public int getChangesCompleted()
	{
		return changesCompleted;
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
			final CompletedOutboundSyncEvent that = (CompletedOutboundSyncEvent) o;
			return new EqualsBuilder().append(getCronJobPk(), that.getCronJobPk())
			                          .append(success, that.success)
			                          .append(changesCompleted, that.changesCompleted)
			                          .build();
		}
		return false;

	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(getCronJobPk())
				.append(isSuccess())
				.append(getChangesCompleted())
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
		                  .add("cronJobPk", getCronJobPk())
		                  .add("success", success)
		                  .add("changesCompleted", changesCompleted)
		                  .add("itemPKs", getGroupItemPKs())
		                  .toString();
	}
}
