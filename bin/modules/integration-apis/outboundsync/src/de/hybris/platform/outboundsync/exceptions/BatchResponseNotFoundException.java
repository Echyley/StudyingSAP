/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.exceptions;

import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;

public class BatchResponseNotFoundException extends RuntimeException
{
	private static final String MSG = "The OutboundItemDTOGroup with Change ID [%s] does not have a corresponding response " +
			"part in the batch response";

	private final OutboundItemDTOGroup group;

	/**
	 * Constructor for the exception
	 *
	 * @param g - {@link OutboundItemDTOGroup} that could not be updated
	 */
	public BatchResponseNotFoundException(final OutboundItemDTOGroup g)
	{
		super(String.format(MSG, g.getChangeId()));
		group = g;
	}

	/**
	 * Gets the group that caused the exception
	 *
	 * @return OutboundItemDTOGroup
	 */
	public OutboundItemDTOGroup getGroup()
	{
		return group;
	}
}
