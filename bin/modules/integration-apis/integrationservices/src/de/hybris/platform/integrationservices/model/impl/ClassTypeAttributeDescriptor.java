/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.AttributeValueAccessor;
import de.hybris.platform.integrationservices.model.CollectionDescriptor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.MapDescriptor;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * {@inheritDoc}
 * {@link TypeAttributeDescriptor} implementation for a {@link IntegrationObjectClassAttributeModel}.
 */
public class ClassTypeAttributeDescriptor extends AbstractDescriptor implements TypeAttributeDescriptor
{
	private final TypeDescriptor containerClassDescriptor;
	private final IntegrationObjectClassAttributeModel attributeModel;
	private PojoIntrospector introspector;

	private TypeDescriptor attributeType;


	ClassTypeAttributeDescriptor(@NotNull final TypeDescriptor classDescriptor,
	                             @NotNull final IntegrationObjectClassAttributeModel model,
	                             final DescriptorFactory factory)
	{
		super(factory);
		containerClassDescriptor = classDescriptor;
		attributeModel = model;
	}


	@Override
	public String getAttributeName()
	{
		return attributeModel.getAttributeName();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always returns an empty string as class types are not persisted in platform.
	 */
	@Override
	public String getQualifier()
	{
		return StringUtils.EMPTY;
	}

	@Override
	public boolean isCollection()
	{
		return getIntrospector().isCollection();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return type of this attribute value
	 */
	@Override
	public TypeDescriptor getAttributeType()
	{
		if (attributeType == null)
		{
			attributeType = deriveAttributeType();
		}
		return attributeType;
	}

	@Override
	public TypeDescriptor getTypeDescriptor()
	{
		return containerClassDescriptor;
	}

	@Override
	public Optional<TypeAttributeDescriptor> reverse()
	{
		return getAttributeType().getAttributes()
		                         .stream()
		                         .filter(attr -> attr.getAttributeType().equals(getTypeDescriptor()))
		                         .findFirst();
	}

	@Override
	public boolean isNullable()
	{
		return getReadMethod()
				.map(Method::getAnnotations)
				.map(this::doesNotHaveNotNullAnnotations)
				.orElse(false);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code false}, as classes can exist independently.
	 */
	@Override
	public boolean isPartOf()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code false}, as classes can exist independently.
	 */
	@Override
	public boolean isAutoCreate()
	{
		return false;
	}

	@Override
	public boolean isLocalized()
	{
		return false;
	}

	@Override
	public boolean isPrimitive()
	{
		return getAttributeType().isPrimitive();
	}

	@Override
	public boolean isMap()
	{
		return getIntrospector().isMap();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always returns {@code false}.
	 */
	@Override
	public boolean isSettable(final Object item)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code false}, as class type cannot have a key.
	 */
	@Override
	public boolean isKeyAttribute()
	{
		return BooleanUtils.toBoolean(attributeModel.getUnique());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code true}.
	 */
	@Override
	public boolean isReadable()
	{
		return true;
	}

	/**
	 * Gets the {@link CollectionDescriptor}
	 *
	 * @return A collection descriptor
	 */
	@Override
	public CollectionDescriptor getCollectionDescriptor()
	{
		return ClassCollectionDescriptor.create(getIntrospector());
	}

	/**
	 * Gets a map descriptor for this attribute.
	 *
	 * @return {@code Optional} containing a {@code MapDescriptor}, if this attribute is of Map type; otherwise returns {@code Optional.empty()}
	 * @see #isMap()
	 */
	@Override
	public Optional<MapDescriptor> getMapDescriptor()
	{
		return Optional.ofNullable(createClassMapDescriptor());
	}

	@Override
	public AttributeValueAccessor accessor()
	{
		final var getter = new ClassAttributeValueGetter(attributeModel);
		final var setter = new NullAttributeValueSetter();
		return new DelegatingAttributeValueAccessor(getter, setter);
	}

	private ClassMapDescriptor createClassMapDescriptor()
	{
		if (isMap())
		{
			return new ClassMapDescriptor(getIntrospector());
		}
		return null;
	}

	private Optional<Method> getReadMethod()
	{
		return getIntrospector().getReadMethod();
	}

	private boolean doesNotHaveNotNullAnnotations(final Annotation[] annotations)
	{
		return Arrays.stream(annotations)
		             .noneMatch(a -> a instanceof NotNull || a instanceof Nonnull);
	}

	private TypeDescriptor deriveAttributeType()
	{
		final IntegrationObjectClassModel referencedClassModel = attributeModel.getReturnIntegrationObjectClass();
		return referencedClassModel != null
				? getFactory().createClassTypeDescriptor(getIODescriptor(), referencedClassModel)
				: createNonReferencedDescriptor();
	}

	private IntegrationObjectDescriptor getIODescriptor()
	{
		return getFactory().createIntegrationObjectDescriptor(
				attributeModel.getIntegrationObjectClass().getIntegrationObject());
	}

	private Class<?> derivePrimitiveClassType()
	{
		return getIntrospector().getActualMethodReturnType(getReadMethod().orElse(null));
	}

	private TypeDescriptor createNonReferencedDescriptor()
	{
		return isMap()
				? MapClassTypeDescriptor.create(getIntrospector())
				: PrimitiveClassTypeDescriptor.create(getIntrospector(), derivePrimitiveClassType());
	}

	private PojoIntrospector getIntrospector()
	{
		if (introspector == null)
		{
			introspector = new PojoIntrospector(attributeModel);
		}
		return introspector;
	}
}
