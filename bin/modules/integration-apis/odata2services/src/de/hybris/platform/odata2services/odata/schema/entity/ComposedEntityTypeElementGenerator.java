/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.KeyGenerator;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertyListGeneratorRegistry;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Property;

import com.google.common.base.Preconditions;

/**
 * Generates {@link EntityType}s based off the provided TypeDescriptor.
 */
public class ComposedEntityTypeElementGenerator extends SingleEntityTypeElementGenerator
{
	private final NavigationPropertyListGeneratorRegistry registry;

	/**
	 * Instantiates a new single entity type generator.
	 *
	 * @param keyGenerator a key generator
	 * @param propertiesGenerator a property generator
	 * @param registry navigation property list registry
	 */
	public ComposedEntityTypeElementGenerator(@NotNull final KeyGenerator keyGenerator,
	                                          @NotNull final SchemaElementGenerator<List<Property>, TypeDescriptor> propertiesGenerator,
	                                          @NotNull final NavigationPropertyListGeneratorRegistry registry)
	{
		super(keyGenerator, propertiesGenerator);
		Preconditions.checkArgument(registry != null, "registry must not be null");
		this.registry = registry;
	}

	@Override
	protected EntityType generateEntityType(final TypeDescriptor typeDescriptor)
	{
		return super.generateEntityType(typeDescriptor)
		            .setNavigationProperties(registry.generate(typeDescriptor));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param typeDescriptor an item type descriptor, based on which the decision has to be made.
	 * @return {@code true} for all descriptors
	 */
	@Override
	protected boolean isApplicable(final TypeDescriptor typeDescriptor)
	{
		return true;
	}

	@Override
	protected String generateEntityTypeName(final TypeDescriptor typeDescriptor)
	{
		return typeDescriptor.getItemCode();
	}
}
