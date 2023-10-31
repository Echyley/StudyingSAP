/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model;

import javax.validation.constraints.NotNull;

/**
 * A factory for creating {@link IntegrationObjectGraphOperations} instances.
 */
public interface GraphOperationsFactory
{
	/**
	 * Creates an instance of {@link IntegrationObjectGraphOperations} for a given integration object.
	 *
	 * @param io an integration object to perform the graph operations on.
	 * @return an instance to use.
	 */
	@NotNull
	IntegrationObjectGraphOperations create(@NotNull IntegrationObjectDescriptor io);
}
