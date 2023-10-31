/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.activator;

import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

import java.util.Collection;

/**
 * Service Activator interface that handles synchronization of Item changes detected and placed in a spring integration channel.
 */
public interface OutboundSyncService
{
	/**
	 * Consumes a DTO with the item changed information and handles the message based on the change type.
	 *
	 * @param outboundItemDTOs A collection of DTOs with the information about the changes in the item.
	 */
	void sync(Collection<OutboundItemDTO> outboundItemDTOs);

	/**
	 * Consumes all DTOs and handles the message for each item based on the change type.
	 *
	 * @param outboundItemDTOGroups An entity holding information about the changed items for a single batch request
	 */
	default void syncBatch(final Collection<OutboundItemDTOGroup> outboundItemDTOGroups)
	{
	}
}