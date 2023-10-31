/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ListItemClassificationAttributeDTOBuilderUnitTest
{
	private ListItemClassificationAttributeDTOBuilder builder;
	private final ClassAttributeAssignmentModel classAttributeAssignment = new ClassAttributeAssignmentModel();
	private final ClassificationAttributeModel classificationAttribute = new ClassificationAttributeModel();
	private final ClassificationClassModel classificationClass = new ClassificationClassModel();
	private static final String CLASSIFICATION_ATTR_CODE = "code";

	@Before
	public void init()
	{
		classificationAttribute.setCode(CLASSIFICATION_ATTR_CODE);
		classAttributeAssignment.setClassificationAttribute(classificationAttribute);
		classAttributeAssignment.setClassificationClass(classificationClass);
		classAttributeAssignment.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		builder = new ListItemClassificationAttributeDTOBuilder(classAttributeAssignment);
	}

	@Test
	public void instantiatesListItemWithDefaultValuesWhenNoArgumentsProvided()
	{
		final ListItemClassificationAttributeDTO actual = builder.build();
		assertFalse("Selected field is false.", actual.isSelected());
		assertFalse("Custom unique field is false.", actual.isCustomUnique());
		assertFalse("Autocreate field is false.", actual.isAutocreate());
		assertEquals("Attribute name field is classification class code.", CLASSIFICATION_ATTR_CODE, actual.getAlias());
		assertEquals("Mandatory class attribute assignment field is set.", classAttributeAssignment,
				actual.getClassAttributeAssignmentModel());
	}

	@Test
	public void instantiatesWithValuesProvidedWhenArgumentsProvided()
	{
		final String attributeName = "media";
		final ListItemClassificationAttributeDTO actual = builder.withSelected(true).withCustomUnique(true).withAutocreate(true)
				.withAttributeName(attributeName).build();
		assertTrue("Selected field is set.", actual.isSelected());
		assertTrue("Custom unique field is set.", actual.isCustomUnique());
		assertTrue("Autocreate field is set.", actual.isAutocreate());
		assertEquals("Attribute name field is set.", attributeName, actual.getAlias());
		assertEquals("Class attribute assignment field is set.", classAttributeAssignment,
				actual.getClassAttributeAssignmentModel());
	}
}
