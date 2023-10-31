/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectGraphOperations;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public final class IntegrationObjectGraphSearch implements IntegrationObjectGraphOperations
{
	private final IntegrationObjectDescriptor integrationObject;

	public IntegrationObjectGraphSearch(final IntegrationObjectDescriptor io)
	{
		integrationObject = io;
	}

	@Override
	public Set<TypeDescriptor> findTypesRelatedTo(final TypeDescriptor descriptor)
	{
		return findTypesRelatedTo(descriptor, new HashSet<>());
	}

	private Set<TypeDescriptor> findTypesRelatedTo(final TypeDescriptor descriptor, final Set<TypeDescriptor> subTree)
	{
		subTree.add(descriptor);
		descriptor.getAttributes()
		          .stream()
		          .map(TypeAttributeDescriptor::getAttributeType)
		          .filter(integrationObject::contains)
		          .filter(Predicate.not(subTree::contains))
		          .forEach(found -> findTypesRelatedTo(found, subTree));
		return subTree;
	}

	@Override
	public Set<TypeDescriptor> findTypesRelatedTo(final String itemCode)
	{
		return integrationObject.getItemTypeDescriptors()
		                        .stream()
		                        .filter(d -> d.getItemCode().equals(itemCode))
		                        .findAny()
		                        .map(this::findTypesRelatedTo)
		                        .orElse(Collections.emptySet());
	}
}
