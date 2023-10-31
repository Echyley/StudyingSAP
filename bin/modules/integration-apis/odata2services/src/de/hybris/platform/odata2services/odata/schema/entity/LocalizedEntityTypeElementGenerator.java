/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ENTITY_TYPE_PREFIX;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.KeyGenerator;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Property;

/**
 * Generates {@link EntityType}s for TypeDescriptors with localized attributes.
 */
public class LocalizedEntityTypeElementGenerator extends SingleEntityTypeElementGenerator
{
	/**
	 * Instantiates a new localized entity type generator.
	 *
	 * @param keyGenerator a localized key generator
	 * @param propertiesGenerator property list generator
	 */
	public LocalizedEntityTypeElementGenerator(@NotNull final KeyGenerator keyGenerator,
	                                           @NotNull final SchemaElementGenerator<List<Property>, TypeDescriptor> propertiesGenerator)
	{
		super(keyGenerator, propertiesGenerator);
	}

	@Override
	protected boolean isApplicable(final TypeDescriptor typeDescriptor)
	{
		return containsLocalizedAttributes(typeDescriptor);
	}

	private boolean containsLocalizedAttributes(final TypeDescriptor typeDescriptor)
	{
		return typeDescriptor.getAttributes().stream()
		                     .anyMatch(TypeAttributeDescriptor::isLocalized);
	}

	@Override
	protected String generateEntityTypeName(final TypeDescriptor typeDescriptor)
	{
		return LOCALIZED_ENTITY_TYPE_PREFIX + typeDescriptor.getItemCode();
	}
}
