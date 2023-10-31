/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Implementation of the {@code TypeDescriptor} for {@link IntegrationObjectClassModel},
 * which provides functionality based on Class type
 */
public class PrimitiveClassTypeDescriptor implements TypeDescriptor
{

	private final String integrationObjectCode;
	private final Class<?> typeClass;

	private PrimitiveClassTypeDescriptor(final PojoIntrospector pojoIntrospector, final Class<?> typeClass)
	{
		Preconditions.checkArgument(pojoIntrospector.getIntegrationObjectCode() != null,
				"PojoIntrospector should contain IntegrationObjectModel with non-null integration object code.");
		Preconditions.checkArgument(typeClass != null, "Non-null type Class is required");
		this.typeClass = typeClass;
		integrationObjectCode = pojoIntrospector.getIntegrationObjectCode();
	}

	/**
	 * Creates an instance of this type descriptor
	 *
	 * @param pojoIntrospector {@code PojoIntrospector} helper class.
	 * @param typeClass        class of descriptor
	 * @return a descriptor of a given typeClass.
	 */
	public static PrimitiveClassTypeDescriptor create(@NotNull final PojoIntrospector pojoIntrospector,
	                                                  @NotNull final Class<?> typeClass)
	{
		return new PrimitiveClassTypeDescriptor(pojoIntrospector, typeClass);
	}

	@Override
	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}

	@Override
	public String getItemCode()
	{
		return typeClass.getName();
	}

	@Override
	public String getTypeCode()
	{
		return "";
	}

	@Override
	public Optional<TypeAttributeDescriptor> getAttribute(final String attrName)
	{
		return Optional.empty();
	}

	/**
	 * {{@inheritDoc}}
	 * @return It will always return empty list.
	 */
	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public boolean isMap()
	{
		return false;
	}

	@Override
	public boolean isEnumeration()
	{
		return false;
	}

	@Override
	public boolean isAbstract()
	{
		return false;
	}

	@Override
	public boolean isInstance(final Object obj)
	{
		return typeClass.isInstance(obj);
	}

	@Override
	public boolean isRoot()
	{
		return false;
	}

	@Override
	public KeyDescriptor getKeyDescriptor()
	{
		return new NullKeyDescriptor();
	}

	/**
	 * {{@inheritDoc}}
	 * @return It will always return empty list.
	 */
	@Override
	public List<ReferencePath> getPathsToRoot()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean hasPathToRoot()
	{
		return false;
	}

	@Override
	public List<ReferencePath> pathFrom(final TypeDescriptor itemType)
	{
		return TypeDescriptor.super.pathFrom(itemType);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(integrationObjectCode, typeClass);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final PrimitiveClassTypeDescriptor that = (PrimitiveClassTypeDescriptor) o;
			return Objects.equals(typeClass, that.typeClass)
					&& Objects.equals(integrationObjectCode, that.integrationObjectCode);
		}
		return false;
	}

}
