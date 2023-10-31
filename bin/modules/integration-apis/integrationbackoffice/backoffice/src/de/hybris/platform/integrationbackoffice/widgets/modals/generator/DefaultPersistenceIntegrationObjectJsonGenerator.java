/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.MetadataPrimitiveData;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultPersistenceIntegrationObjectJsonGenerator extends DefaultAbstractIntegrationObjectJsonGenerator
{
	public DefaultPersistenceIntegrationObjectJsonGenerator(final ReadService readService,
	                                                        final ReadOnlyAttributesConfiguration configuration)
	{
		super(readService, configuration);
	}

	@Override
	List<AbstractIntegrationObjectItemAttributeModel> getAllAttributes(final IntegrationObjectItemModel integrationObjectItem)
	{
		final List<AbstractIntegrationObjectItemAttributeModel> attributes = integrationObjectItem
				.getAttributes()
				.stream()
				.filter(attribute -> !configuration.getReadOnlyAttributes().contains(attribute.getAttributeName()))
				.collect(Collectors.toList());
		attributes.addAll(integrationObjectItem.getClassificationAttributes());
		return attributes;
	}

	@Override
	MetadataPrimitiveData composeMetadataPrimitiveData(final AbstractIntegrationObjectItemAttributeModel attribute,
	                                                   final boolean isFromMap)
	{
		final MetadataPrimitiveData primitiveData;
		if (attribute instanceof IntegrationObjectItemAttributeModel)
		{
			primitiveData = determinePrimitiveType((IntegrationObjectItemAttributeModel) attribute, isFromMap, true);
		}
		else
		{
			primitiveData = determineClassificationType((IntegrationObjectItemClassificationAttributeModel) attribute);
		}
		return primitiveData;
	}
}
