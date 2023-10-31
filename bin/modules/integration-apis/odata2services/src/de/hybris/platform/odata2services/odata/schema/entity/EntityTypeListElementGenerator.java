/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.EntityType;

/**
 * A generator of {@link List<EntityType>} schema elements from a {@link Collection<TypeDescriptor>}.
 */
public class EntityTypeListElementGenerator implements SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>>
{
	private List<SchemaElementGenerator<Collection<EntityType>, TypeDescriptor>> entityTypeGenerators = Collections.emptyList();

	/**
	 * Generates a list of {@link EntityType} schema elements from given collection of {@link TypeDescriptor} using assigned
	 * list of entity type generators.
	 *
	 * @param descriptors a collection of type descriptors for which schema elements are generated.
	 * @return a {@link List<EntityType>} having distinct {@link EntityType#getName()} generated for descriptors using
	 * entity type generators. An empty list will be returned if descriptors is empty, or entity type generators is not injected
	 * or is {@code null}.
	 */
	@Override
	@NotNull
	public List<EntityType> generate(@Nullable final Collection<TypeDescriptor> descriptors)
	{
		if (descriptors != null)
		{
			final List<EntityType> generatedTypes = new ArrayList<>();
			descriptors.forEach(descriptor ->
					entityTypeGenerators.forEach(generator ->
							generatedTypes.addAll(generator.generate(descriptor))));
			return SchemaUtils.removeDuplicates(generatedTypes, EntityType::getName);
		}
		return Collections.emptyList();
	}

	/**
	 * Sets the entity type generators. If passed list is {@code null}, an empty list will be used as default.
	 *
	 * @param generators list of entity type generators from which entity type list will be generated.
	 */
	public void setEntityTypeGenerators(
			@Nullable final List<SchemaElementGenerator<Collection<EntityType>, TypeDescriptor>> generators)
	{
		entityTypeGenerators = generators != null ? List.copyOf(generators) : Collections.emptyList();
	}
}
