/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.GraphOperationsFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectGraphOperations;

/**
 * A default implementation that returns {@link IntegrationObjectGraphSearch} as implementation of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectGraphOperations}
 */
public class DefaultGraphOperationsFactory implements GraphOperationsFactory
{
	@Override
	public IntegrationObjectGraphOperations create(final IntegrationObjectDescriptor io)
	{
		return new IntegrationObjectGraphSearch(io);
	}
}
