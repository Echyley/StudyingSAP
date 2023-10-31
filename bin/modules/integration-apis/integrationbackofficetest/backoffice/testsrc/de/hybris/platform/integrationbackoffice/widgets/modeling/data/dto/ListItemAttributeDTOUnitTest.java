/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ListItemAttributeDTOUnitTest
{
	@Test
	public void testListItemAttributeDTOConstructor()
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");

		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setAttributeType(typeModel);
		attributeDescriptor.setUnique(true);
		attributeDescriptor.setOptional(false);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor).build();
		final AbstractListItemDTO actual = ListItemAttributeDTO.builder(typeDTO)
		                                                       .withSelected(true)
		                                                       .build();


		assertEquals(typeModel.getCode(), actual.getDescription());
	}

	@Test
	public void testListItemAttributeDTOIsRequired()
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");

		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setAttributeType(typeModel);
		attributeDescriptor.setUnique(true);
		attributeDescriptor.setOptional(false);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor).build();
		final ListItemAttributeDTO actual = ListItemAttributeDTO.builder(typeDTO)
		                                                        .withSelected(true)
		                                                        .build();

		assertTrue(actual.isRequired());

		attributeDescriptor.setUnique(false);
		assertFalse(actual.isRequired());

		attributeDescriptor.setUnique(true);
		attributeDescriptor.setOptional(true);
		assertFalse(actual.isRequired());
	}

	@Test
	public void testListItemAttributeDTOFindBaseTypeForMapOfComposedTypes()
	{
		// Map of ComposedTypeModel
		// Product -> keywords
		final ComposedTypeModel composedTypeModel = new ComposedTypeModel();
		composedTypeModel.setCode("Keyword");

		final MapTypeModel mapTypeModel = new MapTypeModel();
		mapTypeModel.setCode("name");

		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setUnique(true);
		attributeDescriptor.setOptional(false);

		mapTypeModel.setReturntype(composedTypeModel);
		attributeDescriptor.setAttributeType(mapTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.MAP)
		                                                 .build();

		final ListItemAttributeDTO actual = ListItemAttributeDTO.builder(typeDTO)
		                                                        .withSelected(true)
		                                                        .build();

		assertEquals(mapTypeModel, actual.getBaseType());
	}

	@Test
	public void testListItemAttributeDTOFindBaseTypeForMapOfCollectionOfAtomicTypes()
	{

		// Map of Collections of AtomicTypeModel
		// Product -> name
		final AtomicTypeModel atomicTypeModel = new AtomicTypeModel();
		atomicTypeModel.setCode("localized:java.lang.String");

		final CollectionTypeModel collectionTypeModel = new CollectionTypeModel();
		collectionTypeModel.setElementType(atomicTypeModel);

		final MapTypeModel mapTypeModel = new MapTypeModel();
		mapTypeModel.setCode("name");

		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setUnique(true);
		attributeDescriptor.setOptional(false);

		mapTypeModel.setReturntype(collectionTypeModel);
		attributeDescriptor.setAttributeType(mapTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.MAP)
		                                                 .build();

		final ListItemAttributeDTO actual = ListItemAttributeDTO.builder(typeDTO)
		                                                        .withSelected(true)
		                                                        .build();

		assertEquals(atomicTypeModel, actual.getBaseType());
	}

	@Test
	public void testFindMatchFound()
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");
		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setAttributeType(typeModel);
		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final IntegrationMapKeyDTO mapKey1 = new IntegrationMapKeyDTO(compType1, "Product");

		final AttributeTypeDTO typeDTOForMatch = AttributeTypeDTO.builder(attributeDescriptor)
		                                                         .withType(typeModel)
		                                                         .build();

		final AttributeTypeDTO typeDTO2 = AttributeTypeDTO.builder(attributeDescriptor).build();

		final ListItemAttributeDTO dtoToMatch = ListItemAttributeDTO.builder(typeDTOForMatch)
		                                                            .withSelected(true)
		                                                            .build();

		final ListItemAttributeDTO dto2 = ListItemAttributeDTO.builder(typeDTO2)
		                                                      .withSelected(true)
		                                                      .build();

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dtoToMatch);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKey1, dtos);

		final AbstractListItemDTO result = dtoToMatch.findMatch(testMap, mapKey1);

		assertEquals(dtoToMatch, result);
	}

	@Test
	public void testFindMatchNotFound() throws NoSuchElementException
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(typeModel);
		final TypeModel typeModel2 = TypeCreatorTestUtils.typeModel("Product2");
		final AttributeDescriptorModel attributeDescriptor2 = TypeCreatorTestUtils.attributeDescriptorModel(typeModel2);

		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final IntegrationMapKeyDTO mapKey1 = new IntegrationMapKeyDTO(compType1, "Product");

		final AttributeTypeDTO typeDTOForMatch = AttributeTypeDTO.builder(attributeDescriptor)
		                                                         .withType(typeModel)
		                                                         .build();

		final AttributeTypeDTO typeDTO2 = AttributeTypeDTO.builder(attributeDescriptor2).build();

		final ListItemAttributeDTO dtoToMatch = ListItemAttributeDTO.builder(typeDTOForMatch)
		                                                            .withSelected(true)
		                                                            .build();

		final ListItemAttributeDTO dto2 = ListItemAttributeDTO.builder(typeDTO2)
		                                                      .withSelected(true)
		                                                      .build();

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dto2);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKey1, dtos);

		assertThatThrownBy(() -> dtoToMatch.findMatch(testMap, mapKey1))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage("No AttributeDescriptor was found");
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNotNull()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(typeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor).build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).withTypeAlias("TestAlias").build();

		assertEquals("TestAlias", dto.getDescription());
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNull()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(typeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor).build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).build();

		assertEquals("Product", dto.getDescription());
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNotNullCollection()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final CollectionTypeModel collectionTypeModel = TypeCreatorTestUtils.collectionTypeModel(typeModel);
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(collectionTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.COLLECTION)
		                                                 .build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).withTypeAlias("TestAlias").build();

		assertEquals("Collection [TestAlias]", dto.getDescription());
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNotNullMap()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final CollectionTypeModel collectionTypeModel = TypeCreatorTestUtils.collectionTypeModel(typeModel);
		final MapTypeModel mapTypeModel = TypeCreatorTestUtils.mapTypeModel(collectionTypeModel);
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(mapTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.MAP)
		                                                 .build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).withTypeAlias("TestAlias").build();

		assertEquals("Map [TestAlias]", dto.getDescription());
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNullCollection()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final CollectionTypeModel collectionTypeModel = TypeCreatorTestUtils.collectionTypeModel(typeModel);
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(collectionTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.COLLECTION)
		                                                 .build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).build();

		assertEquals("Collection [Product]", dto.getDescription());
	}

	@Test
	public void testCreateDescriptionWithTypeAliasNullMap()
	{
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("Product");
		final CollectionTypeModel collectionTypeModel = TypeCreatorTestUtils.collectionTypeModel(typeModel);
		final MapTypeModel mapTypeModel = TypeCreatorTestUtils.mapTypeModel(collectionTypeModel);
		final AttributeDescriptorModel attributeDescriptor = TypeCreatorTestUtils.attributeDescriptorModel(mapTypeModel);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                 .withStructureType(ListItemStructureType.MAP)
		                                                 .build();
		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).build();

		assertEquals("Map [Product]", dto.getDescription());
	}

	@Test
	public void testGetTypeCodeNoAlias()
	{
		final TypeModel type = TypeCreatorTestUtils.typeModel("Test");
		final AttributeDescriptorModel descriptorModel = TypeCreatorTestUtils.attributeDescriptorModel(type);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(descriptorModel).build();

		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).build();

		assertEquals("Test", dto.getTypeCode());
	}

	@Test
	public void testGetTypeCodeWithAlias()
	{
		final TypeModel type = TypeCreatorTestUtils.typeModel("Test");
		final AttributeDescriptorModel descriptorModel = TypeCreatorTestUtils.attributeDescriptorModel(type);

		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(descriptorModel).build();

		final ListItemAttributeDTO dto = ListItemAttributeDTO.builder(typeDTO).withTypeAlias("TestAlias").build();

		assertEquals("TestAlias", dto.getTypeCode());
	}
}
