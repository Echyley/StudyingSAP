/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Generates a list of associations to be included in the EDMX.
 * @deprecated Use {@link AssociationListSchemaElementGenerator} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public class AssociationListGenerator implements SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>
{
	private AssociationGeneratorRegistry associationGeneratorRegistry;
	private DescriptorFactory descriptorFactory;

	@Override
	public List<Association> generate(final Collection<IntegrationObjectItemModel> allIntegrationObjectItemModelsForType)
	{
		Preconditions.checkArgument(allIntegrationObjectItemModelsForType != null,
				"An Association list cannot be generated from a null parameter");

		return allIntegrationObjectItemModelsForType.stream()
				.flatMap(type -> type.getAttributes().stream())
				.map(this::toAssociation)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private Association toAssociation(final IntegrationObjectItemAttributeModel attribute)
	{
		final TypeAttributeDescriptor attrDescriptor = toDescriptor(attribute);
		return associationGeneratorRegistry.getAssociationGenerator(attrDescriptor)
		                            .map(gen -> gen.generate(attrDescriptor))
		                            .orElse(null);
	}

	private TypeAttributeDescriptor toDescriptor(final IntegrationObjectItemAttributeModel attribute)
	{
		return descriptorFactory.createTypeAttributeDescriptor(attribute);
	}

	@Required
	public void setAssociationGeneratorRegistry(final AssociationGeneratorRegistry associationGeneratorRegistry)
	{
		this.associationGeneratorRegistry = associationGeneratorRegistry;
	}

	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}
}
