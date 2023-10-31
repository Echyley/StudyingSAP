/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.MapDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Implementation of the {@code MapDescriptor} for {@link IntegrationObjectClassAttributeModel},
 * which provides functionality based on Class type
 */
public class ClassMapDescriptor implements MapDescriptor
{
	private final TypeDescriptor keyType;
	private final TypeDescriptor valueType;
	private final PojoIntrospector introspector;

	/**
	 * Instantiates this descriptor for {@link IntegrationObjectClassAttributeModel}.
	 *
	 * @param spector a {@link PojoIntrospector} used to retrieve method/attribute info
	 */
	public ClassMapDescriptor(@NotNull final PojoIntrospector spector)
	{
		Preconditions.checkArgument(spector != null, "PojoIntrospector cannot be null");
		Preconditions.checkArgument(spector.isMap(),
				"Only attributes of MapType can be used to instantiate this MapDescriptor");

		introspector = spector;
		keyType = createTypeDescriptor(introspector.getMapKeyType());
		valueType = createTypeDescriptor(introspector.getMapValueType());
	}

	@Override
	public TypeDescriptor getKeyType()
	{
		return keyType;
	}

	@Override
	public TypeDescriptor getValueType()
	{
		return valueType;
	}

	private TypeDescriptor createTypeDescriptor(final Class<?> typeClass)
	{
		return PrimitiveClassTypeDescriptor.create(introspector, typeClass);
	}
}
