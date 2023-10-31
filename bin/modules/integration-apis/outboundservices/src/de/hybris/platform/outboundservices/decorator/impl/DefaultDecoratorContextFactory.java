/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.enums.HttpMethod;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorContextFactory;
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the {@link DecoratorContextFactory}
 */
public class DefaultDecoratorContextFactory implements DecoratorContextFactory
{
	private static final String DEST_NOT_FOUND_ERROR_MSG = "Provided destination '%s' was not found.";

	private final DescriptorFactory descriptorFactory;

	/**
	 * @deprecated use {@link DefaultDecoratorContextFactory(DescriptorFactory)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public DefaultDecoratorContextFactory(final IntegrationObjectService integrationObjectService,
	                                      final FlexibleSearchService flexibleSearchService,
	                                      final DescriptorFactory descriptorFactory)
	{
		this(descriptorFactory);
	}

	/**
	 * Instantiate the DefaultDecoratorContextFactory.
	 *
	 * @param factory DescriptorFactory to create the IntegrationObjectDescriptor.
	 */
	public DefaultDecoratorContextFactory(final DescriptorFactory factory)
	{
		Preconditions.checkArgument(factory != null, "DescriptorFactory must be provided.");
		this.descriptorFactory = factory;
	}

	@Override
	public @NotNull DecoratorContext createContext(@NotNull final SyncParameters params)
	{
		final Collection<String> errors = new ArrayList<>();
		return DecoratorContext.decoratorContextBuilder()
		                       .withDestinationModel(getConsumedDestinationModel(params, errors))
		                       .withIntegrationObject(getIntegrationObjectDescriptor(params))
		                       .withPayloadObject(params.getPayloadObject())
		                       .withOutboundSource(params.getSource())
		                       .withErrors(errors)
		                       .withEventType(params.getEventType())
		                       .withHttpMethod(deriveHttpMethod(params))
		                       .withIntegrationKey(params.getIntegrationKey())
		                       .build();
	}

	private HttpMethod deriveHttpMethod(final SyncParameters params)
	{
		return params.getPayloadObject() == null ? HttpMethod.DELETE : HttpMethod.POST;
	}

	private ConsumedDestinationModel getConsumedDestinationModel(final SyncParameters params, final Collection<String> errors)
	{
		final ConsumedDestinationModel destination = params.getDestination();
		if (destination instanceof ConsumedDestinationNotFoundModel)
		{
			errors.add(String.format(DEST_NOT_FOUND_ERROR_MSG, destination.getId()));
		}
		return destination;
	}

	private IntegrationObjectDescriptor getIntegrationObjectDescriptor(final SyncParameters params)
	{
		final IntegrationObjectModel integrationObjectModel = params.getIntegrationObject();
		return descriptorFactory.createIntegrationObjectDescriptor(integrationObjectModel);
	}
}
