/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.utility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ModalUtilsTest
{
	@Mock(lenient = true)
	private ReadService readService;

	@Test
	public void testServiceNameValidCorrect()
	{
		assertTrue(ModalUtils.isAlphaNumericName("InboundProduct42"));
	}

	@Test
	public void testServiceNameValidIncorrect()
	{
		assertFalse(ModalUtils.isAlphaNumericName(""));
		assertFalse(ModalUtils.isAlphaNumericName("$"));
		assertFalse(ModalUtils.isAlphaNumericName("/"));
		assertFalse(ModalUtils.isAlphaNumericName(" "));
	}

	@Test
	public void testServiceNameUniqueValid()
	{
		setupIOM();

		assertTrue(ModalUtils.isServiceNameUnique("OutboundProduct", readService));
	}

	@Test
	public void testServiceNameUniqueInvalid()
	{
		setupIOM();

		assertFalse(ModalUtils.isServiceNameUnique("InboundProduct42", readService));
	}

	@Test
	public void testIsAliasUniqueNoMatch()
	{
		final String alias = "testAlias";
		final String qualifierOfDTOToBeRename = "qualifier3";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertTrue(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueAliasMatch()
	{
		final String alias = "alias1";
		final String qualifierOfDTOToBeRename = "qualifier3";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertFalse(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueQualifierMatch()
	{
		final String alias = "qualifier2";
		final String qualifierOfDTOToBeRename = "qualifier3";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertFalse(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueQualifierMatch2()
	{
		final String alias = "qualifier2";
		final String qualifierOfDTOToBeRename = "qualifier2";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertTrue(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueQualifierMatch3()
	{
		final String alias = "qualifier1";
		final String qualifierOfDTOToBeRename = "qualifier1";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertTrue(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueMatchClassAttrCode()
	{
		final String alias = "attributeCode";
		final String qualifierOfDTOToBeRename = "qualifier3";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertFalse(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsAliasUniqueMatchClassAttrCode2()  // add to list as long as alias equals to qualifier
	{
		final String alias = "classCode";
		final String qualifierOfDTOToBeRename = "classCode";
		final List<AbstractListItemDTO> listItemDTOS = setupList();

		assertTrue(ModalUtils.isAliasUnique(alias, qualifierOfDTOToBeRename, listItemDTOS));
	}

	@Test
	public void testIsClassificationAttributeCodeAlphaNumericName()
	{
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel attribute = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		given(assignment.getClassificationAttribute()).willReturn(attribute);
		given(assignment.getClassificationClass()).willReturn(classificationClass);
		given(assignment.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.STRING);
		given(classificationClass.getCode()).willReturn("code");
		given(attribute.getCode()).willReturn("Attribute %Code");

		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(assignment)
		                                                                                 .withSelected(true)
		                                                                                 .build();

		assertTrue(ModalUtils.isAlphaNumericName(dto.getClassificationAttributeCode()));
	}

	private void setupIOM()
	{
		final List<IntegrationObjectModel> integrationObjectModels = new ArrayList<>();
		final IntegrationObjectModel inboundProduct42 = new IntegrationObjectModel();
		inboundProduct42.setCode("InboundProduct42");
		integrationObjectModels.add(inboundProduct42);
		lenient().when(readService.getIntgrationObjectModelByCode("InboundProduct42")).thenReturn(integrationObjectModels);
	}

	private List<AbstractListItemDTO> setupList()
	{
		final AttributeDescriptorModel attrDesMod1 = new AttributeDescriptorModel();
		final TypeModel tm = new TypeModel();
		tm.setCode("Type1");
		attrDesMod1.setQualifier("qualifier1");
		attrDesMod1.setAttributeType(tm);
		final AttributeDescriptorModel attrDesMod2 = new AttributeDescriptorModel();
		attrDesMod2.setQualifier("qualifier2");
		attrDesMod2.setAttributeType(tm);

		final ClassAttributeAssignmentModel attributeAssignmentModel = new ClassAttributeAssignmentModel();
		final ClassificationAttributeModel attributeModel = new ClassificationAttributeModel();
		final ClassificationClassModel classificationClassModel = new ClassificationClassModel();
		attributeModel.setCode("attributeCode");
		attributeAssignmentModel.setClassificationAttribute(attributeModel);
		classificationClassModel.setCode("classCode");
		attributeAssignmentModel.setClassificationClass(classificationClassModel);
		attributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		attributeAssignmentModel.setMultiValued(false);

		final String alias = "classificationAttribute";

		final AttributeTypeDTO typeDTO1 = AttributeTypeDTO.builder(attrDesMod1).build();
		final AttributeTypeDTO typeDTO2 = AttributeTypeDTO.builder(attrDesMod2).build();

		final ListItemAttributeDTO attr1 = ListItemAttributeDTO.builder(typeDTO1)
		                                                       .withSelected(true)
		                                                       .withAttributeName("alias1")
		                                                       .build();
		final ListItemAttributeDTO attr2 = ListItemAttributeDTO.builder(typeDTO2)
		                                                       .withSelected(true)
		                                                       .withAttributeName("alias2")
		                                                       .build();
		final ListItemAttributeDTO attr3 = ListItemAttributeDTO.builder(typeDTO1)
		                                                       .withSelected(true)
		                                                       .withAttributeName("qualifier1")
		                                                       .build();

		final ListItemClassificationAttributeDTO attr4 = ListItemClassificationAttributeDTO.builder(attributeAssignmentModel)
		                                  .withSelected(true)
		                                  .withAttributeName(alias)
		                                  .build();

		final List<AbstractListItemDTO> attrList = new ArrayList<>();
		attrList.add(attr1);
		attrList.add(attr2);
		attrList.add(attr3);
		attrList.add(attr4);

		return attrList;
	}
}
