/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultIntegrationObjectDefinitionConverterUnitTest
{
	private static final String COLLECTION_TYPE = "CollectionItem";
	private static final String MAP_TYPE = "MapItem";

	@Mock
	private ReadService readService;

	private DefaultIntegrationObjectDefinitionConverter converter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		doReturn(true).when(readService).isCollectionType(COLLECTION_TYPE);
		doReturn(true).when(readService).isMapType(MAP_TYPE);
		converter = new DefaultIntegrationObjectDefinitionConverter(readService);
	}

	@Test
	public void integrationObjectDefinitionHasMultipleKey()
	{
		final IntegrationObjectModel object = integrationObject(
				item("MapTypeExample",
						attribute(descriptor(MAP_TYPE, false), false, false)),
				item("TypeSystemUnique",
						attribute(descriptor("String", true), false, false),
						attribute(descriptor("ReferencedItem", false), false, true),
						attribute(descriptor(COLLECTION_TYPE, false), false, false)),
				item("CustomUnique",
						attribute(descriptor("String", false), true, false),
						attribute(descriptor("Integer", null), true, false)),
				item("DoubleUnique",
						attribute(descriptor("String", true), true, false)),
				item("NonUnique",
						attribute(descriptor("Integer", null), null, null)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();

		assertThat(dtoMap.keySet())
				.extracting("code")
				.containsExactlyInAnyOrder("MapTypeExample", "TypeSystemUnique", "CustomUnique", "DoubleUnique", "NonUnique");
	}

	@Test
	public void mapTypeIsConverted()
	{
		final IntegrationObjectModel object = integrationObject(
				item("MapTypeExample",
						attribute(descriptor(MAP_TYPE, false), false, false)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();
		assertEquals(ListItemStructureType.MAP,
				((ListItemAttributeDTO) extractDtoForItemType(dtoMap, "MapTypeExample").get(0)).getStructureType());
		assertThat(extractDtoForItemType(dtoMap, "MapTypeExample"))
				.extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected",
						"autocreate")
				.contains(tuple(MAP_TYPE, false, true, false));
	}

	@Test
	public void collectionTypeIsConverted()
	{
		final IntegrationObjectModel object = integrationObject(
				item("CollectionTypeExample",
						attribute(descriptor(COLLECTION_TYPE, false), false, false)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();
		assertEquals(ListItemStructureType.COLLECTION,
				((ListItemAttributeDTO) extractDtoForItemType(dtoMap, "CollectionTypeExample").get(0)).getStructureType());
		assertThat(extractDtoForItemType(dtoMap, "CollectionTypeExample"))
				.extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected",
						"autocreate")
				.contains(tuple(COLLECTION_TYPE, false, true, false));
	}

	@Test
	public void nonStructuredTypeIsConverted()
	{
		final IntegrationObjectModel object = integrationObject(
				item("CollectionTypeExample",
						attribute(descriptor("mockType", false), false, false)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();
		assertEquals(ListItemStructureType.NONE,
				((ListItemAttributeDTO) extractDtoForItemType(dtoMap, "CollectionTypeExample").get(0)).getStructureType());
		assertThat(extractDtoForItemType(dtoMap, "CollectionTypeExample"))
				.extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected",
						"autocreate")
				.contains(tuple("mockType", false, true, false));
	}

	@Test
	public void iOIWithMultipleIOIAIsConverted()
	{
		final IntegrationObjectModel object = integrationObject(
				item("TypeSystemUnique",
						attribute(descriptor("String", true), false, false),
						attribute(descriptor("ReferencedItem", false), false, true),
						attribute(descriptor(COLLECTION_TYPE, null), false, false),
						attribute(descriptor("IOIAIsUnique", false), true, false)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();
		extractDtoForItemType(dtoMap, "TypeSystemUnique")
				.stream()
				.filter(ListItemAttributeDTO.class::isInstance)
				.map(ListItemAttributeDTO.class::cast)
				.forEach(item -> {
					final String type = item.getAttributeDescriptor()
					                        .getAttributeType()
					                        .getItemtype();
					if ("String".equals(type) || "ReferencedItem".equals(type) || "IOIAIsUnique".equals(type))
					{
						assertEquals(ListItemStructureType.NONE,
								item.getStructureType());
					}
					else
					{
						assertEquals(ListItemStructureType.COLLECTION,
								item.getStructureType());
					}
				});

		assertThat(extractDtoForItemType(dtoMap, "TypeSystemUnique"))
				.extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected",
						"autocreate")
				.containsExactlyInAnyOrder(
						tuple("String", false, true, false),
						tuple("ReferencedItem", false, true, true),
						tuple(COLLECTION_TYPE, false, true, false),
						tuple("IOIAIsUnique", true, true, false));
	}

	@Test
	public void iOIAIsConvertedWithDefaultSettings()
	{
		final IntegrationObjectModel object = integrationObject(
				item("NonUnique",
						attribute(descriptor("Integer", null), null, null)));
		final IntegrationObjectDefinition integrationObjectDefinition = converter.toDefinitionMap(object);
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> dtoMap = integrationObjectDefinition.getDefinitionMap();
		assertEquals(ListItemStructureType.NONE,
				((ListItemAttributeDTO) extractDtoForItemType(dtoMap, "NonUnique").get(0)).getStructureType());
		assertThat(extractDtoForItemType(dtoMap, "NonUnique"))
				.extracting("attributeDescriptor.attributeType.itemtype", "customUnique", "selected",
						"autocreate")
				.contains(tuple("Integer", false, true, false));
	}

	private IntegrationObjectModel integrationObject(final IntegrationObjectItemModel... items)
	{
		final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
		doReturn(asSet(items)).when(object).getItems();
		return object;
	}

	private IntegrationObjectItemModel item(final String code, final IntegrationObjectItemAttributeModel... attributes)
	{
		final IntegrationObjectItemModel item = mock(IntegrationObjectItemModel.class);
		doReturn(composedTypeModel(code)).when(item).getType();
		doReturn(asSet(attributes)).when(item).getAttributes();
		doReturn(code).when(item).getCode();
		return item;
	}

	private ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel model = mock(ComposedTypeModel.class);
		doReturn(code).when(model).getCode();
		return model;
	}

	private IntegrationObjectItemAttributeModel attribute(final AttributeDescriptorModel descriptor, final Boolean unique,
	                                                      final Boolean create)
	{
		final IntegrationObjectItemAttributeModel attribute = mock(IntegrationObjectItemAttributeModel.class);
		doReturn(descriptor).when(attribute).getAttributeDescriptor();
		doReturn(unique).when(attribute).getUnique();
		doReturn(create).when(attribute).getAutoCreate();
		return attribute;
	}

	private AttributeDescriptorModel descriptor(final String type, final Boolean unique)
	{
		final TypeModel typeModel;
		if (COLLECTION_TYPE.equals(type))
		{
			typeModel = collectionType(type);
		}
		else if (MAP_TYPE.equals(type))
		{
			typeModel = mapType(type);
		}
		else
		{
			typeModel = nonStructuredType(type);
		}
		final AttributeDescriptorModel descriptor = mock(AttributeDescriptorModel.class);
		doReturn(typeModel).when(descriptor).getAttributeType();
		doReturn(unique).when(descriptor).getUnique();
		return descriptor;
	}

	private CollectionTypeModel collectionType(final String type)
	{
		final CollectionTypeModel model = mock(CollectionTypeModel.class);
		doReturn(type).when(model).getItemtype();
		doReturn(typeModel(type)).when(model).getElementType();
		return model;
	}

	private MapTypeModel mapType(final String type)
	{
		final MapTypeModel model = mock(MapTypeModel.class);
		doReturn(type).when(model).getItemtype();
		doReturn(typeModel(type)).when(model).getReturntype();
		return model;
	}

	private ComposedTypeModel nonStructuredType(final String type)
	{
		//not necessary to be ComposedTypeModel. Just use it for easy testing.
		final ComposedTypeModel model = mock(ComposedTypeModel.class);
		doReturn(type).when(model).getItemtype();
		return model;
	}

	private TypeModel typeModel(final String type)
	{
		final TypeModel model = mock(TypeModel.class);
		doReturn(type).when(model).getCode();
		doReturn(type).when(model).getItemtype();
		return model;
	}

	private List<AbstractListItemDTO> extractDtoForItemType(final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> map,
	                                                        final String type)
	{
		return map.entrySet().stream()
		          .filter(entry -> entry.getKey().getType().getCode().equals(type))
		          .findAny()
		          .map(Map.Entry::getValue)
		          .orElse(Collections.emptyList());
	}

	@SafeVarargs
	private <T> Set<T> asSet(final T... items)
	{
		return new HashSet<>(Arrays.asList(items));
	}
}
