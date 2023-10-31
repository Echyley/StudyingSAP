/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Optional;

/**
 * Service layer implementation for Consumed Destination, used to lookup if item model is assigned a Consumed Destination
 */
public interface ConsumedDestinationVerifyUsageService
{
	/**
	 * Method which looks up if the Consumed Destination was assigned to item.
	 * @param typeCode item type code
	 * @param attributeName attribute was referred to Consumed Destination
	 * @param consumedDestinationModel Consumed Destination
	 * @return a list of item models if assigned Consumed Destination otherwise Optional.empty is returned
	 */
	Optional<List<ItemModel>> findModelsAssignedConsumedDestination(final String typeCode, final String attributeName, final
	                                                            ConsumedDestinationModel consumedDestinationModel);
}
