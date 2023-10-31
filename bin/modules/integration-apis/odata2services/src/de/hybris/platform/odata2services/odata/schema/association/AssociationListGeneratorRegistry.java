/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.olingo.odata2.api.edm.provider.Association;

/**
 * <p>Aggregates associations generated by registered associations generators.</p>
 * <p>Do not extend this class, if needs to be customized. Customization can be done easily by plugging/unplugging registered
 * associations generators in the Spring configuration. And even if it's absolutely necessary to override this class's methods,
 * write a custom registry that delegates to this registry and does custom things around the delegate call (use composition over
 * inheritance)</p>
 */
public class AssociationListGeneratorRegistry
{
	private List<SchemaElementGenerator<List<Association>, Collection<TypeDescriptor>>> associationGenerators = Collections.emptyList();

	/**
	 * Generates associations that should be present in the schema by delegating to the registered generators and then combining
	 * their results.
	 * @param descriptors integration item descriptors, for which EDMX associations should be generated
	 * @return combined list of all associations generated by the nested generators.
	 */
	public List<Association> generateFor(final Collection<TypeDescriptor> descriptors)
	{
		return CollectionUtils.isNotEmpty(descriptors)
				? combineAssociationsProducedByNestedGenerators(descriptors)
				: Collections.emptyList();
	}

	private List<Association> combineAssociationsProducedByNestedGenerators(final Collection<TypeDescriptor> descriptors)
	{
		return associationGenerators.stream()
				.map(g -> g.generate(descriptors))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public void setAssociationGenerators(final List<SchemaElementGenerator<List<Association>, Collection<TypeDescriptor>>> generators)
	{
		associationGenerators = generators != null
				? generators
				: Collections.emptyList();
	}
}
