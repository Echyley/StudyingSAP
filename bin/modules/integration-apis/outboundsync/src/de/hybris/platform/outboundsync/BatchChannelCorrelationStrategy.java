/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;


import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Defines a correlation strategy to be used when aggregating {@link OutboundItemDTOGroup}s.
 * It takes into account the cron job PK.
 */
public class BatchChannelCorrelationStrategy
{
	public String correlationKey(final OutboundItemDTOGroup dtoGroup)
	{
		Preconditions.checkArgument(dtoGroup != null, "Cannot create correlation key with a null OutboundItemDTOGroup");
		return dtoGroup.getCronJobPk() == null
				? StringUtils.EMPTY
				: String.format("%d", dtoGroup.getCronJobPk().getLong());
	}
}