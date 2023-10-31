/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * The default implementation of {@link IntegrationObjectDescriptorService}
 */
public class DefaultIntegrationObjectDescriptorService implements IntegrationObjectDescriptorService
{
	private static final Logger LOG = Log.getLogger(DefaultIntegrationObjectDescriptorService.class);
	private final IntegrationObjectService service;
	private final DescriptorFactory factory;

	/**
	 * Instantiates the service for the integration object model search
	 *
	 * @param s IntegrationObjectService to be delegated to
	 * @param f DescriptorFactory used to create a descriptor for a given integration object item model
	 */
	public DefaultIntegrationObjectDescriptorService(final IntegrationObjectService s, final DescriptorFactory f)
	{
		Preconditions.checkArgument(s != null, "IntegrationObjectService must be provided to instantiate the service.");
		Preconditions.checkArgument(f != null, "DescriptorFactory must be provided to instantiate the service.");
		service = s;
		factory = f;
	}

	@Override
	public IntegrationObjectDescriptor retrieve(final String code)
	{
		final IntegrationObjectModel model;
		try
		{
			model = service.findIntegrationObject(code);
			return factory.createIntegrationObjectDescriptor(model);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.debug("Model for item code '{}' is not found.", code);
			return null;
		}
	}
}
