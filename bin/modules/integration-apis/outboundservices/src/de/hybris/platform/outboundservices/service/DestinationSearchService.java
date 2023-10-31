/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel;

import javax.validation.constraints.NotNull;

/**
 * A service that provides functions to search for a {@link ConsumedDestinationModel}.
 */
public interface DestinationSearchService
{
	/**
	 * This method searches the {@link ConsumedDestinationModel} for the given destination id.
	 *
	 * @param destinationId destination id against which the ConsumedDestinationModel is searched.
	 * @return the found ConsumedDestinationModel, or {@link ConsumedDestinationNotFoundModel} for the destination id if no
	 * destination model is found.
	 */
	@NotNull
	ConsumedDestinationModel findDestination(@NotNull final String destinationId);
}
