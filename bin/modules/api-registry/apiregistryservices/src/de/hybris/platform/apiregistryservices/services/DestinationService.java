/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.exceptions.DestinationNotFoundException;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Service layer interface for destinations
 * 
 * @param <T>
 *           the type parameter which extends the {@link AbstractDestinationModel} type
 */
public interface DestinationService<T extends AbstractDestinationModel>
{
	/**
	 * Find destinations for specific channel
	 *
	 * @param channel
	 *           the channel of the destination
	 * @return List of destinations for the given channel
	 * @deprecated since 1905. Use {@link DestinationService#getDestinationsByDestinationTargetId(String)}
	 */
	@Deprecated(since = "1905", forRemoval = true)
	List<T> getDestinationsByChannel(DestinationChannel channel);

	/**
	 * Find destinations for specific destinationTarget
	 * 
	 * @param destinationTargetId
	 *           the id of the DestinationTarget
	 * @return List of destinations for the given DestinationTarget
	 */
	List<T> getDestinationsByDestinationTargetId(String destinationTargetId);


	/**
	 * Find the list of active destinations for specific clientId
	 *
	 * @param clientId
	 *           The clientId of OAuthClientDetails
	 * @return a List of Destinations by the ExposedOAuthCredential clientId
	 */
	List<ExposedDestinationModel> getActiveExposedDestinationsByClientId(final String clientId);

	/**
	 * Find the list of destinations for specific channel
	 *
	 * @param channel
	 *           The channel assigned to Destinations
	 * @return a List of Destinations by the credential
	 * @deprecated since 1905. Use {@link DestinationService#getActiveExposedDestinationsByDestinationTargetId(String)}
	 */
	@Deprecated(since = "1905", forRemoval = true)
	List<ExposedDestinationModel> getActiveExposedDestinationsByChannel(DestinationChannel channel);

	/**
	 * Since 1905, the id attribute of the destination is not unique, therefore, this gets the first item of the destinations for
	 * specific id.
	 * 
	 * @param id
	 *           the id of the destination
	 * @return The destination for the given id
	 * @deprecated since 1905. Use {@link DestinationService#getDestinationByIdAndByDestinationTargetId(String, String)}
	 */
	@Deprecated(since = "1905", forRemoval = true)
	T getDestinationById(String id);


	/**
	 * Get all destinations
	 *
	 * @return the list of destinations
	 */
	List<T> getAllDestinations();

	/**
	 * Get the list of active exposed destinations for a specific destination target
	 *
	 * @param destinationTargetId
	 * 		id of the destination target
	 * @return a list of exposed destinations
	 */
	List<ExposedDestinationModel> getActiveExposedDestinationsByDestinationTargetId(final String destinationTargetId);

	/**
	 * Get the destination for a specific destination and a specific destination id
	 *
	 * @param destinationId
	 * 		id of the destination
	 * @param destinationTargetId
	 * 		id of the destination target
	 * @return a destination
	 */
	T getDestinationByIdAndByDestinationTargetId(String destinationId, String destinationTargetId);

	/**
	 * Testing the consumed destination url
	 * @param  destinationModel
	 *         given consumedDestination model or exposedDestination model
	 * @throws DestinationNotFoundException
	 *         will be thrown in case of any exception during testing the consumed destination url
	 */
	void testDestinationUrl(AbstractDestinationModel destinationModel) throws DestinationNotFoundException;

	/**
	 * Get all consumed destinations
	 * @return the list of consumed destinations
	 */
	List<ConsumedDestinationModel> getAllConsumedDestinations();

	/**
	 * Get all exposed destinations by credential id
	 *
	 * @param credentialId id of the credential
	 * @return the list of exposed destinations
	 */
	List<ExposedDestinationModel> getExposedDestinationsByCredentialId(String credentialId);

	/**
	 * Get all {@link DestinationModel}s by credential id
	 *
	 * @param credentialId id of {@link AbstractCredentialModel}
	 * @return list of destinations
	 */
	default List<T> getAllDestinationsByCredentialId(final String credentialId)
	{
		return Collections.emptyList();
	}

	/**
	 * Get all credentials that can be deleted
	 * A credential can be deleted means it is consumed by one destination or
	 * multiple destinations with same destination target
	 *
	 * @param destinationTargetId id of {@link DestinationTargetModel}
	 * @return set of {@link AbstractCredentialModel}
	 */
	default Set<AbstractCredentialModel> getDeletableCredentialsByDestinationTargetId(final String destinationTargetId)
	{
		return Collections.emptySet();
	}
}
