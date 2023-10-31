/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.getAttributeInItem;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.getItemInIntegrationObject;
import static de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils.typeModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTOBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemDTOTypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.WriteServiceUnitTest;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReturnIntegrationObjectItemServiceUnitTest
{
	private static final Map<String, ComposedTypeModel> COMPOSED_TYPE_MODEL_MAP = new HashMap<>();

	@Mock
	private ListItemDTOTypeService listItemDTOTypeService;

	@InjectMocks
	private DefaultReturnIntegrationObjectItemService service;

	@Test
	public void testSetReturnIntegrationObjectItem()
	{
		final IntegrationObjectModel io = new IntegrationObjectModel();
		final IntegrationObjectDefinition definition = new IntegrationObjectDefinition();
		final IODefinitionBuilder build = new IODefinitionBuilder(io, definition);

		// IntegrationObjectItem
		final IntegrationObjectItemModel iOI = integrationObjectItem("Product", "Product");
		final IntegrationObjectItemModel iOI2 = integrationObjectItem("CatalogVersion", "CatalogVersion");
		final IntegrationObjectItemModel iOI3 = integrationObjectItem("Language", "Language");

		// Product's catalogVersion attribute
		final TypeModel catalogVersionType = composedType("CatalogVersion");
		final AttributeDescriptorModel descriptorCatalogVersion = attributeDescriptor(DescriptorType.COMPOSEDTYPE,
				catalogVersionType);
		final IntegrationObjectItemAttributeModel iOIACatalogVersion = integrationObjectItemAttribute("catalogVersion",
				descriptorCatalogVersion);
		final ListItemAttributeDTO catalogVersionDTO = build.generateListItemAttributeDTO(iOIACatalogVersion);
		build.addIOIWithIOIA(iOI, List.of(iOIACatalogVersion), List.of(catalogVersionDTO));
		lenient().when(listItemDTOTypeService.getDTOType(catalogVersionDTO)).thenReturn(catalogVersionType);

		// CatalogVersion's code and languages attributes
		final AttributeDescriptorModel descriptorCode = attributeDescriptor(DescriptorType.ATOMICTYPE, typeModel("Code"));
		final IntegrationObjectItemAttributeModel iOIACode = integrationObjectItemAttribute("code", descriptorCode);
		final ListItemAttributeDTO codeDTO = build.generateListItemAttributeDTO(iOIACode);
		final TypeModel languageType = typeModel("Language");
		final AttributeDescriptorModel descriptorLanguage = attributeDescriptor(DescriptorType.COLLECTIONTYPE, languageType);
		final IntegrationObjectItemAttributeModel iOIALanguage = integrationObjectItemAttribute("languages", descriptorLanguage);
		final ListItemAttributeDTO languagesDTO = build.generateListItemAttributeDTO(iOIALanguage);
		build.addIOIWithIOIA(iOI2, List.of(iOIACode, iOIALanguage), List.of(codeDTO, languagesDTO));
		lenient().when(listItemDTOTypeService.getDTOType(codeDTO)).thenReturn(null);
		lenient().when(listItemDTOTypeService.getDTOType(languagesDTO)).thenReturn(languageType);

		// Language's code attribute
		final AttributeDescriptorModel descriptorCode2 = attributeDescriptor(DescriptorType.ATOMICTYPE, typeModel("Code"));
		final IntegrationObjectItemAttributeModel iOIACode2 = integrationObjectItemAttribute("code", descriptorCode2);
		final ListItemAttributeDTO codeDTO2 = build.generateListItemAttributeDTO(iOIACode2);
		build.addIOIWithIOIA(iOI3, List.of(iOIACode2), List.of(codeDTO2));
		lenient().when(listItemDTOTypeService.getDTOType(codeDTO2)).thenReturn(null);

		assertReturnIntegrationObjectItemNotSet(io);

		service.setReturnIntegrationObjectItems(io, definition);

		final IntegrationObjectItemModel product = getItemInIntegrationObject(io, "Product");
		final IntegrationObjectItemModel catalogVersion = getItemInIntegrationObject(io, "CatalogVersion");
		final AbstractIntegrationObjectItemAttributeModel catalogVersionAttr = getAttributeInItem(product, "catalogVersion");

		assertEquals("'CatalogVersion' IOI is returnIntegrationObjectItem of 'catalogVersion' IOIA in 'Product' IOI.",
				catalogVersion, catalogVersionAttr.getReturnIntegrationObjectItem());
	}

	@Test
	public void testSetReturnIntegrationObjectItemWithMultipleDefinition()
	{
		final IntegrationObjectModel io = new IntegrationObjectModel();
		final IntegrationObjectDefinition definition = new IntegrationObjectDefinition();
		final IODefinitionBuilder build = new IODefinitionBuilder(io, definition);

		// IntegrationObjectItem
		final IntegrationObjectItemModel iOI = integrationObjectItem("Product", "Product");
		final IntegrationObjectItemModel iOI2 = integrationObjectItem("CatalogVersion", "CatalogVersion");
		iOI2.setAttributes(new HashSet<>());
		final IntegrationObjectItemModel iOI3 = integrationObjectItem("CatalogVersion2", "CatalogVersion");
		iOI3.setAttributes(new HashSet<>());
		build.addIntegrationObjectItem(iOI);
		build.addIntegrationObjectItem(iOI2);
		build.addIntegrationObjectItem(iOI3);

		// Product's catalogVersion attribute
		final TypeModel catalogVersionType = composedType("CatalogVersion");
		final AttributeDescriptorModel descriptorCatalogVersion = attributeDescriptor(DescriptorType.COMPOSEDTYPE,
				catalogVersionType);
		final IntegrationObjectItemAttributeModel iOIACatalogVersion = integrationObjectItemAttribute("catalogVersion",
				descriptorCatalogVersion);
		final ListItemAttributeDTO catalogVersionDTO = ListItemAttributeDTO
				.builder(AttributeTypeDTO.builder(descriptorCatalogVersion).build())
				.withSelected(true)
				.withAttributeName("catalogVersion")
				.withTypeAlias("")
				.build();
		lenient().when(listItemDTOTypeService.getDTOType(catalogVersionDTO)).thenReturn(catalogVersionType);

		// Product's catalogVersion2 attribute
		final IntegrationObjectItemAttributeModel iOIACatalogVersion2 = integrationObjectItemAttribute("catalogVersion2",
				descriptorCatalogVersion);
		final ListItemAttributeDTO catalogVersion2DTO = ListItemAttributeDTO
				.builder(AttributeTypeDTO.builder(descriptorCatalogVersion).build())
				.withSelected(true)
				.withAttributeName("catalogVersion2")
				.withTypeAlias("CatalogVersion2")
				.build();
		lenient().when(listItemDTOTypeService.getDTOType(catalogVersion2DTO)).thenReturn(catalogVersionType);

		build.addIOIWithIOIA(iOI, List.of(iOIACatalogVersion, iOIACatalogVersion2),
				List.of(catalogVersionDTO, catalogVersion2DTO));

		assertReturnIntegrationObjectItemNotSet(io);

		service.setReturnIntegrationObjectItems(io, definition);

		final IntegrationObjectItemModel product = getItemInIntegrationObject(io, "Product");
		final IntegrationObjectItemModel catalogVersionItem = getItemInIntegrationObject(io, "CatalogVersion");
		final IntegrationObjectItemModel catalogVersion2Item = getItemInIntegrationObject(io, "CatalogVersion2");
		final AbstractIntegrationObjectItemAttributeModel catalogVersionAttr = getAttributeInItem(product, "catalogVersion");
		final AbstractIntegrationObjectItemAttributeModel catalogVersion2Attr = getAttributeInItem(product, "catalogVersion2");

		assertEquals("'CatalogVersion' IOI is returnIntegrationObjectItem of 'catalogVersion' IOIA in 'Product' IOI.",
				catalogVersionItem, catalogVersionAttr.getReturnIntegrationObjectItem());
		assertEquals("'CatalogVersion2' IOI is returnIntegrationObjectItem of 'catalogVersion2' IOIA in 'Product' IOI.",
				catalogVersion2Item, catalogVersion2Attr.getReturnIntegrationObjectItem());
	}

	@Test
	public void testPersistReturnIntegrationObjectItemWithSubtype()
	{
		IntegrationObjectModel mockIO = WriteServiceUnitTest.mockIntegrationObject();
		IntegrationObjectDefinition convertedMap = mockDTOMap(mockIO);

		mockIO = addIOIToExistingIO(mockIO);
		convertedMap = addMapEntry(mockIO, convertedMap);

		assertReturnIntegrationObjectItemNotSet(mockIO);

		service.setReturnIntegrationObjectItems(mockIO, convertedMap);

		final IntegrationObjectItemModel product = getItemInIntegrationObject(mockIO, "Product");
		final IntegrationObjectItemModel classificationClass = getItemInIntegrationObject(mockIO, "ClassificationClass");

		for (final IntegrationObjectItemAttributeModel attributeModel : product.getAttributes())
		{
			if (attributeModel.getAttributeName().equals("ClassificationClass"))
			{
				assertEquals(classificationClass, attributeModel.getReturnIntegrationObjectItem());
			}
		}
	}

	private void assertReturnIntegrationObjectItemNotSet(final IntegrationObjectModel io)
	{
		io.getItems().forEach(ioi -> ioi.getAttributes().forEach(ioia -> assertNull(ioia.getReturnIntegrationObjectItem())));
	}

	static class IODefinitionBuilder
	{
		IntegrationObjectModel integrationObjectModel;
		IntegrationObjectDefinition definition;

		IODefinitionBuilder(final IntegrationObjectModel integrationObject, final IntegrationObjectDefinition definition)
		{
			this.integrationObjectModel = integrationObject;
			this.definition = definition;
		}

		void addIOIWithIOIA(final IntegrationObjectItemModel item,
		                    final List<IntegrationObjectItemAttributeModel> attrList,
		                    final List<AbstractListItemDTO> dtoList)
		{
			final ComposedTypeModel itemComposedType = item.getType();
			final String itemCode = item.getCode();
			final IntegrationMapKeyDTO definitionKey = new IntegrationMapKeyDTO(itemComposedType, itemCode);
			definition.setAttributesByKey(definitionKey, new ArrayList<>(dtoList));
			item.setAttributes(new HashSet<>(attrList));
			addIntegrationObjectItem(item);
		}

		ListItemAttributeDTO generateListItemAttributeDTO(final IntegrationObjectItemAttributeModel attribute)
		{
			final AttributeDescriptorModel attributeDescriptor = attribute.getAttributeDescriptor();
			final AttributeTypeDTOBuilder builder = AttributeTypeDTO.builder(attributeDescriptor);
			if (attributeDescriptor.getAttributeType() instanceof CollectionTypeModel)
			{
				builder.withStructureType(ListItemStructureType.COLLECTION);
			}
			final AttributeTypeDTO dto = builder.build();
			return ListItemAttributeDTO.builder(dto)
			                           .withSelected(true)
			                           .withAttributeName(attribute.getAttributeName())
			                           .build();
		}

		void setTypeAliasForListItemDTO(final IntegrationMapKeyDTO keyDTO, final String attributeName, final String typeAlias)
		{
			final List<AbstractListItemDTO> dtoList = definition.getAttributesByKey(keyDTO);
			for (final AbstractListItemDTO dto : dtoList)
			{
				if (dto.getAlias().equals(attributeName))
				{
					dto.setTypeAlias(typeAlias);
				}
			}
		}

		private void addIntegrationObjectItem(final IntegrationObjectItemModel iOI)
		{
			final Set<IntegrationObjectItemModel> newItemSet = integrationObjectModel.getItems() == null ?
					new HashSet<>() : new HashSet<>(integrationObjectModel.getItems());
			newItemSet.add(iOI);
			integrationObjectModel.setItems(newItemSet);
		}
	}

	private AttributeDescriptorModel attributeDescriptor(final DescriptorType descriptorType, final TypeModel type)
	{
		final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
		if (descriptorType.equals(DescriptorType.COMPOSEDTYPE))
		{
			attributeDescriptorModel.setAttributeType(type);
		}
		else if (descriptorType.equals(DescriptorType.COLLECTIONTYPE))
		{
			final CollectionTypeModel mockCollection = mock(CollectionTypeModel.class);
			lenient().when(mockCollection.getItemtype()).thenReturn("CollectionType");
			lenient().when(mockCollection.getElementType()).thenReturn(type);
			attributeDescriptorModel.setAttributeType(mockCollection);
		}
		else if (descriptorType.equals(DescriptorType.ATOMICTYPE))
		{
			final TypeModel mockType = mock(TypeModel.class);
			lenient().doReturn("AtomicType").when(mockType).getItemtype();
			lenient().doReturn(type.getCode()).when(mockType).getCode();
			attributeDescriptorModel.setAttributeType(mockType);
		}
		return attributeDescriptorModel;
	}

	private static ComposedTypeModel composedType(final String code)
	{
		if (!COMPOSED_TYPE_MODEL_MAP.containsKey(code))
		{
			final ComposedTypeModel composedType = new ComposedTypeModel();
			composedType.setCode(code);
			COMPOSED_TYPE_MODEL_MAP.put(code, composedType);
		}
		return COMPOSED_TYPE_MODEL_MAP.get(code);
	}

	enum DescriptorType
	{
		COMPOSEDTYPE,
		COLLECTIONTYPE,
		ATOMICTYPE
	}

	private IntegrationObjectItemModel integrationObjectItem(final String iOICode, final String composedTypeCode)
	{
		final IntegrationObjectItemModel iOI = new IntegrationObjectItemModel();
		iOI.setCode(iOICode);
		iOI.setType(composedType(composedTypeCode));
		iOI.setClassificationAttributes(Collections.emptySet());
		return iOI;
	}

	private IntegrationObjectItemAttributeModel integrationObjectItemAttribute(final String attributeName,
	                                                                           final AttributeDescriptorModel attributeDescriptor)
	{
		final IntegrationObjectItemAttributeModel attribute = new IntegrationObjectItemAttributeModel();
		attribute.setAttributeName(attributeName);
		attribute.setAttributeDescriptor(attributeDescriptor);
		return attribute;
	}


	private IntegrationObjectDefinition mockDTOMap(final IntegrationObjectModel ioModel)
	{
		//TODO may be good to re-address this method and make it more efficient as part of another subtask
		final IntegrationObjectDefinition mockMap = new IntegrationObjectDefinition();

		ComposedTypeModel mockType1 = null;
		IntegrationMapKeyDTO mapKey1 = null;
		for (final IntegrationObjectItemModel item : ioModel.getItems())
		{
			if (item.getCode().equals("Product"))
			{
				mockType1 = item.getType();
				mapKey1 = new IntegrationMapKeyDTO(mockType1, "Product");
			}
		}

		ComposedTypeModel mockType2 = null;
		IntegrationMapKeyDTO mapKey2 = null;
		for (final IntegrationObjectItemModel item : ioModel.getItems())
		{
			if (item.getCode().equals("CatalogVersion"))
			{
				mockType2 = item.getType();
				mapKey2 = new IntegrationMapKeyDTO(mockType2, "CatalogVersion");
			}
		}

		ComposedTypeModel mockType3 = null;
		IntegrationMapKeyDTO mapKey3 = null;
		for (final IntegrationObjectItemModel item : ioModel.getItems())
		{
			if (item.getCode().equals("Language"))
			{
				mockType3 = item.getType();
				mapKey3 = new IntegrationMapKeyDTO(mockType3, "Language");
			}
		}

		final CollectionTypeModel mockType3b = mock(CollectionTypeModel.class);
		lenient().when(mockType3b.getCode()).thenReturn("Language");

		final TypeModel mockType4 = mock(TypeModel.class);
		lenient().when(mockType4.getItemtype()).thenReturn("AtomicType");

		final AttributeDescriptorModel catalogVersionDescriptor = new AttributeDescriptorModel();
		catalogVersionDescriptor.setAttributeType(mockType2);

		final AttributeDescriptorModel languageDescriptor = new AttributeDescriptorModel();
		languageDescriptor.setAttributeType(mockType3b);
		lenient().when(mockType3b.getElementType()).thenReturn(mockType3);

		final AttributeDescriptorModel codeDescriptor = new AttributeDescriptorModel();
		codeDescriptor.setAttributeType(mockType4);

		// Attribute type dto setup
		final AttributeTypeDTO catalogVersionType = AttributeTypeDTO.builder(catalogVersionDescriptor).build();
		final AttributeTypeDTO atomicType = AttributeTypeDTO.builder(codeDescriptor).build();
		final AttributeTypeDTO languageType = AttributeTypeDTO.builder(languageDescriptor)
		                                                      .withStructureType(ListItemStructureType.COLLECTION)
		                                                      .build();

		// Generic list item
		final ListItemAttributeDTO genericDto1 = ListItemAttributeDTO.builder(atomicType)
		                                                             .withSelected(true)
		                                                             .withAttributeName("code")
		                                                             .build();

		// Product list items
		final ListItemAttributeDTO productDto1 = ListItemAttributeDTO.builder(catalogVersionType)
		                                                             .withSelected(true)
		                                                             .withAttributeName("catalogVersion")
		                                                             .build();
		final List<AbstractListItemDTO> productList = new ArrayList<>();
		productList.add(productDto1);
		productList.add(genericDto1);

		// CatalogVersion list items
		final ListItemAttributeDTO catalogVersionDto1 = ListItemAttributeDTO.builder(languageType)
		                                                                    .withSelected(true)
		                                                                    .withAttributeName("languages")
		                                                                    .build();
		final List<AbstractListItemDTO> catalogVersionList = new ArrayList<>();
		catalogVersionList.add(catalogVersionDto1);
		catalogVersionList.add(genericDto1);

		// Language list items
		final List<AbstractListItemDTO> languageList = new ArrayList<>();
		languageList.add(genericDto1);

		// Compile map
		mockMap.setAttributesByKey(mapKey1, productList);
		mockMap.setAttributesByKey(mapKey2, catalogVersionList);
		mockMap.setAttributesByKey(mapKey3, languageList);

		return mockMap;
	}

	private IntegrationObjectModel addIOIToExistingIO(final IntegrationObjectModel io)
	{
		// Add a subtype IOI to the IO
		final IntegrationObjectItemModel ioItemClassificationClass = spy(IntegrationObjectItemModel.class);
		final ComposedTypeModel classificationClassComposedType = mock(ComposedTypeModel.class);
		final String classificationClassCode = "ClassificationClass";
		ioItemClassificationClass.setCode(classificationClassCode);
		lenient().when(classificationClassComposedType.getItemtype()).thenReturn(classificationClassCode);
		lenient().doReturn(classificationClassComposedType).when(ioItemClassificationClass).getType();

		// 'ClassificationClass' integration object item attribute (contained by 'Product')
		final TypeModel mockType = mock(TypeModel.class);
		lenient().when(mockType.getItemtype()).thenReturn("ComposedType");
		lenient().when(mockType.getCode()).thenReturn("ClassificationClass");

		final AttributeDescriptorModel classificationClassDescriptor = new AttributeDescriptorModel();
		classificationClassDescriptor.setAttributeType(mockType);

		final IntegrationObjectItemAttributeModel ioiAttributeCategory = new IntegrationObjectItemAttributeModel();
		ioiAttributeCategory.setAttributeDescriptor(classificationClassDescriptor);
		ioiAttributeCategory.setAttributeName("supercategories");

		// 'hmcXML' integration object item attribute (contained by 'ClassificationClass')
		final TypeModel mockType2 = mock(TypeModel.class);
		lenient().when(mockType2.getItemtype()).thenReturn("AtomicType");

		final AttributeDescriptorModel hmcXMLDescriptor = new AttributeDescriptorModel();
		hmcXMLDescriptor.setAttributeType(mockType2);

		final IntegrationObjectItemAttributeModel ioiAttributeCode = new IntegrationObjectItemAttributeModel();
		ioiAttributeCode.setAttributeDescriptor(hmcXMLDescriptor);
		ioiAttributeCode.setAttributeName("hmcXML");

		// Relationships
		final Set<IntegrationObjectItemAttributeModel> classificationClassAttributes = new HashSet<>();
		classificationClassAttributes.add(ioiAttributeCode);
		ioItemClassificationClass.setAttributes(classificationClassAttributes);
		ioItemClassificationClass.setClassificationAttributes(Collections.emptySet());

		Set<IntegrationObjectItemAttributeModel> productAttributes;
		for (final IntegrationObjectItemModel item : io.getItems())
		{
			if (item.getCode().equals("Product"))
			{
				productAttributes = item.getAttributes();
				productAttributes.add(ioiAttributeCategory);
				item.setAttributes(productAttributes);
			}
		}

		// Update IO
		final Set<IntegrationObjectItemModel> items = io.getItems();
		items.add(ioItemClassificationClass);
		io.setItems(items);
		return io;
	}

	private IntegrationObjectDefinition addMapEntry(final IntegrationObjectModel ioModel,
	                                                final IntegrationObjectDefinition dtoMap)
	{
		//TODO look into refactoring this for efficiency as part of another subtask
		ComposedTypeModel mockType = null;
		IntegrationMapKeyDTO mapKey = null;
		for (final IntegrationObjectItemModel item : ioModel.getItems())
		{
			if (item.getCode().equals("ClassificationClass"))
			{
				mockType = item.getType();
				mapKey = new IntegrationMapKeyDTO(mockType, "ClassificationClass");
			}
		}

		final CollectionTypeModel mockTypeB = mock(CollectionTypeModel.class);
		lenient().when(mockTypeB.getCode()).thenReturn("ClassificationClass");

		final AttributeDescriptorModel classificationClassDescriptor = new AttributeDescriptorModel();
		classificationClassDescriptor.setAttributeType(mockTypeB);
		lenient().when(mockTypeB.getElementType()).thenReturn(mockType);

		final TypeModel mockType2 = mock(TypeModel.class);
		lenient().when(mockType2.getItemtype()).thenReturn("AtomicType");

		final AttributeDescriptorModel hmcXMLDescriptor = new AttributeDescriptorModel();
		hmcXMLDescriptor.setAttributeType(mockType2);

		// Product list items
		for (final IntegrationMapKeyDTO typeKey : dtoMap.getDefinitionMap().keySet())
		{
			if (typeKey.getCode().equals("Product"))
			{
				final List<AbstractListItemDTO> productList = dtoMap.getAttributesByKey(typeKey);
				final AttributeTypeDTO productType = AttributeTypeDTO.builder(classificationClassDescriptor)
				                                                     .withStructureType(ListItemStructureType.COLLECTION)
				                                                     .build();
				final ListItemAttributeDTO productDto1 = ListItemAttributeDTO.builder(productType)
				                                                             .withSelected(true)
				                                                             .withAttributeName("supercategories")
				                                                             .build();
				productList.add(productDto1);
				dtoMap.setAttributesByKey(typeKey, productList);
			}
		}

		// ClassificationClass list items
		final AttributeTypeDTO classificationType = AttributeTypeDTO.builder(hmcXMLDescriptor).build();
		final ListItemAttributeDTO classificationClassDto1 = ListItemAttributeDTO.builder(classificationType)
		                                                                         .withSelected(true)
		                                                                         .withAttributeName("hmcXML")
		                                                                         .build();
		final List<AbstractListItemDTO> classificationClassList = new ArrayList<>();
		classificationClassList.add(classificationClassDto1);

		dtoMap.setAttributesByKey(mapKey, classificationClassList);

		return dtoMap;
	}
}
