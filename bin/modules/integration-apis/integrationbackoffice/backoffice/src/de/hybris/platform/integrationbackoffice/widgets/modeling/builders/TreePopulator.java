/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.builders;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

import java.util.Deque;
import java.util.Set;

import org.zkoss.zul.Treeitem;

/**
 * Populates a tree from a base {@link Treeitem}. It aims to create a tree under some {@link Treeitem} with {@link TreeNodeData} or
 * adding new tree node to an existing tree.
 */
public interface TreePopulator
{
	/**
	 * Find all ancestors for a given tree item. The ancestors will be ordered from the root tree item to the given tree item.
	 *
	 * @param treeitem the tree item for which its ancestors will be determined.
	 * @return a deque of the tree item's ancestors.
	 */
	Deque<IntegrationMapKeyDTO> determineTreeitemAncestors(Treeitem treeitem);

	/**
	 * Creates a {@link Treeitem} with a given {@link TreeNodeData} and adds it as a child of a given parent {@link Treeitem}.
	 *
	 * @param parent the tree node under which a new tree node is adding.
	 * @param tnd    contains the data needed to create a new tree node.
	 * @return the newly created tree node with tnd.
	 */
	Treeitem appendTreeitem(Treeitem parent, TreeNodeData tnd);

	/**
	 * Populates a tree iteratively.
	 *
	 * @param parent               new tree node will be created and added to this object and works as parent for iteration.
	 * @param existingDefinitions  a map that contains IntegrationMapKeyDTOs and lists of ListItemDTOs which are selected.
	 *                             Normally it is converted by DataStructureBuilder with the selected IO that reflected
	 *                             its IntegrationObjectItem as key and ListItemDTO from IntegrationObjectItemAttribute as value.
	 * @param currentAttributesMap a map that contains all the IntegrationMapKeyDTOs that related to the IntegrationObject
	 *                             and its list of ListItemDTO.
	 * @param subtypeDataSet       a set that helps to determine the real type of a TreeNode. Could be a subtype of the original type.
	 */
	IntegrationObjectDefinition populateTree(Treeitem parent,
	                                         IntegrationObjectDefinition existingDefinitions,
	                                         IntegrationObjectDefinition currentAttributesMap,
	                                         Set<SubtypeData> subtypeDataSet);

	/**
	 * Populates a tree iteratively, but only with selected {@link AbstractListItemDTO}.
	 *
	 * @param parent              tree item on which new child tree items will be appended to in the current iteration.
	 * @param existingDefinitions a map that contains IntegrationMapKeyDTOs and lists of ListItemDTOs which are selected.
	 * @param subtypeDataSet      a set that helps to determine the real type of a TreeNode. Could be a subtype of the original type.
	 */
	void populateTreeSelectedMode(Treeitem parent,
	                              IntegrationObjectDefinition existingDefinitions,
	                              Set<SubtypeData> subtypeDataSet);

	/**
	 * Reset class state by clearing the deque of ancestors.
	 */
	void resetState();

	/**
	 * Set the ancestors. For any tree node, the populator will not create child tree nodes that is an ancestor.
	 *
	 * @param ancestors the deque of ancestors.
	 */
	void setAncestors(Deque<IntegrationMapKeyDTO> ancestors);

	/**
	 * Add a key at the end of the ancestors deque.
	 *
	 * @param integrationMapKeyDTO the key to be added to the ancestors deque.
	 */
	void addAncestor(IntegrationMapKeyDTO integrationMapKeyDTO);
}
