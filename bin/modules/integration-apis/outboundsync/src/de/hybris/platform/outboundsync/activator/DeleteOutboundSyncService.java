/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator;

import de.hybris.platform.outboundsync.dto.OutboundItemDTO;

/**
 * Service to synchronize item deletions
 */
public interface DeleteOutboundSyncService
{
	/**
	 * Synchronize the item deletion
	 * @param deletedItem Contains the data about the deleted item
	 */
	void sync(OutboundItemDTO deletedItem);
}
