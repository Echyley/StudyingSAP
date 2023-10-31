/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.router.impl;

import de.hybris.platform.outboundsync.activator.OutboundSyncService;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;
import de.hybris.platform.outboundsync.job.BatchChangeSender;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.router.OutboundSyncBatchRouter;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link OutboundSyncBatchRouter}.
 */
public class DefaultOutboundSyncBatchRouter implements OutboundSyncBatchRouter
{
	private final BatchChangeSender batchChangeSender;
	private final OutboundSyncService outboundSyncService;
	private final OutboundItemFactory outboundItemFactory;

	/**
	 * Instantiates this base class.
	 *
	 * @param batchChangeSender gateway for batch changes
	 * @param syncService       synchronization service for Item changes detected
	 * @param factory           OutboundItemDTOs to OutboundItemDTOGroup converter
	 */
	protected DefaultOutboundSyncBatchRouter(@NotNull final BatchChangeSender batchChangeSender,
	                                         @NotNull final OutboundSyncService syncService,
	                                         @NotNull final OutboundItemFactory factory)
	{
		Preconditions.checkArgument(batchChangeSender != null, "BatchChangeSender cannot be null");
		Preconditions.checkArgument(syncService != null, "OutboundSyncService cannot be null");
		Preconditions.checkArgument(factory != null, "OutboundItemFactory cannot be null");

		this.batchChangeSender = batchChangeSender;
		outboundSyncService = syncService;
		outboundItemFactory = factory;
	}

	@Override
	public void route(final Collection<OutboundItemDTO> outboundItemDTOs)
	{
		final boolean isSendBatch = outboundItemDTOs.stream()
		                                            .findFirst()
		                                            .filter(Predicate.not(OutboundItemDTO::isSynchronizeDelete))
		                                            .map(OutboundItemDTO::isBatch)
		                                            .orElse(false);
		if (isSendBatch)
		{
			batchChangeSender.send(asItemGroup(outboundItemDTOs));
		}
		else
		{
			outboundSyncService.sync(outboundItemDTOs);
		}
	}

	protected OutboundItemDTOGroup asItemGroup(final Collection<OutboundItemDTO> items)
	{
		return OutboundItemDTOGroup.from(items, outboundItemFactory, UUID.randomUUID().toString());
	}
}

