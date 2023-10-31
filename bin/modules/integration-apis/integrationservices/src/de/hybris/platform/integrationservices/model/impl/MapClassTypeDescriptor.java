/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * {@inheritDoc}
 * {@link TypeDescriptor} for {@link de.hybris.platform.integrationservices.model.IntegrationObjectClassModel}.
 * <p>This implementation is effectively immutable and therefore is thread safe</p>
 * <p>Reuse this implementation through composition not inheritance</p>
 */
public final class MapClassTypeDescriptor implements TypeDescriptor
{
	private final PojoIntrospector pojoIntrospector;

	private MapClassTypeDescriptor(final PojoIntrospector introspector)
	{
		Preconditions.checkArgument(introspector != null, "Non-null PojoIntrospector is required");
		Preconditions.checkArgument(introspector.isMap(), "PojoIntrospector must be for a Map type attribute");
		pojoIntrospector = introspector;
	}

	/**
	 * Creates an instance of this type descriptor
	 *
	 * @param introspector an introspector which helps perform java reflections on POJOs
	 * @return a type descriptor
	 */
	public static TypeDescriptor create(final PojoIntrospector introspector)
	{
		return new MapClassTypeDescriptor(introspector);
	}

	@Override
	public String getIntegrationObjectCode()
	{
		return pojoIntrospector.getIntegrationObjectCode();
	}

	@Override
	public String getItemCode()
	{
		final String mapType = pojoIntrospector.getReturnType().map(Class::getSimpleName).orElse("");

		return pojoIntrospector.getMapKeyType().getSimpleName()
				+ "2"
				+ pojoIntrospector.getMapValueType().getSimpleName()
				+ mapType;
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

	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return Collections.emptySet();
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public boolean isMap()
	{
		return true;
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
		return obj instanceof Map;
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
		return Collections.emptyList();
	}
}
