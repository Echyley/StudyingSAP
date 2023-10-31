/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

/**
 * Schema element generator for {@link Set<EntityType>} for given {@link TypeDescriptor}.
 */
public class PrimitiveEntityTypeElementGenerator implements SchemaElementGenerator<Set<EntityType>, TypeDescriptor>
{
	private SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator;

	/**
	 * Generates the schema element for given {@link TypeDescriptor} using provided generator.
	 *
	 * @param descriptor the type descriptor for which schema element needs to be generated.
	 * @return a set of {@link EntityType} generated from the given descriptor. Returns empty set if descriptor is {@code null}
	 * or has no primitive collection attribute.
	 */
	@Override
	public Set<EntityType> generate(final TypeDescriptor descriptor)
	{
		if (descriptor != null)
		{
			final Set<String> simpleTypes = getTypesFromPrimitiveCollections(descriptor);

			return simpleTypes.stream()
			                  .map(primitiveCollectionMemberEntityTypeGenerator::generate)
			                  .collect(Collectors.toSet());
		}
		return Collections.emptySet();
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
	public void setPrimitiveCollectionMemberEntityTypeGenerator(final PrimitiveCollectionMemberEntityTypeGenerator generator)
	{
		primitiveCollectionMemberEntityTypeGenerator = generator;
	}
}
