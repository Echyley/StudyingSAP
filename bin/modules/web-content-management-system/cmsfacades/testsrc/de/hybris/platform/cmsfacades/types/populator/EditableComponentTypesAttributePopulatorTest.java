/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class EditableComponentTypesAttributePopulatorTest
{
	private static final String EDITABLE_ATTRIBUTE_1 = "attribute1";
	private static final String EDITABLE_ATTRIBUTE_2 = "attribute2";
	private static final String EDITABLE_ATTRIBUTE_3 = "attribute3";
	private static final String NON_EDITABLE_ATTRIBUTE = "attribute4";

	@InjectMocks
	private EditableComponentTypesAttributePopulator populator;

	@Mock
	private AttributeDescriptorModel attribute;

	private final ComponentTypeAttributeData attributeDto = new ComponentTypeAttributeData();

	Set<String> editableAttributes = new HashSet<String>();

	@Before
	public void setUp()
	{

		editableAttributes.add(EDITABLE_ATTRIBUTE_1);
		editableAttributes.add(EDITABLE_ATTRIBUTE_2);
		editableAttributes.add(EDITABLE_ATTRIBUTE_3);

		attributeDto.setEditable(true);

	}

	@Test
	public void shouldSetEditableToTrueIfPartOfSet()
	{
		// GIVEN
		Mockito.when(attribute.getQualifier()).thenReturn(EDITABLE_ATTRIBUTE_1);
		populator.setEditableAttributes(editableAttributes);

		// WHEN
		populator.populate(attribute, attributeDto);

		// THEN
		Assert.assertTrue(attributeDto.isEditable());
	}

	@Test
	public void shouldKeepEditableToFalseIfNotPartOfSet()
	{
		// GIVEN
		Mockito.when(attribute.getQualifier()).thenReturn(NON_EDITABLE_ATTRIBUTE);
		populator.setEditableAttributes(editableAttributes);

		// WHEN
		populator.populate(attribute, attributeDto);

		// THEN
		Assert.assertFalse(attributeDto.isEditable());
	}

	@Test
	public void shouldSetEditableToFalseIfSetIsEmpty()
	{
		// GIVEN
		Mockito.when(attribute.getQualifier()).thenReturn(EDITABLE_ATTRIBUTE_1);
		populator.setEditableAttributes(new HashSet<String>());

		// WHEN
		populator.populate(attribute, attributeDto);

		// THEN
		Assert.assertFalse(attributeDto.isEditable());
	}

	@Test
	public void shouldSetEditableToFalseIfSetIsNull()
	{
		// GIVEN
		Mockito.when(attribute.getQualifier()).thenReturn(EDITABLE_ATTRIBUTE_1);

		// WHEN
		populator.populate(attribute, attributeDto);

		// THEN
		Assert.assertFalse(attributeDto.isEditable());
	}

}
