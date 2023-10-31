/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.model.StreamConfigurationModel;
import de.hybris.platform.outboundsync.dto.ChangeInfo;

import java.util.Optional;

/**
 * A parser that converts {@link ItemChangeDTO#getInfo()} string into a structured format.
 */
public interface ChangeInfoParser
{
	/**
	 * Parses info associated with an item change.
	 * @param info a string calculated from the {@link StreamConfigurationModel#getInfoExpression()}
	 * @return an optional containing the structured presentation of the parsed info; or {@code Optional.empty()}, if the info
	 * could not be parsed.
	 */
	Optional<ChangeInfo> parse(String info);
}
