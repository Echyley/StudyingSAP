/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class AttributeTypeDTOBuilderUnitTest
{
	private AttributeTypeDTOBuilder builder;
	private final AttributeDescriptorModel descriptor = new AttributeDescriptorModel();

	@Before
	public void init()
	{
		builder = new AttributeTypeDTOBuilder(descriptor);
	}

	@Test
	public void instantiatesListItemWithDefaultValuesWhenNoArgumentsProvided()
	{
		final AttributeTypeDTO actual = builder.build();
		assertEquals("Mandatory attribute descriptor is set", descriptor, actual.getAttributeDescriptor());
		assertEquals("Structure type is none.", ListItemStructureType.NONE, actual.getStructureType());
		assertNull("Type is null", actual.getType());
	}

	@Test
	public void instantiatesWithValuesProvidedWhenArgumentsProvided()
	{
		final TypeModel type = new TypeModel();
		final AttributeTypeDTO actual = builder.withStructureType(ListItemStructureType.COLLECTION)
		                                       .withType(type)
		                                       .build();
		assertEquals("Mandatory attribute descriptor is set.", descriptor, actual.getAttributeDescriptor());
		assertEquals("Structure type is set.", ListItemStructureType.COLLECTION, actual.getStructureType());
		assertEquals("Type is set", type, actual.getType());
	}
}
