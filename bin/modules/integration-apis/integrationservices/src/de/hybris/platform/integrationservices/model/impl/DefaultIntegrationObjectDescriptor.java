/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link IntegrationObjectDescriptor} based on {@link IntegrationObjectModel} data structure.
 */
public class DefaultIntegrationObjectDescriptor extends AbstractDescriptor implements IntegrationObjectDescriptor
{
	private final IntegrationObjectModel integrationObjectModel;
	private Set<TypeDescriptor> integrationObjectItems;

	DefaultIntegrationObjectDescriptor(final IntegrationObjectModel model, final DescriptorFactory factory)
	{
		super(factory);
		Preconditions.checkArgument(model != null, "IntegrationObjectModel is required");
		validateNonConflictingModel(model);
		integrationObjectModel = model;
	}

	@Override
	public String getCode()
	{
		return integrationObjectModel.getCode();
	}

	@Override
	public Set<TypeDescriptor> getItemTypeDescriptors()
	{
		if (integrationObjectItems == null)
		{
			final Set<TypeDescriptor> items = integrationObjectModel.getItems().stream()
			                                                        .map(item -> getFactory().createItemTypeDescriptor(item))
			                                                        .collect(Collectors.toSet());

			final Set<TypeDescriptor> classes = integrationObjectModel.getClasses().stream()
			                                                          .map(c -> getFactory().createClassTypeDescriptor(this, c))
			                                                          .collect(Collectors.toSet());

			final var descriptors = new HashSet<>(items);
			descriptors.addAll(classes);
			integrationObjectItems = descriptors;
		}
		return new HashSet<>(integrationObjectItems);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #getTypeDescriptor(Object)}
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Optional<TypeDescriptor> getItemTypeDescriptor(final ItemModel item)
	{
		return getTypeDescriptor(item);
	}

	@Override
	public Optional<TypeDescriptor> getTypeDescriptor(final Object payloadObject)
	{
		if (payloadObject == null)
		{
			return Optional.empty();
		}
		final Optional<TypeDescriptor> exactMatch = findMatchingTypeDescriptor(
				d -> matchesPayloadObject(payloadObject, d));
		return exactMatch.isPresent()
				? exactMatch
				: findMatchingTypeDescriptor(type -> type.isInstance(payloadObject));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #getRootType()} instead.
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Optional<TypeDescriptor> getRootItemType()
	{
		return getRootType();
	}

	private void validateNonConflictingModel(final IntegrationObjectModel model)
	{
		if (CollectionUtils.isNotEmpty(model.getItems()) && CollectionUtils.isNotEmpty(model.getClasses()))
		{
			throw new IntegrationObjectItemAndClassConflictException(model.getCode());
		}
	}

	private boolean matchesPayloadObject(final Object payloadObject, final TypeDescriptor d)
	{
		return ((payloadObject instanceof ItemModel item) && matchesItemModel(item, d))
				|| matchesPOJO(payloadObject, d);
	}

	private boolean matchesPOJO(final Object payloadObject, final TypeDescriptor d)
	{
		return d.getTypeCode().equals(payloadObject.getClass().getName());
	}

	private boolean matchesItemModel(final ItemModel item, final TypeDescriptor d)
	{
		return d.getTypeCode().equals(deriveItemTypeCode(item));
	}

	private String deriveItemTypeCode(final ItemModel item)
	{
		return item instanceof ComposedTypeModel model
				? model.getCode()
				: item.getItemtype();
	}

	private Optional<TypeDescriptor> findMatchingTypeDescriptor(final Predicate<TypeDescriptor> predicate)
	{
		return getItemTypeDescriptors().stream()
		                               .filter(predicate)
		                               .findAny();
	}

	@Override
	public Optional<TypeDescriptor> getRootType()
	{
		return getItemTypeDescriptors().stream()
		                               .filter(TypeDescriptor::isRoot)
		                               .findFirst();
	}

	@Override
	public boolean contains(final TypeDescriptor descriptor)
	{
		return getItemTypeDescriptors().contains(descriptor);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final DefaultIntegrationObjectDescriptor that = (DefaultIntegrationObjectDescriptor) o;

		return integrationObjectModel.getCode().equals(that.integrationObjectModel.getCode());
	}

	@Override
	public int hashCode()
	{
		return integrationObjectModel.getCode().hashCode();
	}

	@Override
	public String toString()
	{
		return "IntegrationObject{" +
				"code=" + integrationObjectModel.getCode() +
				'}';
	}
}
