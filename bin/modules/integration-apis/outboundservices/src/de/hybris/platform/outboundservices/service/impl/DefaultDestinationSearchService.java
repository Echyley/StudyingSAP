/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel;
import de.hybris.platform.outboundservices.service.DestinationSearchService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * The default implementation of {@link DestinationSearchService} which uses {@link FlexibleSearchService}.
 */
public class DefaultDestinationSearchService implements DestinationSearchService
{
	private static final Logger LOG = Log.getLogger(DefaultDestinationSearchService.class);

	private final FlexibleSearchService flexibleSearchService;

	/**
	 * To instantiate the {@link DefaultDestinationSearchService}.
	 *
	 * @param searchService the non-null {@link FlexibleSearchService} which will be used for searching.
	 */
	public DefaultDestinationSearchService(@NotNull final FlexibleSearchService searchService)
	{
		flexibleSearchService = Objects.requireNonNull(searchService, "searchService must be provided.");
	}

	/**
	 * This method searches the {@link ConsumedDestinationModel} for the given destination id.
	 *
	 * @param destinationId destination id against which the ConsumedDestinationModel is searched.
	 * @return the found ConsumedDestinationModel, or {@link ConsumedDestinationNotFoundModel} for the destination id if no
	 * destination model is found.
	 */
	@Override
	public ConsumedDestinationModel findDestination(@NotNull final String destinationId)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(destinationId), "destinationId cannot be empty or null.");
		try
		{
			final var example = new ConsumedDestinationModel();
			example.setId(destinationId);
			return flexibleSearchService.getModelByExample(example);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.warn("Failed to find ConsumedDestination with id '{}'", destinationId, e);
			return new ConsumedDestinationNotFoundModel(destinationId);
		}
	}
}
