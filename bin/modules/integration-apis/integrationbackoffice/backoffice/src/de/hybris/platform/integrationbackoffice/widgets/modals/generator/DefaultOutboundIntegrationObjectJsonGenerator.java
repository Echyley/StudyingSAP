/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.MetadataPrimitiveData;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;

public class DefaultOutboundIntegrationObjectJsonGenerator extends DefaultAbstractIntegrationObjectJsonGenerator
{
	public DefaultOutboundIntegrationObjectJsonGenerator(final ReadService readService,
	                                                     final ReadOnlyAttributesConfiguration configuration)
	{
		super(readService, configuration);
	}

	@Override
	List<AbstractIntegrationObjectItemAttributeModel> getAllAttributes(final IntegrationObjectItemModel integrationObjectItem)
	{
		final List<AbstractIntegrationObjectItemAttributeModel> attributes = new ArrayList<>(
				integrationObjectItem.getAttributes());
		attributes.addAll(integrationObjectItem.getClassificationAttributes());
		attributes.addAll(integrationObjectItem.getVirtualAttributes());
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
		else if (attribute instanceof IntegrationObjectItemClassificationAttributeModel)
		{
			primitiveData = determineClassificationType((IntegrationObjectItemClassificationAttributeModel) attribute);
		}
		else
		{
			primitiveData = determineVirtualType((IntegrationObjectItemVirtualAttributeModel) attribute);
		}
		return primitiveData;
	}

	private MetadataPrimitiveData determineVirtualType(final IntegrationObjectItemVirtualAttributeModel virtualAttribute)
	{
		final IntegrationObjectVirtualAttributeDescriptorModel virtualAttributeDescriptor = virtualAttribute.getRetrievalDescriptor();
		final Class classType = ((AtomicTypeModel) virtualAttributeDescriptor.getType()).getJavaClass();
		return buildMetadataPrimitiveData(virtualAttribute, classType);
	}
}
