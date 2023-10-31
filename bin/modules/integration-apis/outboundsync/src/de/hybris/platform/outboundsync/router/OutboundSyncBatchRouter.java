/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.router;

import de.hybris.platform.outboundsync.dto.OutboundItemDTO;

import java.util.Collection;

/**
 * Service to route batch versus non-batch outbound requests
 */
public interface OutboundSyncBatchRouter
{
	/**
	 * Routes the group of DTOs based of whether or not batch is enabled.
	 *
	 * @param outboundItemDTOs A collection of DTOs with the information about the changes in the item.
	 */
	void route(final Collection<OutboundItemDTO> outboundItemDTOs);
}