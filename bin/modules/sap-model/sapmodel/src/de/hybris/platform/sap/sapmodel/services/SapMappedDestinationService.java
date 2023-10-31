/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services;

import java.util.Optional;

import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.sap.sapmodel.enums.SAPDestinationKey;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Service layer interface for destinations
 *
 * @param <T> the type parameter which extends the {@link AbstractDestinationModel} type
 */
public interface SapMappedDestinationService<T extends AbstractDestinationModel>
{
	/**
	 * Get the destination for a specific destination key from SAPConsumedDestinationEntry
	 *
	 * @param baseStoreModel a BaseStoreModel
	 * @param destinationKey destination key to fetch associated consumed destination
	 * @return Optional.empty() if no destination found and Optional.of(destination) if destination found
	 */
	Optional<T> getDestinationByKeyForBaseStore(BaseStoreModel baseStoreModel, SAPDestinationKey destinationKey);


}
