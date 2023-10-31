/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service;

import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;

import javax.annotation.Nullable;

/**
 * A service for retrieving integration object descriptors
 */
public interface IntegrationObjectDescriptorService
{
	/**
	 * Retrieves an integration object descriptor with the given code.
	 *
	 * @param code Integration object's code
	 * @return integration object descriptor with the given code or null if descriptor is not found
	 */
	@Nullable
	IntegrationObjectDescriptor retrieve(String code);
}
