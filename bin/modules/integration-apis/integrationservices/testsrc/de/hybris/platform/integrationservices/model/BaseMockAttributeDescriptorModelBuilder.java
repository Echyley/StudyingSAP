/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.model;

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Encapsulates builder behavior common for all kinds of {@code AttributeDescriptorModel} builders.
 */
public abstract class BaseMockAttributeDescriptorModelBuilder<B extends BaseMockAttributeDescriptorModelBuilder, M extends AttributeDescriptorModel>
{
	protected String enclosingType;
	protected String qualifier;
	protected Boolean unique;
	protected Boolean optional;
	protected Boolean partOf;
	protected Object defaultValue;
	protected Boolean primitive;
	protected Boolean localized;

	public static MockAttributeDescriptorModelBuilder attributeDescriptor()
	{
		return new MockAttributeDescriptorModelBuilder();
	}

	public static MockCollectionDescriptorModelBuilder collectionDescriptor()
	{
		return new MockCollectionDescriptorModelBuilder();
	}

	public B withEnclosingType(final String type)
	{
		enclosingType = type;
		return myself();
	}

	public B withQualifier(final String name)
	{
		qualifier = name;
		return myself();
	}

	public B unique()
	{
		return withUnique(true);
	}

	public B withUnique(final Boolean value)
	{
		unique = value;
		return myself();
	}

	public B optional()
	{
		return withOptional(true);
	}

	public B withOptional(final Boolean value)
	{
		optional = value;
		return myself();
	}

	public B withPartOf(final Boolean value)
	{
		partOf = value;
		return myself();
	}

	public B withDefaultValue(final Object value)
	{
		defaultValue = value;
		return myself();
	}

	public B withPrimitive(final Boolean b)
	{
		primitive = b;
		return myself();
	}

	public B withLocalized(final Boolean b)
	{
		localized = b;
		return myself();
	}

	protected final M createMock(final Class<M> mockClass)
	{
		final M model = mock(mockClass);
		return model;
	}

	public abstract M build();

	protected static ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		lenient().when(composedTypeModel.getCode()).thenReturn(code);
		return composedTypeModel;
	}

	protected static TypeModel typeModel(final String typecode)
	{
		final TypeModel typeModel = mock(TypeModel.class);
		lenient().when(typeModel.getCode()).thenReturn(typecode);
		return typeModel;
	}

	protected static AtomicTypeModel primitiveTypeModel(final String typecode)
	{
		final AtomicTypeModel typeModel = mock(AtomicTypeModel.class);
		lenient().when(typeModel.getCode()).thenReturn(typecode);
		return typeModel;
	}

	protected abstract B myself();
}
