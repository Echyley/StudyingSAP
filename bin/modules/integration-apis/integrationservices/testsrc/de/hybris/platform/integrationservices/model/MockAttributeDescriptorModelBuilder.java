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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

public class MockAttributeDescriptorModelBuilder extends BaseMockAttributeDescriptorModelBuilder<MockAttributeDescriptorModelBuilder, AttributeDescriptorModel>
{
	private TypeModel typeModel;

	MockAttributeDescriptorModelBuilder()
	{
		// Prevent instantiation from externally
	}

	public static MockAttributeDescriptorModelBuilder attributeDescriptorModelBuilder()
	{
		return new MockAttributeDescriptorModelBuilder();
	}
	
	public MockAttributeDescriptorModelBuilder withTypeCode(final String typeCode)
	{
		typeModel = typeModel(typeCode);
		return this;
	}

	public MockAttributeDescriptorModelBuilder withPrimitiveTypeCode(final String typeCode)
	{
		typeModel = primitiveTypeModel(typeCode);
		return this;
	}

	public <T extends TypeModel> MockAttributeDescriptorModelBuilder withType(final Class<T> type)
	{
		typeModel = mock(type);
		return this;
	}

	public AttributeDescriptorModel build()
	{
		final AttributeDescriptorModel model = createMock(AttributeDescriptorModel.class);
		lenient().when(model.getAttributeType()).thenReturn(typeModel);
		lenient().when(model.getQualifier()).thenReturn(qualifier);
		lenient().when(model.getUnique()).thenReturn(unique);
		lenient().when(model.getPartOf()).thenReturn(partOf);
		final ComposedTypeModel composedTypeModel = composedTypeModel(enclosingType);
		lenient().when(model.getEnclosingType()).thenReturn(composedTypeModel);
		lenient().when(model.getOptional()).thenReturn(optional);
		lenient().when(model.getDefaultValue()).thenReturn(defaultValue);
		lenient().when(model.getLocalized()).thenReturn(localized);
		lenient().when(model.getPrimitive()).thenReturn(primitive);
		return model;
	}

	@Override
	protected MockAttributeDescriptorModelBuilder myself()
	{
		return this;
	}
}

