/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job;

import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

import javax.validation.constraints.NotNull;

/**
 * Gateway that represents the starting point of aggregation of batch parts.
 */
public interface BatchChangeSender
{
	/**
	 * Sends the root item dto group to the specified spring channel.
	 */
	void send(@NotNull OutboundItemDTOGroup change);
}
