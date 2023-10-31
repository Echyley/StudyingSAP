/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.impl.StreamingChangesCollector;

import java.util.Optional;

/**
 * A filter for changes collected by {@link StreamingChangesCollector}, that passes the change through.
 * If the change is not applicable for synchronization then the change is filtered out.
 */
public interface SyncFilter
{
	/**
	 * If the evaluate method is applicable for the given outbound item DTO
	 *
	 * @param dto outbound item DTO
	 * @return applicable
	 */
	boolean isApplicable(OutboundItemDTO dto);

	/**
	 * Evaluates the filtering conditions against the specified item DTO.
	 *
	 * @param dto an item DTO to check against the filtering conditions.
	 * @param io integration object
	 * @return an {@code Optional} containing the {@code dto}, if it satisfies the filtering conditions; or an
	 * {@code Optional.empty()}, if it does not.
	 */
	Optional<OutboundItemDTO> evaluate(OutboundItemDTO dto, IntegrationObjectModel io);
}
