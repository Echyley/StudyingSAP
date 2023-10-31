/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.strategies.impl;

import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.services.CredentialService;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.strategies.DestinationTargetDeletionStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link DestinationTargetDeletionStrategy}
 */
public class DefaultDestinationTargetDeletionStrategy implements DestinationTargetDeletionStrategy
{
	private final ModelService modelService;
	private final DestinationService<AbstractDestinationModel> destinationService;
	private final EventConfigurationDao eventConfigurationDao;
	private final CredentialService credentialService;

	/**
	 * Constructor to create DefaultDestinationTargetDeletionStrategy
	 *
	 * @param modelService          a model service implementation to be used by this service
	 * @param destinationService    a service for searching {@link DestinationTargetModel}s
	 * @param eventConfigurationDao a DAO for the {@link EventConfigurationModel}
	 * @param credentialService     a service for searching {@link OAuthClientDetailsModel}
	 */
	public DefaultDestinationTargetDeletionStrategy(@NotNull final ModelService modelService,
	                                                @NotNull final DestinationService<AbstractDestinationModel> destinationService,
	                                                @NotNull final EventConfigurationDao eventConfigurationDao,
	                                                @NotNull final CredentialService credentialService)
	{
		Preconditions.checkArgument(modelService != null, "ModelService cannot be null");
		Preconditions.checkArgument(destinationService != null, "DestinationService cannot be null");
		Preconditions.checkArgument(eventConfigurationDao != null, "EventConfigurationDao cannot be null");
		Preconditions.checkArgument(credentialService != null, "CredentialService cannot be null");

		this.modelService = modelService;
		this.destinationService = destinationService;
		this.eventConfigurationDao = eventConfigurationDao;
		this.credentialService = credentialService;
	}

	@Override
	public void deleteDestinationTarget(final DestinationTargetModel destinationTarget)
	{
		Preconditions.checkArgument(destinationTarget != null, "DestinationTargetModel must not be null");

		final List<AbstractDestinationModel> destinations = destinationService
				.getDestinationsByDestinationTargetId(destinationTarget.getId());

		final List<EventConfigurationModel> eventConfigurations = eventConfigurationDao
				.findEventConfigsByDestinationTargetId(destinationTarget.getId());

		final Set<AbstractCredentialModel> credentialsToBeDeleted = destinationService
				.getDeletableCredentialsByDestinationTargetId(destinationTarget.getId());

		destinations.forEach(this::resetExposedDestinationTargetId);

		final Set<EndpointModel> endpointsToBeDeleted = getDeletableEndpoints(destinations, destinationTarget);

		final Set<OAuthClientDetailsModel> clientDetailsToBeDeleted = getDeletableClientDetails(credentialsToBeDeleted);

		modelService.removeAll(clientDetailsToBeDeleted);
		modelService.removeAll(endpointsToBeDeleted);
		modelService.removeAll(credentialsToBeDeleted);
		modelService.removeAll(destinations);
		modelService.removeAll(eventConfigurations);
		modelService.removeAll(destinationTarget);
	}

	DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	private void resetExposedDestinationTargetId(final AbstractDestinationModel destination)
	{
		if (destination instanceof ExposedDestinationModel)
		{
			((ExposedDestinationModel) destination).setTargetId(null);
		}
	}

	private Set<OAuthClientDetailsModel> getDeletableClientDetails(final Set<AbstractCredentialModel> credentialsToBeDeleted)
	{
		return credentialService.getDeletableClientDetailsByCredentials(
				credentialsToBeDeleted.stream()
				                      .filter(ExposedOAuthCredentialModel.class::isInstance)
				                      .map(ExposedOAuthCredentialModel.class::cast)
				                      .collect(Collectors.toSet()));
	}

	private Set<EndpointModel> getDeletableEndpoints(final List<AbstractDestinationModel> destinations,
	                                                 final DestinationTargetModel destinationTarget)
	{
		return destinations.stream()
		                   .filter(ConsumedDestinationModel.class::isInstance)
		                   .map(AbstractDestinationModel::getEndpoint)
		                   .filter(Objects::nonNull)
		                   .filter(endpoint -> this.isEndpointOnlyBelongingToDestinationTargetToBeDeleted(
				                   (List<AbstractDestinationModel>) endpoint.getDestinations(), destinationTarget))
		                   .collect(Collectors.toSet());
	}

	private boolean isEndpointOnlyBelongingToDestinationTargetToBeDeleted(final List<AbstractDestinationModel> destinations,
	                                                                      final DestinationTargetModel destinationTarget)
	{
		return destinations.stream()
		                   .allMatch(consumedDes -> consumedDes.getDestinationTarget().getId().equals(destinationTarget.getId()));
	}
}
