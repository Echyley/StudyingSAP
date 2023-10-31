/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationObjectDefinitionTrimmerUnitTest
{
	@Mock
	private ReadService readService;
	@Mock
	private Tree tree;
	@Mock
	private Treeitem rootTreeItem;
	@Mock
	private Treechildren treechildren;
	@Mock
	private Treeitem productTreeItem;
	@Mock
	private Treeitem warehouseTreeItem;

	private ComposedTypeModel rootType;
	private ComposedTypeModel product;
	private ComposedTypeModel warehouse;
	private IntegrationMapKeyDTO rootTypeKeyDTO;
	private IntegrationMapKeyDTO productKeyDTO;
	private IntegrationMapKeyDTO warehouseKeyDTO;
	private TreeNodeData rootNodeData;
	private TreeNodeData warehouseNodeData;
	private TreeNodeData productNodeData;
	private DefaultIntegrationObjectDefinitionTrimmer trimmer;

	@Before
	public void setUp()
	{
		rootType = new ComposedTypeModel();
		rootTypeKeyDTO = new IntegrationMapKeyDTO(new ComposedTypeModel(), "rootType");
		rootNodeData = new TreeNodeData(null, null, rootTypeKeyDTO);

		product = new ComposedTypeModel();
		product.setCode("Product");
		productKeyDTO = new IntegrationMapKeyDTO(product, "product");
		productNodeData = new TreeNodeData("product", null, this.productKeyDTO);

		warehouse = new ComposedTypeModel();
		warehouse.setCode("Warehouse");
		warehouseKeyDTO = new IntegrationMapKeyDTO(warehouse, "warehouse");
		warehouseNodeData = new TreeNodeData("warehouse", null, this.warehouseKeyDTO);

		trimmer = new DefaultIntegrationObjectDefinitionTrimmer(readService);
	}

	@Test
	public void testTrimMap()
	{
		final IntegrationObjectDefinition attributesMap = prepareMockMap();

		assertEquals(3, attributesMap.getAttributesByKey(rootTypeKeyDTO).size());
		assertEquals(1, attributesMap.getAttributesByKey(warehouseKeyDTO).size());
		assertEquals(0, attributesMap.getAttributesByKey(productKeyDTO).size());

		// Stock level
		//      -> product (not selected, trimmed)
		//      -> products (not selected, trimmed)
		//      -> warehouse (selected)
		//          -> name (selected)

		lenient().when(tree.getItems()).thenReturn(Collections.singletonList(rootTreeItem));
		lenient().when(rootTreeItem.getValue()).thenReturn(rootNodeData);
		lenient().when(rootTreeItem.getTreechildren()).thenReturn(treechildren);
		lenient().when(warehouseTreeItem.getValue()).thenReturn(warehouseNodeData);

		final List<Component> children = new ArrayList<>();
		children.add(productTreeItem);
		children.add(warehouseTreeItem);

		lenient().when(treechildren.getChildren()).thenReturn(children);
		lenient().when(productTreeItem.getValue()).thenReturn(productNodeData);
		lenient().when(productTreeItem.getLabel()).thenReturn("product [Product]");
		lenient().when(warehouseTreeItem.getValue()).thenReturn(warehouseNodeData);
		lenient().when(warehouseTreeItem.getLabel()).thenReturn("warehouse [Warehouse]");

		lenient().when(readService.isComplexType(warehouse)).thenReturn(true);

		final IntegrationObjectDefinition trimmedMap = trimmer.trimMap(tree, attributesMap);

		assertNotNull(trimmedMap);
		assertEquals(1, trimmedMap.getAttributesByKey(rootTypeKeyDTO).size());
		assertEquals(1, trimmedMap.getAttributesByKey(warehouseKeyDTO).size());
		assertTrue(trimmedMap.getAttributesByKey(productKeyDTO).isEmpty());

		final List<AbstractListItemDTO> stockLevelAttributes = trimmedMap.getAttributesByKey(rootTypeKeyDTO);
		final List<AbstractListItemDTO> warehouseAttributes = trimmedMap.getAttributesByKey(warehouseKeyDTO);
		assertTrue(stockLevelAttributes.get(0).isSelected());
		assertEquals(warehouse, ((ListItemAttributeDTO) stockLevelAttributes.get(0)).getType());
		assertEquals("warehouse",
				((ListItemAttributeDTO) stockLevelAttributes.get(0)).getAttributeDescriptor().getQualifier());
		assertTrue(warehouseAttributes.get(0).isSelected());
		assertEquals("java.lang.String", ((ListItemAttributeDTO) warehouseAttributes.get(0)).getType().getCode());
		assertEquals("name", ((ListItemAttributeDTO) warehouseAttributes.get(0)).getAttributeDescriptor().getQualifier());
	}


	private IntegrationObjectDefinition prepareMockMap()
	{
		final IntegrationObjectDefinition attributesMap = new IntegrationObjectDefinition();

		// StockLevel attributes
		final List<AbstractListItemDTO> stockLevelAttributes = new ArrayList<>();

		final AttributeDescriptorModel productAttribute = new AttributeDescriptorModel();
		productAttribute.setAttributeType(rootType);
		productAttribute.setQualifier("product");

		final AttributeDescriptorModel productsAttribute = new AttributeDescriptorModel();
		final CollectionTypeModel collectionTypeModel = new CollectionTypeModel();
		collectionTypeModel.setElementType(product);
		productsAttribute.setAttributeType(collectionTypeModel);
		productsAttribute.setQualifier("products");

		final AttributeDescriptorModel warehouseAttribute = new AttributeDescriptorModel();
		warehouseAttribute.setAttributeType(warehouse);
		warehouseAttribute.setQualifier("warehouse");

		final AttributeTypeDTO typeDTO1 = AttributeTypeDTO.builder(productAttribute).withStructureType(ListItemStructureType.NONE)
		                                                  .build();
		stockLevelAttributes.add(ListItemAttributeDTO.builder(typeDTO1).withSelected(false).withCustomUnique(false)
		                                             .withAutocreate(false).withAttributeName("").build());
		final AttributeTypeDTO typeDTO2 = AttributeTypeDTO.builder(productsAttribute)
		                                                  .withStructureType(ListItemStructureType.COLLECTION).build();
		stockLevelAttributes.add(ListItemAttributeDTO.builder(typeDTO2).withSelected(false).withCustomUnique(false)
		                                             .withAutocreate(false).withAttributeName("").build());
		final AttributeTypeDTO typeDTO3 = AttributeTypeDTO.builder(warehouseAttribute)
		                                                  .withStructureType(ListItemStructureType.NONE)
		                                                  .build();
		stockLevelAttributes.add(ListItemAttributeDTO.builder(typeDTO3).withSelected(true).withCustomUnique(true)
		                                             .withAutocreate(false).withAttributeName("").build());

		// Warehouse attributes
		final List<AbstractListItemDTO> warehouseAttributes = new ArrayList<>();

		final AttributeDescriptorModel nameAttribute = new AttributeDescriptorModel();
		final AtomicTypeModel atomicTypeModel = new AtomicTypeModel();
		atomicTypeModel.setCode("java.lang.String");
		nameAttribute.setAttributeType(atomicTypeModel);
		nameAttribute.setQualifier("name");

		final AttributeTypeDTO typeDTO4 = AttributeTypeDTO.builder(nameAttribute).withStructureType(ListItemStructureType.NONE)
		                                                  .build();
		warehouseAttributes.add(ListItemAttributeDTO.builder(typeDTO4).withSelected(true).withCustomUnique(true)
		                                            .withAutocreate(false).withAttributeName("").build());

		attributesMap.setAttributesByKey(rootTypeKeyDTO, stockLevelAttributes);
		attributesMap.setAttributesByKey(productKeyDTO, Collections.emptyList());
		attributesMap.setAttributesByKey(warehouseKeyDTO, warehouseAttributes);
		return attributesMap;
	}
}
