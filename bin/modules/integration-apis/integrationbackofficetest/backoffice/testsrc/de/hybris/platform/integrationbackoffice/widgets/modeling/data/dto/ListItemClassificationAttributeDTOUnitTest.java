/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ListItemClassificationAttributeDTOUnitTest
{
	final private String qualifier = "weight";
	final private String category = "dimensions";

	@Test
	public void testListItemClassificationAttributeDTOConstructor()
	{
		final ListItemClassificationAttributeDTO actual = TypeCreatorTestUtils.createClassificationAttributeDTO("", qualifier,
				ClassificationAttributeTypeEnum.STRING, category);

		assertEquals(qualifier, actual.getAlias());
		assertEquals(category, actual.getCategoryCode());
	}

	@Test
	public void testListItemClassificationAttributeDTOSetAlias()
	{
		final ListItemClassificationAttributeDTO actual = TypeCreatorTestUtils.createClassificationAttributeDTO("", qualifier,
				ClassificationAttributeTypeEnum.STRING, category);

		actual.setAlias("");
		assertEquals(qualifier, actual.getAlias());

		final String alias = "aliasOfWeight";
		actual.setAlias(alias);
		assertEquals(alias, actual.getAlias());
	}

	@Test
	public void testCreateDescriptionNonReference()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(null);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(false);
		lenient().when(assignmentModel.getAttributeType()).thenReturn(type);

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "number";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionNonReferenceList()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(null);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(true);
		lenient().when(assignmentModel.getAttributeType()).thenReturn(type);

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "Collection [number]";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionReferenceList()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ComposedTypeModel ctm = mock(ComposedTypeModel.class);
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(ctm);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(true);
		lenient().when(ctm.getCode()).thenReturn("Product");

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "Collection [Product]";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionReference()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ComposedTypeModel ctm = mock(ComposedTypeModel.class);
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(ctm);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(false);
		lenient().when(ctm.getCode()).thenReturn("Product");

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "Product";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionEnum()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.ENUM;
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(null);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(false);
		lenient().when(assignmentModel.getAttributeType()).thenReturn(type);

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "ValueList";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionLocalized()
	{
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		final ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		final ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		lenient().when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		lenient().when(classModel.getCode()).thenReturn("class");
		lenient().when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		lenient().when(attr.getCode()).thenReturn("code");
		lenient().when(assignmentModel.getReferenceType()).thenReturn(null);
		lenient().when(assignmentModel.getMultiValued()).thenReturn(false);
		lenient().when(assignmentModel.getAttributeType()).thenReturn(type);
		lenient().when(assignmentModel.getLocalized()).thenReturn(true);

		final ListItemClassificationAttributeDTO dto =
				ListItemClassificationAttributeDTO.builder(assignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();

		final String expected = "localized:number";
		final String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testFindMatchFound()
	{
		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final String ccCode = "test";
		final String attrCode = "test";
		final IntegrationMapKeyDTO mapKeyDTO = new IntegrationMapKeyDTO(compType1, ccCode);
		final ListItemClassificationAttributeDTO dtoToMatch = createListitem(ccCode, attrCode);
		final ListItemClassificationAttributeDTO dto2 = createListitem("", "");

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dtoToMatch);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKeyDTO, dtos);

		final AbstractListItemDTO result = dtoToMatch.findMatch(testMap, mapKeyDTO);

		assertEquals(dtoToMatch, result);
	}

	@Test
	public void testFindMatchNotFound()
	{
		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final String ccModelCodeToMatch = "ccModelCodeToMatch";
		final IntegrationMapKeyDTO mapKeyDTO = new IntegrationMapKeyDTO(compType1, ccModelCodeToMatch);

		final ListItemClassificationAttributeDTO dtoToMatch = createListitem(ccModelCodeToMatch, "test");
		final ListItemClassificationAttributeDTO dto2 = createListitem("ccModelCode", "attrCode");

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dto2);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKeyDTO, dtos);

		assertThatThrownBy(() -> dtoToMatch.findMatch(testMap, mapKeyDTO))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage(String.format("No ClassificationAttribute was found for %s",
						dtoToMatch.getClassificationAttributeCode()));
	}

	@Test
	public void testGetTypeCodeNoAlias()
	{
		final ComposedTypeModel composedTypeModel = TypeCreatorTestUtils.composedTypeModel("Test");
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = TypeCreatorTestUtils.referenceClassAttributeAssignmentModel(
				"Qualifier", composedTypeModel);

		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
		                                                                                 .withAttributeName("AttrName")
		                                                                                 .build();

		assertEquals("Test", dto.getTypeCode());
	}

	@Test
	public void testGetTypeCodeWithAlias()
	{
		final ComposedTypeModel composedTypeModel = TypeCreatorTestUtils.composedTypeModel("Test");
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = TypeCreatorTestUtils.referenceClassAttributeAssignmentModel(
				"Qualifier", composedTypeModel);

		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
		                                                                                 .withAttributeName("AttrName")
		                                                                                 .withTypeAlias("TestAlias")
		                                                                                 .build();

		assertEquals("TestAlias", dto.getTypeCode());
	}

	@Test
	public void testGetTypeCodeWithAliasForStringClassification()
	{
		final String classificationAttribute = "Qualifier";
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = TypeCreatorTestUtils.stringClassAttributeAssignmentModel(
				classificationAttribute);

		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
		                                                                                 .withAttributeName("AttrName")
		                                                                                 .withTypeAlias("TestAlias")
		                                                                                 .build();

		assertEquals("TestAlias", dto.getTypeCode());
	}

	@Test
	public void testGetTypeCodeNoAliasForStringClassification()
	{
		final String classificationAttribute = "Qualifier";
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = TypeCreatorTestUtils.stringClassAttributeAssignmentModel(
				classificationAttribute);

		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
		                                                                                 .withAttributeName("AttrName")
		                                                                                 .build();

		assertEquals("string", dto.getTypeCode());
	}

	private ListItemClassificationAttributeDTO createListitem(final String ccModelCode, final String attrModelCode)
	{
		final ClassAttributeAssignmentModel assignmentModel = new ClassAttributeAssignmentModel();
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		final ClassificationClassModel ccModel = new ClassificationClassModel();
		ccModel.setCode(ccModelCode);
		final ClassificationAttributeModel attributeModel = new ClassificationAttributeModel();
		attributeModel.setCode(attrModelCode);
		assignmentModel.setClassificationAttribute(attributeModel);
		assignmentModel.setClassificationClass(ccModel);
		assignmentModel.setAttributeType(type);
		assignmentModel.setMultiValued(false);

		return ListItemClassificationAttributeDTO.builder(assignmentModel)
		                                         .withSelected(true)
		                                         .build();
	}
}
