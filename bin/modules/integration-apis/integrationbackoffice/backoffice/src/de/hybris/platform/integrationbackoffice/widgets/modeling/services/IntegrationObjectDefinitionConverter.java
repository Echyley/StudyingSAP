/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

/**
 * Converts an {@link IntegrationObjectModel} to a {@link IntegrationObjectDefinition}.
 */
public interface IntegrationObjectDefinitionConverter
{
	/**
	 * Converts an {@link IntegrationObjectModel} to a {@link IntegrationObjectDefinition}.
	 *
	 * @param integrationObject the {@link IntegrationObjectModel} to convert.
	 * @return an {@link IntegrationObjectDefinition} representation of the integration object.
	 */
	IntegrationObjectDefinition toDefinitionMap(IntegrationObjectModel integrationObject);
}
