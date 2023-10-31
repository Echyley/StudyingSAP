/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class ListItemAttributeDTOBuilderUnitTest
{
	private ListItemAttributeDTOBuilder builder;
	private final TypeModel typeModel = TypeCreatorTestUtils.typeModel("notMatters");
	private final AttributeDescriptorModel descriptorModel = TypeCreatorTestUtils.attributeDescriptorModel(typeModel);
	private final AttributeTypeDTO attributeTypeDTO = AttributeTypeDTO.builder(descriptorModel).build();

	@Before
	public void init()
	{
		builder = new ListItemAttributeDTOBuilder(attributeTypeDTO);
	}

	@Test
	public void instantiatesListItemWithDefaultValuesWhenNoArgumentsProvided()
	{
		final ListItemAttributeDTO actual = builder.build();
		assertFalse("Selected field is false.", actual.isSelected());
		assertFalse("Custom unique field is false.", actual.isCustomUnique());
		assertFalse("Autocreate field is false.", actual.isAutocreate());
		assertEquals("Attribute name field is empty string.", StringUtils.EMPTY, actual.getAlias());
		assertEquals("Item code field is empty string.", StringUtils.EMPTY, actual.getTypeAlias());
		assertEquals("Mandatory AttributeTypeDTO field is set.", attributeTypeDTO, actual.getAttributeTypeDTO());
	}

	@Test
	public void instantiatesWithValuesProvidedWhenArgumentsProvided()
	{
		final String attributeName = "localPrice";
		final String itemCode = "Price";
		final ListItemAttributeDTO actual = builder.withSelected(true)
		                                           .withCustomUnique(true)
		                                           .withAutocreate(true)
		                                           .withAttributeName(attributeName)
		                                           .withTypeAlias(itemCode)
		                                           .build();
		assertTrue("Selected field is set.", actual.isSelected());
		assertTrue("Custom unique field is set.", actual.isCustomUnique());
		assertTrue("Autocreate field is set.", actual.isAutocreate());
		assertEquals("Attribute name field is set.", attributeName, actual.getAlias());
		assertEquals("Item code field is set.", itemCode, actual.getTypeAlias());
		assertEquals("Mandatory AttributeTypeDTO field is set.", attributeTypeDTO, actual.getAttributeTypeDTO());
	}
}
