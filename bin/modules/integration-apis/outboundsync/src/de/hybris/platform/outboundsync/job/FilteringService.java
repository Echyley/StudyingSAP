/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.job.impl.StreamingChangesCollector;

import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * A filter service for all outbound item dto's collected by {@link StreamingChangesCollector}.
 */
public interface FilteringService
{
	/**
	 * Searches for an applicable {@link SyncFilter} from the syncFilterList and calls evaluate on the first applicable filter
	 * if any is found.  Consumes the item change dto and returns optional empty if an applicable {@link SyncFilter}
	 * filters it out when evaluating.
	 *
	 * @param dto - outbound item dto
	 * @param io - integration object
	 * @return - outbound item dto that will be sent to the spring configuration channel for synchronization, or an empty optional
	 */
	Optional<OutboundItemDTO> applyFilters(@NotNull OutboundItemDTO dto, @NotNull IntegrationObjectModel io);
}
