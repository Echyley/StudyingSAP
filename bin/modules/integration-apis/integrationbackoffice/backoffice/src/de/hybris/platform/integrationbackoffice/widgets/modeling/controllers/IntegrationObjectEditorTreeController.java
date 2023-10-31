/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData.createRootTreeNodeData;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_LIST_BOX;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_TREE;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.SELECTED_ITEM_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.createTreeItem;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.findInTreechildren;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.renameTreeitem;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorValidator.validateHasKey;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.TreePopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.CreateTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinitionDuplicationMap;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RenameTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RetypeTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemDTOMissingDescriptorModelException;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionConverter;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionTrimmer;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;


public class IntegrationObjectEditorTreeController extends DefaultWidgetController
{
	private static final Logger LOG = Log.getLogger(IntegrationObjectEditorTreeController.class);
	public static final String LOAD_IO_IN_SOCKET = "loadIO";
	public static final String RETYPE_NODES_IN_SOCKET = "retypeTreeNodes";
	public static final String CREATE_TREE_IN_SOCKET = "createTree";
	public static final String CHECK_FOR_STRUCT_IN_SOCKET = "checkForStructuredType";
	public static final String RENAME_NODES_IN_SOCKET = "renameTreeNodes";
	public static final String CREATE_DYNAMIC_NODE_IN_SOCKET = "createDynamicTreeNode";
	public static final String AUTO_SELECT_RELATION_IN_SOCKET = "autoSelectAttributeRelation";
	public static final String VIEW_EVENT_COMPONENT_ID = "composedTypeTree";

	@WireVariable
	protected transient TreePopulator treePopulator;
	@WireVariable
	protected transient IntegrationObjectPresentationPopulator integrationObjectPresentationPopulator;
	protected transient IntegrationObjectPresentation iOPresentation;
	@WireVariable
	protected transient IntegrationObjectDefinitionConverter integrationObjectDefinitionConverter;
	@WireVariable
	protected transient ReadService readService;
	@WireVariable
	protected transient NotificationService notificationService;
	@WireVariable
	protected transient IntegrationObjectDefinitionTrimmer integrationObjectDefinitionTrimmer;
	@WireVariable
	protected transient DataStructureBuilder dataStructureBuilder;
	@WireVariable
	protected transient IntegrationBackofficeEventSender integrationBackofficeEventSender;

	protected Tree composedTypeTree;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		iOPresentation = integrationObjectPresentationPopulator.getIOEditorPresentation();
		iOPresentation.setComposedTypeTree(composedTypeTree);
		treePopulator.resetState();
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectDeletedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			clearSelectedIntegrationObject();
		}
	}

	@ViewEvent(componentID = VIEW_EVENT_COMPONENT_ID, eventName = Events.ON_SELECT)
	public void composedTypeTreeOnSelect()
	{
		final TreeNodeData tnd = composedTypeTree.getSelectedItem().getValue();
		iOPresentation.setSelectedTypeInstance(tnd.getMapKeyDTO());
		sendOutput(SELECTED_ITEM_OUTPUT_SOCKET, iOPresentation.getSelectedTypeInstance());
	}

	@SocketEvent(socketId = LOAD_IO_IN_SOCKET)
	public void loadIntegrationObject(final String s)
	{
		iOPresentation.setAttributeDuplicationMap(new IntegrationObjectDefinitionDuplicationMap());
		final IntegrationObjectModel integrationObject = iOPresentation.getSelectedIntegrationObject();
		final IntegrationObjectItemModel root = integrationObject.getRootItem();
		if (root != null)
		{
			try
			{
				final IntegrationObjectDefinition integrationObjectDefinition =
						integrationObjectDefinitionConverter.toDefinitionMap(integrationObject);

				final Set<SubtypeData> subtypeDataSet =
						iOPresentation.getDataStructureBuilder()
						              .compileSubtypeDataSet(integrationObjectDefinition, new HashSet<>());

				iOPresentation.setSubtypeDataSet(subtypeDataSet);
				final CreateTreeData createTreeData = new CreateTreeData(root.getType(), integrationObjectDefinition);
				handleCreateTree(createTreeData);
				final IntegrationObjectDefinition trimmedMap = integrationObjectDefinitionTrimmer.trimMap(
						iOPresentation.getComposedTypeTree(), iOPresentation.getCurrentAttributesMap());
				if (!("").equals(validateHasKey(trimmedMap)))
				{
					showObjectLoadedFurtherConfigurationMessage();
				}
			}
			catch (final ListItemDTOMissingDescriptorModelException e)
			{
				LOG.error(e.getMessage());
				showMissingTypeMessage(e.getMessage());
			}
		}
		else
		{
			clearTreeAndListBox();
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE, Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.invalidObjectLoaded"));
		}
	}

	@SocketEvent(socketId = RETYPE_NODES_IN_SOCKET)
	public void retypeTreeNodes(final RetypeTreeData retypeTreeData)
	{
		final IntegrationMapKeyDTO parentKey = retypeTreeData.getParentKey();
		final ComposedTypeModel newComposedType = retypeTreeData.getNewComposedType();
		final Treeitem currentTreeitem = retypeTreeData.getCurrentTreeitem();
		final ListItemAttributeDTO dto = retypeTreeData.getDto();

		final String dtoAttributeDescriptorQualifier = dto.getAttributeDescriptor().getQualifier();
		updateTreeIfDynamicTreeNodeIsCreated(currentTreeitem, dto, dtoAttributeDescriptorQualifier);

		updateTreeWithSubtype(parentKey, newComposedType, dtoAttributeDescriptorQualifier);
	}

	@SocketEvent(socketId = CREATE_TREE_IN_SOCKET)
	public void handleCreateTree(final CreateTreeData data)
	{
		clearTreeAndListBox();

		final Treechildren rootLevel = iOPresentation.getComposedTypeTree().getTreechildren();
		final IntegrationMapKeyDTO rootKey = new IntegrationMapKeyDTO(data.getRoot(), data.getRoot().getCode());
		final TreeNodeData tnd = createRootTreeNodeData(rootKey);
		final Treeitem rootTreeItem = createTreeItem(tnd, true);
		rootLevel.appendChild(rootTreeItem);
		treePopulator.addAncestor(rootKey);

		final IntegrationObjectDefinition existingDefinition = data.getDefinitionMap();
		final Set<SubtypeData> subtypeDataSet = iOPresentation.getSubtypeDataSet();
		if (iOPresentation.getFilterState() == IntegrationFilterState.SHOW_ALL)
		{
			final IntegrationObjectDefinition initialDefinition = dataStructureBuilder.populateAttributesMap(rootKey,
					new IntegrationObjectDefinition());
			final IntegrationObjectDefinition updatedDefinition = treePopulator.populateTree(rootTreeItem, existingDefinition,
					initialDefinition, subtypeDataSet);
			final IntegrationObjectDefinition combinedDefinition = dataStructureBuilder.loadExistingDefinitions(
					existingDefinition, updatedDefinition);
			iOPresentation.setCurrentAttributesMap(combinedDefinition);
		}
		else
		{
			iOPresentation.setCurrentAttributesMap(existingDefinition);
			treePopulator.populateTreeSelectedMode(rootTreeItem, existingDefinition, subtypeDataSet);
		}

		rootTreeItem.setSelected(true);
		sendOnSelectEvent(rootTreeItem);
	}

	@SocketEvent(socketId = CHECK_FOR_STRUCT_IN_SOCKET)
	public void checkTreeNodeForStructuredType(final AbstractListItemDTO dto)
	{
		if (dto instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO listItemAttributeDTO = (ListItemAttributeDTO) dto;

			if (isComplexStructuredType(listItemAttributeDTO))
			{
				final Treechildren nodeChildren = composedTypeTree.getSelectedItem().getTreechildren();
				final String attributeDescriptorQualifier = listItemAttributeDTO.getAttributeDescriptor().getQualifier();
				if (findInTreechildren(attributeDescriptorQualifier, nodeChildren) == null)
				{
					createDynamicTreeNode(dto);
				}
			}
		}
	}

	@SocketEvent(socketId = RENAME_NODES_IN_SOCKET)
	public void renameTreeitemEvent(final RenameTreeData renameTreeData)
	{
		final IntegrationMapKeyDTO parentKey = renameTreeData.getParentKey();
		final AbstractListItemDTO matchedDTO = renameTreeData.getMatchedDTO();
		final String qualifier = renameTreeData.getQualifier();

		composedTypeTree.getItems().forEach(treeitem -> {
			final TreeNodeData treeNodeData = treeitem.getValue();
			if (parentKey.equals(treeNodeData.getMapKeyDTO()))
			{
				final Treeitem childTreeitem = findInTreechildren(qualifier, treeitem.getTreechildren());
				renameTreeitem(childTreeitem, matchedDTO);
			}
		});
	}

	/**
	 * Allows for the creation of tree node post initial generation. Used to add nodes for types that were not considered during
	 * initialization of the tree after they have been selected by the user.
	 *
	 * @param dto Object containing type and qualifier information used to create the new node
	 */
	@SocketEvent(socketId = CREATE_DYNAMIC_NODE_IN_SOCKET)
	public void createDynamicTreeNode(final AbstractListItemDTO dto)
	{
		final String qualifier = dto.getQualifier();
		final ComposedTypeModel type = (ComposedTypeModel) dto.getType();
		final String alias = dto.getAlias();

		final String dtoType = dto.getTypeCode();
		final IntegrationMapKeyDTO mapKeyDTO = new IntegrationMapKeyDTO(type, dtoType);

		final TreeNodeData tnd = new TreeNodeData(qualifier, alias, mapKeyDTO);

		final Treeitem parent = composedTypeTree.getSelectedItem();
		final Treeitem treeItem = treePopulator.appendTreeitem(parent, tnd);

		treePopulator.resetState();
		final IntegrationObjectDefinition iODefinition = treePopulator.populateTree(treeItem, new IntegrationObjectDefinition(),
				iOPresentation.buildAttributesMapEntry(mapKeyDTO), iOPresentation.getSubtypeDataSet());
		iOPresentation.setCurrentAttributesMap(iODefinition);
	}

	@SocketEvent(socketId = AUTO_SELECT_RELATION_IN_SOCKET)
	public void autoSelectAttributeRelation(Treeitem currentTreeitem)
	{
		final Treeitem rootTreeitem = (Treeitem) composedTypeTree.getTreechildren().getFirstChild();
		while (currentTreeitem != rootTreeitem)
		{
			final String qualifier = ((TreeNodeData) currentTreeitem.getValue()).getQualifier();
			final Treeitem parentTreeitem = currentTreeitem.getParentItem();
			final IntegrationMapKeyDTO parentType = ((TreeNodeData) parentTreeitem.getValue()).getMapKeyDTO();
			iOPresentation.getCurrentAttributesMap()
			              .getAttributesByKey(parentType)
			              .stream()
			              .filter(ListItemAttributeDTO.class::isInstance)
			              .map(ListItemAttributeDTO.class::cast)
			              .forEach(listItemDTO -> {
				              if (listItemDTO.getAttributeDescriptor().getQualifier().equals(qualifier))
				              {
					              listItemDTO.setSelected(true);
				              }
			              });
			currentTreeitem = parentTreeitem;
		}
	}

	@SocketEvent(socketId = CLEAR_TREE)
	public void handleClearTree()
	{
		composedTypeTree.getTreechildren().getChildren().clear();
		treePopulator.resetState();
	}

	public void sendOnSelectEvent(final Treeitem treeitem)
	{
		integrationBackofficeEventSender.sendEvent(Events.ON_SELECT, composedTypeTree, treeitem);
	}

	private void updateTreeIfDynamicTreeNodeIsCreated(final Treeitem currentTreeitem, final ListItemAttributeDTO dto,
	                                                  final String dtoAttributeDescriptorQualifier)
	{
		if (readService.isComplexType(dto.getType()))
		{
			final Treechildren nodeChildren = currentTreeitem.getTreechildren();
			if (findInTreechildren(dtoAttributeDescriptorQualifier, nodeChildren) == null)
			{
				createDynamicTreeNode(dto);
			}
		}
	}

	private void updateTreeWithSubtype(final IntegrationMapKeyDTO parentKey, final ComposedTypeModel newComposedType,
	                                   final String dtoAttributeDescriptorQualifier)
	{
		final List<Treeitem> treeitems = new ArrayList<>(composedTypeTree.getItems());
		final Iterator<Treeitem> iterator = treeitems.iterator();
		while (iterator.hasNext())
		{
			final Treeitem treeitem = iterator.next();
			final TreeNodeData treeNodeData = treeitem.getValue();
			final IntegrationMapKeyDTO treeNodeMapKey = treeNodeData.getMapKeyDTO();
			if (parentKey.equals(treeNodeMapKey))
			{
				final Treechildren treechildren = treeitem.getTreechildren();
				final Treeitem childTreeitem = findInTreechildren(dtoAttributeDescriptorQualifier, treechildren);
				if (childTreeitem != null)
				{
					final TreeNodeData childTreeNodeData = childTreeitem.getValue();
					final IntegrationMapKeyDTO mapKeyDTO = childTreeNodeData.getMapKeyDTO();

					updateMapKeyDTO(newComposedType, mapKeyDTO);
					childTreeNodeData.setMapKeyDTO(mapKeyDTO);

					replaceNodeInTree(treechildren, childTreeitem, childTreeNodeData);
					iterator.remove();
				}
			}
		}
	}

	private void updateMapKeyDTO(final ComposedTypeModel newComposedType, final IntegrationMapKeyDTO mapKeyDTO)
	{
		if (!mapKeyDTO.hasAlias())
		{
			mapKeyDTO.setCode(newComposedType.getCode());
		}
		mapKeyDTO.setType(newComposedType);
	}

	private void replaceNodeInTree(final Treechildren treechildren, final Treeitem childTreeitem,
	                               final TreeNodeData childTreeNodeData)
	{
		final Treeitem updatedTreeitem = createTreeItem(childTreeNodeData, childTreeitem.isOpen());
		updatedTreeitem.appendChild(new Treechildren());
		treechildren.insertBefore(updatedTreeitem, childTreeitem);
		treechildren.removeChild(childTreeitem);

		treePopulator.setAncestors(treePopulator.determineTreeitemAncestors(updatedTreeitem));
		final IntegrationObjectDefinition iODefinition = treePopulator.populateTree(updatedTreeitem,
				new IntegrationObjectDefinition(), iOPresentation.getCurrentAttributesMap(),
				iOPresentation.getSubtypeDataSet());
		iOPresentation.setCurrentAttributesMap(iODefinition);
	}

	private void showObjectLoadedFurtherConfigurationMessage()
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE, Level.WARNING,
				getLabel("integrationbackoffice.editMode.warning.msg.serviceLoadedNeedsFurtherConfig"));
	}

	private void clearSelectedIntegrationObject()
	{
		clearTreeAndListBox();
		iOPresentation.setSelectedIntegrationObject(null);
		iOPresentation.getAttributeDuplicationMap().clear();
	}

	private boolean isComplexStructuredType(final ListItemAttributeDTO dto)
	{
		final boolean isStructuredType = dto.getStructureType() == ListItemStructureType.MAP
				|| dto.getStructureType() == ListItemStructureType.COLLECTION;
		return isStructuredType && readService.isComplexType(dto.getType());
	}

	private void showMissingTypeMessage(final String sourceMessage)
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE, Level.WARNING,
				getLabel("integrationbackoffice.editMode.error.msg.missingType", Arrays.array(sourceMessage)));
	}

	private void clearTreeAndListBox()
	{
		handleClearTree();
		sendOutput(CLEAR_LIST_BOX, "");
	}
}
