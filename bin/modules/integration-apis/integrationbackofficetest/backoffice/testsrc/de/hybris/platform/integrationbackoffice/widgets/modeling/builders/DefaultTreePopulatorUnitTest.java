/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.builders;

import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.composedTypeModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.TreeNodeDataSetGenerator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorAttributesFilteringService;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.zul.Treeitem;

@UnitTest
public class DefaultTreePopulatorUnitTest
{
	private DefaultTreePopulator treePopulator;
	private TreeNodeDataSetGenerator treeNodeDataSetGenerator;
	private EditorAttributesFilteringService attrFilterService;

	private static final String ROOT_TYPE_CODE = "Product";
	private static final ComposedTypeModel ROOT_TYPE = composedTypeModel(ROOT_TYPE_CODE);
	private static final IntegrationMapKeyDTO ROOT = new IntegrationMapKeyDTO(ROOT_TYPE, ROOT_TYPE.getCode());
	private static final TreeNodeData ROOT_TND = TreeNodeData.createRootTreeNodeData(ROOT);
	private static final Treeitem ROOT_TREEITEM = treeitem(0, ROOT_TND);

	private static final String INSTANCE_TYPE_CODE = "CatalogVersion";
	private static final ComposedTypeModel INSTANCE_TYPE = composedTypeModel(INSTANCE_TYPE_CODE);
	private static final String INSTANCE_CODE = "CatalogVersion1";
	private static final IntegrationMapKeyDTO INSTANCE = new IntegrationMapKeyDTO(INSTANCE_TYPE, INSTANCE_CODE);
	private static final TreeNodeData INSTANCE_TND = new TreeNodeData("catalogVersion", "catalogVersion1", INSTANCE);
	private static final Treeitem INSTANCE_TREEITEM = treeitem(1, INSTANCE_TND);

	private static final String INSTANCE2_CODE = "CatalogVersion2";
	private static final IntegrationMapKeyDTO INSTANCE2 = new IntegrationMapKeyDTO(INSTANCE_TYPE, INSTANCE2_CODE);
	private static final TreeNodeData INSTANCE2_TND = new TreeNodeData("catalogVersion", "catalogVersion2", INSTANCE2);

	private static final Set<SubtypeData> SUBTYPE_DATA_SET = Collections.emptySet();

	@Before
	public void setUp()
	{
		attrFilterService = mock(EditorAttributesFilteringService.class);
		final DataStructureBuilder dataStructureBuilder = mock(DataStructureBuilder.class);
		treeNodeDataSetGenerator = mock(TreeNodeDataSetGenerator.class);
		treePopulator = new DefaultTreePopulator(attrFilterService, dataStructureBuilder, treeNodeDataSetGenerator);
	}

	@Test
	public void oneAncestorWhenDetermineTreeitemAncestorsAndTreeitemIsRoot()
	{
		final Deque<IntegrationMapKeyDTO> ancestors = treePopulator.determineTreeitemAncestors(ROOT_TREEITEM);
		assertEquals("Ancestors contains root instance.", ROOT, ancestors.peek());
		assertEquals("Ancestors contains only root instance.", 1, ancestors.size());
	}

	@Test
	public void ancestorsOrderedProperlyWhenDetermineTreeitemAncestorsAndTreeitemIsNotRoot()
	{
		when(INSTANCE_TREEITEM.getParentItem()).thenReturn(ROOT_TREEITEM);

		final Deque<IntegrationMapKeyDTO> ancestors = treePopulator.determineTreeitemAncestors(INSTANCE_TREEITEM);

		assertEquals("Ancestors has correct size.", 2, ancestors.size());
		assertEquals("Root instance is first.", ROOT, ancestors.getFirst());
		assertEquals("Tree item instance is last.", INSTANCE, ancestors.getLast());
	}

	@Test
	public void treeitemStillAddedWhenAppendTreeitemAndParentHasNoTreechildren()
	{
		final IntegrationMapKeyDTO keyDTO = new IntegrationMapKeyDTO(mock(ComposedTypeModel.class), "");
		final TreeNodeData treeNodeData = new TreeNodeData("", "", keyDTO);
		final Treeitem parentTreeitem = new Treeitem();

		final Treeitem treeitem = treePopulator.appendTreeitem(parentTreeitem, treeNodeData);

		assertNotNull("Treechildren for parent created", parentTreeitem.getTreechildren());
		assertEquals("New tree item has correct value.", treeNodeData, treeitem.getValue());
	}

	@Test
	public void copySetWhenSetAncestorsArgumentIsNotEmpty()
	{
		final Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();
		ancestors.add(INSTANCE);
		treePopulator.setAncestors(ancestors);
		assertNotSame("Ancestors is not the same.", treePopulator.ancestors, ancestors);
	}

	@Test
	public void ancestorsClearedWhenSetAncestorsArgumentIsEmptyOrNull()
	{
		final Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();
		ancestors.add(INSTANCE);

		treePopulator.ancestors = ancestors;
		treePopulator.setAncestors(null);
		assertTrue("Ancestors cleared.", treePopulator.ancestors.isEmpty());

		treePopulator.ancestors = ancestors;
		treePopulator.setAncestors(new ArrayDeque<>());
		assertTrue("Ancestors cleared.", treePopulator.ancestors.isEmpty());
	}

	@Test
	public void ancestorsClearedWhenResetState()
	{
		final Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();
		ancestors.add(INSTANCE);
		treePopulator.ancestors = ancestors;
		treePopulator.resetState();
		assertTrue("Ancestors cleared.", treePopulator.ancestors.isEmpty());
	}

	@Test
	public void instanceAddedWhenAddAncestor()
	{
		treePopulator.addAncestor(INSTANCE);
		assertEquals("Instance added.", INSTANCE, treePopulator.ancestors.getFirst());
	}

	@Test
	public void sortedTreeNodesCreatedWhenPopulateTreeAndTypeNotInAncestors()
	{
		// setup
		final Treeitem parentTreeitem = new Treeitem("", ROOT_TND);
		final IntegrationObjectDefinition existingDefinitions = mock(IntegrationObjectDefinition.class);
		final IntegrationObjectDefinition currentAttributesMap = mock(IntegrationObjectDefinition.class);

		final Set<AttributeDescriptorModel> attributes = Collections.emptySet();
		when(attrFilterService.filterAttributesForTree(ROOT_TYPE)).thenReturn(attributes);

		final List<AbstractListItemDTO> existingAttributes = Collections.emptyList();
		when(existingDefinitions.getAttributesByKey(ROOT)).thenReturn(existingAttributes);

		final Set<TreeNodeData> treeNodeDataSet = Set.of(INSTANCE2_TND, INSTANCE_TND);
		when(treeNodeDataSetGenerator.generate(attributes, existingAttributes, SUBTYPE_DATA_SET, ROOT)).thenReturn(
				treeNodeDataSet);

		// test
		treePopulator.populateTree(parentTreeitem, existingDefinitions, currentAttributesMap, SUBTYPE_DATA_SET);

		final Collection<Treeitem> items = parentTreeitem.getTreechildren().getItems();
		final Iterator<Treeitem> iterator = items.iterator();
		final Treeitem firstItem = iterator.next();
		final Treeitem secondItem = iterator.next();
		assertEquals("First tree item is catalogVersion1.", INSTANCE_TND, firstItem.getValue());
		assertEquals("Second tree item is catalogVersion2.", INSTANCE2_TND, secondItem.getValue());
	}

	@Test
	public void noTreeNodesCreatedWhenPopulateTreeAndTypeIsInAncestors()
	{
		// setup
		final Treeitem parentTreeitem = new Treeitem("", ROOT_TND);
		final IntegrationObjectDefinition existingDefinitions = mock(IntegrationObjectDefinition.class);
		final IntegrationObjectDefinition currentAttributesMap = mock(IntegrationObjectDefinition.class);

		final Set<AttributeDescriptorModel> attributes = Collections.emptySet();
		when(attrFilterService.filterAttributesForTree(ROOT_TYPE)).thenReturn(attributes);

		final List<AbstractListItemDTO> existingAttributes = Collections.emptyList();
		when(existingDefinitions.getAttributesByKey(ROOT)).thenReturn(existingAttributes);

		final Set<TreeNodeData> treeNodeDataSet = Set.of(INSTANCE_TND);
		when(treeNodeDataSetGenerator.generate(attributes, existingAttributes, SUBTYPE_DATA_SET, ROOT)).thenReturn(
				treeNodeDataSet);

		final Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();
		ancestors.add(INSTANCE);
		treePopulator.setAncestors(ancestors);

		// test
		treePopulator.populateTree(parentTreeitem, existingDefinitions, currentAttributesMap, SUBTYPE_DATA_SET);
		assertNull("No tree items created.", parentTreeitem.getTreechildren());
	}

	@Test
	public void sortedTreeNodesCreatedWhenPopulateTreeSelectedModeAndTypeNotInAncestors()
	{
		// setup
		final Treeitem parentTreeitem = new Treeitem("", ROOT_TND);
		final IntegrationObjectDefinition existingDefinitions = mock(IntegrationObjectDefinition.class);

		final List<AbstractListItemDTO> existingAttributes = Collections.emptyList();
		when(existingDefinitions.getAttributesByKey(ROOT)).thenReturn(existingAttributes);

		final Set<TreeNodeData> treeNodeDataSet = Set.of(INSTANCE2_TND, INSTANCE_TND);
		when(treeNodeDataSetGenerator.generateInSelectedMode(existingAttributes, SUBTYPE_DATA_SET, ROOT)).thenReturn(
				treeNodeDataSet);

		// test
		treePopulator.populateTreeSelectedMode(parentTreeitem, existingDefinitions, SUBTYPE_DATA_SET);

		final Collection<Treeitem> items = parentTreeitem.getTreechildren().getItems();
		final Iterator<Treeitem> iterator = items.iterator();
		final Treeitem firstItem = iterator.next();
		final Treeitem secondItem = iterator.next();
		assertEquals("First tree item is catalogVersion1.", INSTANCE_TND, firstItem.getValue());
		assertEquals("Second tree item is catalogVersion2.", INSTANCE2_TND, secondItem.getValue());
	}

	@Test
	public void noTreeNodesCreatedWhenPopulateTreeSelectedModeAndTypeNotInAncestors()
	{
		// setup
		final Treeitem parentTreeitem = new Treeitem("", ROOT_TND);
		final IntegrationObjectDefinition existingDefinitions = mock(IntegrationObjectDefinition.class);

		final List<AbstractListItemDTO> existingAttributes = Collections.emptyList();
		when(existingDefinitions.getAttributesByKey(ROOT)).thenReturn(existingAttributes);

		final Set<TreeNodeData> treeNodeDataSet = Set.of(INSTANCE_TND);
		when(treeNodeDataSetGenerator.generateInSelectedMode(existingAttributes, SUBTYPE_DATA_SET, ROOT)).thenReturn(
				treeNodeDataSet);

		final Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();
		ancestors.add(INSTANCE);
		treePopulator.setAncestors(ancestors);

		// test
		treePopulator.populateTreeSelectedMode(parentTreeitem, existingDefinitions, SUBTYPE_DATA_SET);
		assertNull("No tree items created.", parentTreeitem.getTreechildren());
	}

	private static Treeitem treeitem(final int level, final TreeNodeData treeNodeData)
	{
		final Treeitem treeitem = mock(Treeitem.class);
		when(treeitem.getLevel()).thenReturn(level);
		when(treeitem.getValue()).thenReturn(treeNodeData);
		return treeitem;
	}
}
