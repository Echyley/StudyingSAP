/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

public class AssociationGeneratorRegistry
{
	private List<AssociationGenerator> associationGenerators = new ArrayList<>();

	/**
	 * Searches for an association generator that may be applicable to this attribute. An attribute representing
	 * a non-primitive type should not return Optional.empty.
	 *
	 * @param attribute {@link TypeAttributeDescriptor} holding data about an integration object item's attribute
	 * @return an optional association generator
	 */
	public Optional<AssociationGenerator> getAssociationGenerator(final TypeAttributeDescriptor attribute)
	{
		return attribute != null ? getAssociationGeneratorInternal(attribute) : Optional.empty();
	}

	private Optional<AssociationGenerator> getAssociationGeneratorInternal(final TypeAttributeDescriptor attribute)
	{
		return Optional.of(associationGenerators.stream()
				.filter(generator -> generator.isApplicable(attribute))
				.findFirst())
				.orElse(Optional.empty());
	}

	@Required
	public void setAssociationGenerators(final List<AssociationGenerator> associationGenerators)
	{
		this.associationGenerators = associationGenerators != null ?
				List.copyOf(associationGenerators) :
				Collections.emptyList();
	}
}
