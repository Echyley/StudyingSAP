/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

/**
 * @deprecated Use an implementation of {@code SchemaElementGenerator<Set<EntityType>, TypeDescriptor>} instead.
 */
@Deprecated(since = "2211.FP1",forRemoval = true)
public class PrimitiveEntityTypeGenerator implements SchemaElementGenerator<Set<EntityType>, IntegrationObjectItemModel>
{
	private SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator;
	private DescriptorFactory descriptorFactory;

	@Override
	public Set<EntityType> generate(final IntegrationObjectItemModel itemModel)
	{
		final TypeDescriptor typeDescriptor = createItemTypeDescriptor(itemModel);

		final Set<String> simpleTypes = getTypesFromPrimitiveCollections(typeDescriptor);

		return simpleTypes.stream()
		                  .map(primitiveCollectionMemberEntityTypeGenerator::generate)
		                  .collect(Collectors.toSet());
	}

	protected TypeDescriptor createItemTypeDescriptor(final IntegrationObjectItemModel itemModel)
	{
		return getDescriptorFactory().createItemTypeDescriptor(itemModel);
	}

	private static Set<String> getTypesFromPrimitiveCollections(final TypeDescriptor itemTypeDescriptor)
	{
		return itemTypeDescriptor.getAttributes().stream()
		                         .filter(TypeAttributeDescriptor::isCollection)
		                         .map(TypeAttributeDescriptor::getAttributeType)
		                         .filter(TypeDescriptor::isPrimitive)
		                         .map(TypeDescriptor::getItemCode)
		                         .collect(Collectors.toSet());
	}

	@Required
	public void setPrimitiveCollectionMemberEntityTypeGenerator(final SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator)
	{
		this.primitiveCollectionMemberEntityTypeGenerator = primitiveCollectionMemberEntityTypeGenerator;
	}

	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}

	DescriptorFactory getDescriptorFactory()
	{
		if(descriptorFactory == null) {
			descriptorFactory = ApplicationBeans.getBean("integrationServicesDescriptorFactory", DescriptorFactory.class);
		}
		return descriptorFactory;
	}
}
