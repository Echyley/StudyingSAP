/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.MoreObjects;

/**
 * {@inheritDoc}
 * {@link TypeDescriptor} for {@link IntegrationObjectClassModel}.
 */
public class ClassTypeDescriptor extends AbstractDescriptor implements TypeDescriptor
{
	private static final ReferencePathFinder DEFAULT_PATH_FINDER = new AttributePathFinder();

	private final IntegrationObjectClassModel classTypeModel;
	private Map<String, TypeAttributeDescriptor> attributeDescriptors;
	private final IntegrationObjectDescriptor ioDescriptor;
	private ReferencePathFinder referencePathFinder;

	/**
	 * Instantiates this descriptor for an item in an integration object.
	 *
	 * @param descriptor descriptor for the integration object containing this class descriptor.
	 * @param model      a class item model to create a descriptor for.
	 * @param factory    a descriptor factory implementation to be used by this descriptor for creating nested infrastructure, e.g.
	 *                   attribute descriptors for attributes in the integration object item, value accessors, etc.
	 */
	ClassTypeDescriptor(@NotNull final IntegrationObjectDescriptor descriptor,
	                    @NotNull final IntegrationObjectClassModel model,
	                    final DescriptorFactory factory)
	{
		super(factory);
		classTypeModel = model;
		ioDescriptor = descriptor;
		referencePathFinder = DEFAULT_PATH_FINDER;
	}

	@Override
	public String getIntegrationObjectCode()
	{
		return ioDescriptor.getCode();
	}

	@Override
	public String getItemCode()
	{
		return classTypeModel.getCode();
	}

	@Override
	public String getTypeCode()
	{
		return classTypeModel.getType().getName();
	}

	@Override
	public Optional<TypeAttributeDescriptor> getAttribute(final String attrName)
	{
		return Optional.ofNullable(attributeDescriptors().get(attrName));
	}

	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return new HashSet<>(attributeDescriptors().values());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always returns {@code false}, because it is not a primitive type.
	 */
	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always returns {@code false}, because it is not a map.
	 */
	@Override
	public boolean isMap()
	{
		return false;
	}

	@Override
	public boolean isEnumeration()
	{
		return classTypeModel.getType().isEnum();
	}

	@Override
	public boolean isAbstract()
	{
		return Modifier.isAbstract(classTypeModel.getType().getModifiers());
	}

	@Override
	public boolean isInstance(final Object obj)
	{
		return classTypeModel.getType().isInstance(obj);
	}

	@Override
	public boolean isRoot()
	{
		return Boolean.TRUE.equals(classTypeModel.getRoot());
	}

	@Override
	public KeyDescriptor getKeyDescriptor()
	{
		return ItemKeyDescriptor.create(this);
	}

	@Override
	public List<ReferencePath> getPathsToRoot()
	{
		return ioDescriptor.getRootType()
		                   .map(rootType -> referencePathFinder.findAllPaths(this, rootType))
		                   .orElse(Collections.emptyList());
	}

	@Override
	public boolean hasPathToRoot()
	{
		return !getPathsToRoot().isEmpty();
	}

	@Override
	public List<ReferencePath> pathFrom(final TypeDescriptor classType)
	{
		return referencePathFinder.findAllPaths(classType, this);
	}

	/**
	 * Injects {@code ReferencePathFinder} implementation to use.
	 *
	 * @param finder a finder implementation to use for methods returning {@link ReferencePath}s. {@code null} value resets the
	 *               finder implementation to default, which is {@link AttributePathFinder}
	 */
	public void setReferencePathFinder(final ReferencePathFinder finder)
	{
		referencePathFinder = finder != null ? finder : DEFAULT_PATH_FINDER;
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
		final var that = (ClassTypeDescriptor) o;
		return new EqualsBuilder()
				.append(this.getIntegrationObjectCode(), that.getIntegrationObjectCode())
				.append(this.getItemCode(), that.getItemCode())
				.build();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder()
				.append(getIntegrationObjectCode())
				.append(getItemCode())
				.build();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
		                  .add("integrationObjectCode", getIntegrationObjectCode())
		                  .add("itemCode", getItemCode())
		                  .toString();
	}

	private Map<String, TypeAttributeDescriptor> attributeDescriptors()
	{
		if (attributeDescriptors == null)
		{
			attributeDescriptors = createAttributeDescriptors();
		}
		return attributeDescriptors;
	}

	private Map<String, TypeAttributeDescriptor> createAttributeDescriptors()
	{
		return classTypeModel.getAttributes().stream()
		                     .map(this::createClassAttributeDescriptor)
		                     .collect(Collectors.toMap(TypeAttributeDescriptor::getAttributeName, Function.identity()));
	}

	private TypeAttributeDescriptor createClassAttributeDescriptor(final IntegrationObjectClassAttributeModel model)
	{
		return getFactory().createTypeAttributeDescriptor(this, model);
	}
}
