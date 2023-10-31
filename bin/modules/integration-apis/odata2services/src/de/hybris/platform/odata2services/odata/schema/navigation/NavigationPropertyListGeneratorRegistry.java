/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.navigation;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.springframework.beans.factory.annotation.Required;

/**
 * This registry stores a collection of navigation property list generators and combine their results
 */
public class NavigationPropertyListGeneratorRegistry
{
	private Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> generators = new ArrayList<>();
	private Collection<SchemaElementGenerator<List<NavigationProperty>, TypeDescriptor>> schemaElementGenerators = new ArrayList<>();

	public List<NavigationProperty> generate(final TypeDescriptor itemDescriptor)
	{
		return getSchemaElementGenerators() != null ? generateInternal(itemDescriptor) : Collections.emptyList();
	}

	private List<NavigationProperty> generateInternal(final TypeDescriptor itemDescriptor)
	{
		return getSchemaElementGenerators().stream()
				.map(generator -> generator.generate(itemDescriptor))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * @deprecated not used anymore and will be removed. Use {@link #getSchemaElementGenerators()} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> getGenerators()
	{
		return new ArrayList<>(generators);
	}

	/**
	 * @deprecated not used anymore and will be removed. Use {@link #setSchemaElementGenerators(Collection)} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setGenerators(final Collection<SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>> generators)
	{
		this.generators = generators != null ?
				List.copyOf(generators) :
				Collections.emptyList();
	}

	protected Collection<SchemaElementGenerator<List<NavigationProperty>, TypeDescriptor>> getSchemaElementGenerators()
	{
		return new ArrayList<>(schemaElementGenerators);
	}

	@Required
	public void setSchemaElementGenerators(final Collection<SchemaElementGenerator<List<NavigationProperty>, TypeDescriptor>> schemaElementGenerators)
	{
		this.schemaElementGenerators = schemaElementGenerators != null ?
				List.copyOf(schemaElementGenerators) :
				Collections.emptyList();
	}
}
