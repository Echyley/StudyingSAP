/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemVirtualAttributeDTO;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WriteServiceUnitTest
{
	@Mock
	private ReadService readService;
	@Mock
	private ModelService modelService;
	@Mock
	private ReturnIntegrationObjectItemService returnIOIService;

	@InjectMocks
	private WriteService writeService;

	@Before
	public void setUp()
	{
		lenient().when(modelService.create(IntegrationObjectItemModel.class)).thenReturn(new IntegrationObjectItemModel());
		lenient().when(modelService.create(IntegrationObjectItemAttributeModel.class)).thenReturn(
				new IntegrationObjectItemAttributeModel());
		lenient().when(modelService.create(IntegrationObjectItemClassificationAttributeModel.class)).thenReturn(
				new IntegrationObjectItemClassificationAttributeModel());
		lenient().when(modelService.create(IntegrationObjectItemVirtualAttributeModel.class)).thenReturn(
				new IntegrationObjectItemVirtualAttributeModel());
	}

	@Test
	public void testCreateIntegrationObject()
	{
		final String name = "InboundStockLevel";
		final IntegrationObjectModel io = spy(IntegrationObjectModel.class);
		lenient().when(modelService.create(IntegrationObjectModel.class)).thenReturn(io);

		final IntegrationObjectModel actual = writeService.createIntegrationObject(name, IntegrationType.INBOUND);

		assertEquals(name, actual.getCode());
		assertEquals(IntegrationType.INBOUND, actual.getIntegrationType());
		assertEquals(0, actual.getItems().size());
		verify(modelService, times(1)).create(IntegrationObjectModel.class);
	}

	@Test
	public void testPersistDefinitionsClear()
	{
		final IntegrationObjectModel io = mockIntegrationObject();

		writeService.createDefinitions(io, new IntegrationObjectDefinition(), "");

		assertEquals(0, io.getItems().size());
	}

	@Test
	public void testPersistDefinitionsClearWithPK()
	{
		final IntegrationObjectItemModel integrationObjectItemModel = mock(IntegrationObjectItemModel.class);
		final PK pk = PK.fromLong(1);
		lenient().when(integrationObjectItemModel.getPk()).thenReturn(pk);

		final IntegrationObjectModel io = new IntegrationObjectModel();
		final Set<IntegrationObjectItemModel> ioItems = new HashSet<>();
		ioItems.add(integrationObjectItemModel);
		io.setItems(ioItems);

		writeService.clearIntegrationObject(io);

		assertNull(io.getItems());
	}

	@Test
	public void testCreateDefinitions()
	{
		final IntegrationObjectModel integrationObjectModel = new IntegrationObjectModel();
		integrationObjectModel.setItems(Collections.emptySet());
		final String rootCode = "Product";

		// Classification Attribute
		final ListItemClassificationAttributeDTO classificationAttribute = TypeCreatorTestUtils.createClassificationAttributeDTO(
				"",
				"weight",
				ClassificationAttributeTypeEnum.STRING, "dimensions");

		// Regular attribute
		final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
		attributeDescriptorModel.setQualifier("code");
		attributeDescriptorModel.setAttributeType(new TypeModel());
		attributeDescriptorModel.setUnique(true);
		final AttributeTypeDTO typeDTO = AttributeTypeDTO.builder(attributeDescriptorModel).build();
		final ListItemAttributeDTO attribute = ListItemAttributeDTO.builder(typeDTO)
		                                                           .withSelected(true)
		                                                           .build();

		// Map of attributes
		final IntegrationObjectDefinition map = new IntegrationObjectDefinition();
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		lenient().when(composedTypeModel.getCode()).thenReturn(rootCode);

		final List<AbstractListItemDTO> list = new ArrayList<>();
		list.add(attribute);
		list.add(classificationAttribute);

		final IntegrationMapKeyDTO mapKey = new IntegrationMapKeyDTO(composedTypeModel, "Product");
		map.setAttributesByKey(mapKey, list);

		writeService.createDefinitions(integrationObjectModel, map, rootCode);

		final IntegrationObjectItemModel rootItem = integrationObjectModel.getItems().iterator().next();

		assertTrue(rootItem.getRoot());
		assertEquals(1, rootItem.getAttributes().size());
		assertEquals(1, rootItem.getClassificationAttributes().size());
	}

	@Test
	public void testCloneIntegrationObject()
	{
		final ComposedTypeModel userType = new ComposedTypeModel();
		userType.setCode("User");
		final IntegrationObjectModel integrationObjectModel = new IntegrationObjectModel();
		final IntegrationObjectItemModel userItem = new IntegrationObjectItemModel();
		userItem.setCode("InboundUser");
		userItem.setIntegrationObject(integrationObjectModel);
		userItem.setType(userType);
		userItem.setRoot(true);
		userItem.setItemTypeMatch(ItemTypeMatchEnum.ALL_SUB_AND_SUPER_TYPES);
		userItem.setAttributes(Collections.emptySet());
		userItem.setClassificationAttributes(Collections.emptySet());
		userItem.setVirtualAttributes(Collections.emptySet());
		integrationObjectModel.setItems(Set.of(userItem));
		final IntegrationObjectModel integrationObjectModelClone = new IntegrationObjectModel();

		final IntegrationObjectModel actual = writeService.cloneIntegrationObject(integrationObjectModel,
				integrationObjectModelClone);
		final IntegrationObjectItemModel actualUserItem = actual.getItems().iterator().next();

		assertEquals("Expect root flag to be same to same", userItem.getRoot(), actualUserItem.getRoot());
		assertEquals("Expect item type to be the same", userItem.getType(), actualUserItem.getType());
		assertNotEquals("Expect alias to not be item type code", userItem.getType().getCode(), actualUserItem.getCode());
		assertEquals("Expect item alias to be the same", userItem.getCode(), actualUserItem.getCode());
		assertEquals("Expect integration object to be set by argument", integrationObjectModelClone,
				actualUserItem.getIntegrationObject());
		assertEquals("Expect item type match to be the same", userItem.getItemTypeMatch(), actualUserItem.getItemTypeMatch());
	}

	@Test
	public void testPersistIntegrationObject()
	{
		final IntegrationObjectModel integrationObjectModel = new IntegrationObjectModel();
		writeService.persistIntegrationObject(integrationObjectModel);
		verify(modelService, times(1)).save(integrationObjectModel);
	}

	@Test
	public void testPersistIntegrationObjectItem()
	{
		final Set<IntegrationObjectItemModel> set = Set.of(new IntegrationObjectItemModel());
		writeService.persistIntegrationObjectItems(set);
		verify(modelService).saveAll(set);
	}

	@Test
	public void testBuildIntegrationObjectItem()
	{
		final String code = "StockLevel";
		final IntegrationObjectModel ioModel = mock(IntegrationObjectModel.class);
		final ComposedTypeModel ctm = mock(ComposedTypeModel.class);
		lenient().when(ctm.getCode()).thenReturn(code);
		final IntegrationMapKeyDTO keyDTO = new IntegrationMapKeyDTO(ctm, code);

		final IntegrationObjectItemModel actual = writeService.buildIntegrationObjectItem(ioModel, keyDTO, "");

		verify(modelService, times(1)).create(IntegrationObjectItemModel.class);
		assertEquals(code, actual.getCode());
		assertEquals(ioModel, actual.getIntegrationObject());
		assertEquals(ctm, actual.getType());
	}

	@Test
	public void itemIsRootWhenBuildIntegrationObjectItemAndKeyCodeIsRootCode()
	{
		final IntegrationObjectModel io = new IntegrationObjectModel();
		final IntegrationMapKeyDTO keyDTO = new IntegrationMapKeyDTO(new ComposedTypeModel(), "Address");
		final IntegrationObjectItemModel actualItem = writeService.buildIntegrationObjectItem(io, keyDTO, "Address");
		assertTrue("Item is root.", actualItem.getRoot());
	}

	@Test
	public void itemIsNotRootWhenBuildIntegrationObjectItemAndKeyCodeIsNotRootCode()
	{
		final IntegrationObjectModel io = new IntegrationObjectModel();
		final IntegrationMapKeyDTO keyDTO = new IntegrationMapKeyDTO(new ComposedTypeModel(), "Address2");
		final IntegrationObjectItemModel actualItem = writeService.buildIntegrationObjectItem(io, keyDTO, "Address");
		assertFalse("Item is not root.", actualItem.getRoot());
	}

	@Test
	public void testCategorizeDTOs()
	{
		final ListItemAttributeDTO attributeDTO = mock(ListItemAttributeDTO.class);
		final ListItemClassificationAttributeDTO classificationAttributeDTO = mock(ListItemClassificationAttributeDTO.class);
		final ListItemVirtualAttributeDTO virtualAttributeDTO = mock(ListItemVirtualAttributeDTO.class);
		final List<AbstractListItemDTO> abstractDTOs = List.of(attributeDTO, classificationAttributeDTO, virtualAttributeDTO);
		final Set<ListItemAttributeDTO> attributeDTOs = new HashSet<>();
		final Set<ListItemClassificationAttributeDTO> classificationAttributesDTOs = new HashSet<>();
		final Set<ListItemVirtualAttributeDTO> virtualAttributeDTOs = new HashSet<>();
		writeService.categorizeDTOs(abstractDTOs, attributeDTOs, classificationAttributesDTOs, virtualAttributeDTOs);
		assertEquals(attributeDTO, attributeDTOs.iterator().next());
		assertEquals(classificationAttributeDTO, classificationAttributesDTOs.iterator().next());
		assertEquals(virtualAttributeDTO, virtualAttributeDTOs.iterator().next());
	}

	@Test
	public void testBuildIntegrationObjectItemAttribute()
	{
		final AttributeDescriptorModel attributeDescriptor1 = new AttributeDescriptorModel();
		attributeDescriptor1.setQualifier("q1");
		attributeDescriptor1.setAttributeType(new TypeModel());
		attributeDescriptor1.setUnique(true);
		final AttributeDescriptorModel attributeDescriptor2 = new AttributeDescriptorModel();
		attributeDescriptor2.setQualifier("q2");
		attributeDescriptor2.setAttributeType(new TypeModel());
		attributeDescriptor2.setUnique(false);
		final AttributeDescriptorModel attributeDescriptor3 = new AttributeDescriptorModel();
		attributeDescriptor3.setQualifier("q3");
		attributeDescriptor3.setAttributeType(new TypeModel());
		attributeDescriptor3.setUnique(true);
		final AttributeDescriptorModel attributeDescriptor4 = new AttributeDescriptorModel();
		attributeDescriptor4.setQualifier("q4");
		attributeDescriptor4.setAttributeType(new TypeModel());
		attributeDescriptor4.setUnique(false);

		final AttributeTypeDTO t1 = AttributeTypeDTO.builder(attributeDescriptor1).build();
		final AttributeTypeDTO t2 = AttributeTypeDTO.builder(attributeDescriptor2).build();
		final AttributeTypeDTO t3 = AttributeTypeDTO.builder(attributeDescriptor3).build();
		final AttributeTypeDTO t4 = AttributeTypeDTO.builder(attributeDescriptor4).build();

		final ListItemAttributeDTO dto1 = ListItemAttributeDTO.builder(t1).withSelected(true).build();
		final ListItemAttributeDTO dto2 = ListItemAttributeDTO.builder(t2).withSelected(true).build();
		final ListItemAttributeDTO dto3 = ListItemAttributeDTO.builder(t3).withSelected(true).build();
		final ListItemAttributeDTO dto4 = ListItemAttributeDTO.builder(t4).withSelected(true).build();

		final Set<ListItemAttributeDTO> dtos = new HashSet<>();
		dtos.add(dto1);
		dtos.add(dto2);
		dtos.add(dto3);
		dtos.add(dto4);

		final IntegrationObjectItemModel ioItem = mock(IntegrationObjectItemModel.class);

		final Set<IntegrationObjectItemAttributeModel> actual = writeService.buildIntegrationObjectItemAttribute(dtos, ioItem);

		actual.forEach(ioia -> dtos.forEach(dto -> {
			final AttributeDescriptorModel attributeDescriptor = dto.getAttributeDescriptor();
			if (attributeDescriptor.equals(ioia.getAttributeDescriptor()))
			{
				assertEquals(attributeDescriptor.getQualifier(), ioia.getAttributeName());
				assertEquals(ioItem, ioia.getIntegrationObjectItem());
				assertEquals(attributeDescriptor.getUnique() || dto.isCustomUnique(), ioia.getUnique());
				assertEquals(dto.isAutocreate(), ioia.getAutoCreate());
				assertNull(ioia.getReturnIntegrationObjectItem());
			}
		}));
	}

	@Test
	public void testBuildIntegrationObjectItemClassificationAttribute()
	{
		final ClassificationAttributeModel classificationAttributeModel = new ClassificationAttributeModel();
		classificationAttributeModel.setCode("classAttrCode");

		final ClassificationClassModel classificationClassModel = new ClassificationClassModel();
		classificationClassModel.setCode("classCode");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		final ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.STRING;
		classAttributeAssignmentModel.setAttributeType(type);
		classAttributeAssignmentModel.setClassificationAttribute(classificationAttributeModel);
		classAttributeAssignmentModel.setClassificationClass(classificationClassModel);
		classAttributeAssignmentModel.setMultiValued(false);

		final ListItemClassificationAttributeDTO dto1 =
				ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
				                                  .withSelected(true)
				                                  .withAttributeName("name")
				                                  .build();
		final Set<ListItemClassificationAttributeDTO> dtos = new HashSet<>();
		dtos.add(dto1);

		final IntegrationObjectItemModel ioItem = mock(IntegrationObjectItemModel.class);

		final Set<IntegrationObjectItemClassificationAttributeModel> actual = writeService.buildIntegrationObjectItemClassificationAttribute(
				dtos, ioItem);

		actual.forEach(ioica -> dtos.forEach(dto -> {
			assertEquals(dto.getClassAttributeAssignmentModel(), ioica.getClassAttributeAssignment());
			assertEquals(dto.getAlias(), dto.getAlias());
		}));
	}

	@Test
	public void testDeleteIntegrationObject()
	{
		final IntegrationObjectModel integrationObject = mock(IntegrationObjectModel.class);
		final PK integrationObjectPK = PK.fromLong(123);
		lenient().when(integrationObject.getPk()).thenReturn(integrationObjectPK);
		writeService.deleteIntegrationObject(integrationObject);
		verify(modelService, times(1)).remove(integrationObjectPK);
	}

	@Test
	public void testCloneIntegrationObjectItemAttribute()
	{
		final ComposedTypeModel type = new ComposedTypeModel();
		type.setCode("catalog");

		// A IOI that is used to set for IOIA's return IOI
		final String returnIOICode = "Catalog";
		final IntegrationObjectItemModel returnIntegrationObject = new IntegrationObjectItemModel();
		returnIntegrationObject.setType(type);
		returnIntegrationObject.setCode(returnIOICode);

		// IOIA to be cloned.
		final AttributeDescriptorModel attributeDescriptor = new AttributeDescriptorModel();
		final IntegrationObjectItemAttributeModel attribute = new IntegrationObjectItemAttributeModel();
		attribute.setAttributeDescriptor(attributeDescriptor);
		attribute.setAttributeName("catalog");
		attribute.setIntegrationObjectItem(new IntegrationObjectItemModel());
		attribute.setUnique(false);
		attribute.setReturnIntegrationObjectItem(returnIntegrationObject);
		attribute.setAutoCreate(false);

		// A cloned IOI from IOI above. All IOI are cloned then processing IOIA cloning.
		final IntegrationObjectItemModel itemClone = new IntegrationObjectItemModel();
		final Map<String, IntegrationObjectItemModel> itemsCloneMap = new HashMap<>();
		final IntegrationObjectItemModel clonedCatalogIntegrationObjectItem = new IntegrationObjectItemModel();
		clonedCatalogIntegrationObjectItem.setType(type);
		clonedCatalogIntegrationObjectItem.setCode(returnIOICode);
		itemsCloneMap.put(clonedCatalogIntegrationObjectItem.getCode(), clonedCatalogIntegrationObjectItem);

		final IntegrationObjectItemAttributeModel actual = writeService.cloneIntegrationObjectItemAttribute(attribute,
				itemClone, itemsCloneMap);

		assertEquals("Expect attribute descriptor to be the same", attributeDescriptor, actual.getAttributeDescriptor());
		assertEquals("Expect attribute name to be the same", attribute.getAttributeName(), actual.getAttributeName());
		assertEquals("Expect integration object item to be set by argument", itemClone, actual.getIntegrationObjectItem());
		assertEquals("Expect unique to be the same", attribute.getUnique(), actual.getUnique());
		assertEquals("Expect autocreate to be the same", attribute.getAutoCreate(), actual.getAutoCreate());
		assertNotEquals("Expect returnIntegrationObjectItem not to be shallow copy", returnIntegrationObject,
				actual.getReturnIntegrationObjectItem());
		assertEquals("Expect returnIntegrationObjectItem to be clone integration object item", clonedCatalogIntegrationObjectItem,
				actual.getReturnIntegrationObjectItem());
	}

	@Test
	public void testCloneIntegrationObjectItemClassificationAttribute()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = new ClassAttributeAssignmentModel();
		final IntegrationObjectItemClassificationAttributeModel classificationAttribute =
				new IntegrationObjectItemClassificationAttributeModel();
		classificationAttribute.setClassAttributeAssignment(classAttributeAssignment);
		classificationAttribute.setAttributeName("length");
		classificationAttribute.setIntegrationObjectItem(new IntegrationObjectItemModel());
		classificationAttribute.setAutoCreate(false);
		classificationAttribute.setReturnIntegrationObjectItem(null);
		final IntegrationObjectItemModel itemClone = new IntegrationObjectItemModel();
		final Map<String, IntegrationObjectItemModel> itemsCloneMap = new HashMap<>();

		final IntegrationObjectItemClassificationAttributeModel actual =
				writeService.cloneIntegrationObjectItemClassificationAttribute(classificationAttribute, itemClone, itemsCloneMap);

		assertEquals("Expect class attribute assignment to be the same", classAttributeAssignment,
				actual.getClassAttributeAssignment());
		assertEquals("Expect attribute name to be the same", classificationAttribute.getAttributeName(),
				actual.getAttributeName());
		assertEquals("Expect integration object item to be set by argument", itemClone, actual.getIntegrationObjectItem());
		assertEquals("Expect autocreate to be the same", classificationAttribute.getAutoCreate(), actual.getAutoCreate());
		assertNull("Expect returnIntegrationObjectItem to be null because original attribute returnIntegrationObjectItem is null",
				actual.getReturnIntegrationObjectItem());
	}

	@Test
	public void testCloneIntegrationObjectItemVirtualAttributeModel()
	{
		final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor = new IntegrationObjectVirtualAttributeDescriptorModel();
		final IntegrationObjectItemVirtualAttributeModel virtualAttribute = new IntegrationObjectItemVirtualAttributeModel();
		virtualAttribute.setRetrievalDescriptor(retrievalDescriptor);
		virtualAttribute.setAttributeName("virtualCode");
		virtualAttribute.setIntegrationObjectItem(new IntegrationObjectItemModel());
		virtualAttribute.setAutoCreate(false);
		final IntegrationObjectItemModel itemClone = new IntegrationObjectItemModel();

		final IntegrationObjectItemVirtualAttributeModel actual = writeService.cloneIntegrationObjectItemVirtualAttributeModel(
				virtualAttribute, itemClone);

		assertEquals("Expect retrieval descriptor to be the same", retrievalDescriptor, actual.getRetrievalDescriptor());
		assertEquals("Expect attribute name to be the same", virtualAttribute.getAttributeName(), actual.getAttributeName());
		assertEquals("Expect integration object item to be set by argument", itemClone, actual.getIntegrationObjectItem());
		assertFalse("Expect autocreate to be false", actual.getAutoCreate());
	}

	public static IntegrationObjectModel mockIntegrationObject()
	{
		final IntegrationObjectModel io = new IntegrationObjectModel();

		// 'Product' IntegrationObjectItem (top-level item)
		final IntegrationObjectItemModel ioItemProduct = spy(IntegrationObjectItemModel.class);
		final ComposedTypeModel productComposedType = new ComposedTypeModel();
		final String productCode = "Product";
		ioItemProduct.setCode(productCode);
		productComposedType.setCode(productCode);
		ioItemProduct.setType(productComposedType);

		// 'CatalogVersion' integration object item
		final IntegrationObjectItemModel ioItemCatalogVersion = spy(IntegrationObjectItemModel.class);
		final ComposedTypeModel catalogVersionComposedType = new ComposedTypeModel();
		final String catalogVersionCode = "CatalogVersion";
		ioItemCatalogVersion.setCode(catalogVersionCode);
		catalogVersionComposedType.setCode(catalogVersionCode);
		ioItemCatalogVersion.setType(catalogVersionComposedType);

		// AttributeDescriptor for 'CatalogVersion' IOIA (contained by 'Product')
		final AttributeDescriptorModel catalogVersionDescriptor = new AttributeDescriptorModel();
		catalogVersionDescriptor.setAttributeType(catalogVersionComposedType);

		// Product's CatalogVersion(IOI)'s 'catalogVersion' IOIA
		final IntegrationObjectItemAttributeModel ioiAttributeCatalogVersion = new IntegrationObjectItemAttributeModel();
		ioiAttributeCatalogVersion.setAttributeDescriptor(catalogVersionDescriptor);
		ioiAttributeCatalogVersion.setAttributeName("catalogVersion");

		// add IOIA to 'Product' IOI
		final Set<IntegrationObjectItemAttributeModel> productAttributes = new HashSet<>();
		productAttributes.add(ioiAttributeCatalogVersion);
		ioItemProduct.setAttributes(productAttributes);


		// 'Language' integration object item
		final IntegrationObjectItemModel ioItemLanguage = spy(IntegrationObjectItemModel.class);
		final ComposedTypeModel languageComposedType = new ComposedTypeModel();
		final String languageCode = "Language";
		ioItemLanguage.setCode(languageCode);
		languageComposedType.setCode(languageCode);
		ioItemLanguage.setType(languageComposedType);

		// 'code' integration object item attribute (contained by 'CatalogVersion' and 'Language')
		final TypeModel mockType2 = mock(TypeModel.class);
		lenient().when(mockType2.getItemtype()).thenReturn("AtomicType");

		// AttributeDescripto for 'code' IOIA
		final AttributeDescriptorModel codeDescriptor = new AttributeDescriptorModel();
		codeDescriptor.setAttributeType(mockType2);

		// 'code' IOIA shared by 'Language' IOI and 'catalogVersion' IOI
		final IntegrationObjectItemAttributeModel ioiAttributeCode = new IntegrationObjectItemAttributeModel();
		ioiAttributeCode.setAttributeDescriptor(codeDescriptor);
		ioiAttributeCode.setAttributeName("code");

		// add IOIA to 'Language' IOI
		final Set<IntegrationObjectItemAttributeModel> languageAttributes = new HashSet<>();
		languageAttributes.add(ioiAttributeCode);
		ioItemLanguage.setAttributes(languageAttributes);


		// 'languages' integration object item attribute (contained by 'CatalogVersion')
		final TypeModel elementMockType = mock(TypeModel.class);
		lenient().when(elementMockType.getCode()).thenReturn("Language");

		final CollectionTypeModel mockType3 = mock(CollectionTypeModel.class);
		lenient().when(mockType3.getItemtype()).thenReturn("CollectionType");
		lenient().when(mockType3.getElementType()).thenReturn(elementMockType);

		final AttributeDescriptorModel languagesDescriptor = new AttributeDescriptorModel();
		languagesDescriptor.setAttributeType(mockType3);

		// 'languages' IOIA that represents a collection of 'Language' typeModel
		final IntegrationObjectItemAttributeModel ioiAttributeLanguages = new IntegrationObjectItemAttributeModel();
		ioiAttributeLanguages.setAttributeDescriptor(languagesDescriptor);
		ioiAttributeLanguages.setAttributeName("languages");

		// add IOIA to 'CatalogVersion' IOI
		final Set<IntegrationObjectItemAttributeModel> catalogVersionAttributes = new HashSet<>();
		catalogVersionAttributes.add(ioiAttributeCode);
		catalogVersionAttributes.add(ioiAttributeLanguages);
		ioItemCatalogVersion.setAttributes(catalogVersionAttributes);

		final Set<IntegrationObjectItemModel> ioItems = new HashSet<>();
		ioItems.add(ioItemProduct);
		ioItems.add(ioItemCatalogVersion);
		ioItems.add(ioItemLanguage);

		for (final IntegrationObjectItemModel item : ioItems)
		{
			item.setClassificationAttributes(Collections.emptySet());
		}

		io.setItems(ioItems);
		return io;
	}
}
