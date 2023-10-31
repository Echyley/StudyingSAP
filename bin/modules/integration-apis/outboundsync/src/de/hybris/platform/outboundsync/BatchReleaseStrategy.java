/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.config.impl.OutboundSyncConfiguration;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;
import de.hybris.platform.outboundsync.events.StartedOutboundSyncEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.springframework.integration.annotation.ReleaseStrategy;

import com.google.common.base.Preconditions;

/**
 * Defines a release strategy to be used when aggregating {@link OutboundItemDTOGroup}s.
 * It is responsible for determining if the aggregation is complete.
 */
public class BatchReleaseStrategy extends AbstractEventListener<StartedOutboundSyncEvent>
{
	private final OutboundSyncConfiguration outboundSyncConfiguration;
	private final Map<PK, Integer> changedItemCount = new ConcurrentHashMap<>();

	protected BatchReleaseStrategy(@NotNull final OutboundSyncConfiguration configuration)
	{
		Preconditions.checkArgument(configuration != null, "OutboundSyncConfiguration cannot be null");
		outboundSyncConfiguration = configuration;
	}

	/**
	 * Determines if a list of itemDTOGroups can be released from aggregation.
	 *
	 * @param itemDTOGroups list of item groups to be aggregated
	 * @return {@code true} if the aggregation can be released, {@code false} if not
	 */
	@ReleaseStrategy
	public boolean canMessagesBeReleased(final Collection<OutboundItemDTOGroup> itemDTOGroups)
	{

		final Optional<OutboundItemDTOGroup> optionalDTOGroup = itemDTOGroups.stream().findFirst();
		if (optionalDTOGroup.isPresent())
		{
			final PK cronJobPk = optionalDTOGroup.get().getCronJobPk();
			final int aggregatedItemsSize = itemDTOGroups.stream()
			                                             .map(OutboundItemDTOGroup::getItemsCount)
			                                             .mapToInt(Integer::intValue)
			                                             .sum();
			final Integer cronJobChangedItemCount = this.changedItemCount.get(cronJobPk);
			if (hasReachedConfigurableLimit(itemDTOGroups.size(), cronJobPk)
					|| matchesNumberOfDetectedItemChanges(aggregatedItemsSize, cronJobChangedItemCount, cronJobPk))
			{
				return true;
			}
		}
		return false;
	}

	private boolean matchesNumberOfDetectedItemChanges(final Integer changeSetCount, final Integer changedItemCount, final PK pk)
	{
		if (changeSetCount >= changedItemCount)
		{
			this.changedItemCount.remove(pk);
			return true;
		}
		return false;
	}

	private boolean hasReachedConfigurableLimit(final int count, final PK pk)
	{
		if (outboundSyncConfiguration.getOutboundBatchLimit() == count)
		{
			final int updatedChangeItemCount = this.changedItemCount.get(pk) - count;
			changedItemCount.put(pk, updatedChangeItemCount);
			return true;
		}
		return false;
	}

	@Override
	protected void onEvent(final StartedOutboundSyncEvent startedOutboundSyncEvent)
	{
		changedItemCount.put(startedOutboundSyncEvent.getCronJobPk(), startedOutboundSyncEvent.getItemCount());
	}
}