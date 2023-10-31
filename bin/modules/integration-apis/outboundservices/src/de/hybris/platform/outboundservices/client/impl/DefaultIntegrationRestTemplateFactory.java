/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateCreator;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestOperations;

import com.google.common.base.Preconditions;


/**
 * The default implementation of the factory to create rest template instance.
 */
public class DefaultIntegrationRestTemplateFactory implements IntegrationRestTemplateFactory
{
	private List<IntegrationRestTemplateCreator> restTemplateCreators = new ArrayList<>();

	@Override
	public RestOperations create(final ConsumedDestinationModel destination)
	{
		Preconditions.checkArgument(destination != null, "Consumed destination model cannot be null.");

		final IntegrationRestTemplateCreator creator = getRestTemplateCreators()
				.stream()
				.filter(s -> s.isApplicable(destination))
				.findFirst()
				.orElseThrow(UnsupportedRestTemplateException::new);

		return creator.create(destination);
	}

	protected List<IntegrationRestTemplateCreator> getRestTemplateCreators()
	{
		return new ArrayList<>(restTemplateCreators);
	}

	@Required
	public void setRestTemplateCreators(final List<IntegrationRestTemplateCreator> restTemplateCreators)
	{
		this.restTemplateCreators = restTemplateCreators != null ?
				List.copyOf(restTemplateCreators) :
				Collections.emptyList();
	}
}
