/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectItemTypeMatchService;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;

import com.google.common.base.Preconditions;

public class IntegrationObjectPresentation implements Serializable
{
	private final transient DataStructureBuilder dataStructureBuilder;
	private final transient IntegrationObjectItemTypeMatchService itemTypeMatchService;

	private transient Tree composedTypeTree;
	private transient Listbox attributesListBox;

	private transient IntegrationFilterState filterState = IntegrationFilterState.SHOW_ALL;
	private transient IntegrationObjectDefinition currentAttributesMap = new IntegrationObjectDefinition();
	private transient Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap = new HashMap<>();
	private transient IntegrationObjectDefinitionDuplicationMap attributeDuplicationMap = new IntegrationObjectDefinitionDuplicationMap();
	private transient Set<SubtypeData> subtypeDataSet = new HashSet<>();
	private transient boolean isModified = false;

	private IntegrationObjectModel selectedIntegrationObject = null;
	private transient IntegrationMapKeyDTO selectedTypeInstance = null;

	private boolean editModeFlag;

	public IntegrationObjectPresentation(@NotNull final DataStructureBuilder dataStructureBuilder,
	                                     @NotNull final IntegrationObjectItemTypeMatchService itemTypeMatchService)
	{
		Preconditions.checkArgument(dataStructureBuilder != null, "DataStructureBuilder must be provided");
		Preconditions.checkArgument(itemTypeMatchService != null, "IntegrationObjectItemTypeMatchService must be provided");
		this.dataStructureBuilder = dataStructureBuilder;
		this.itemTypeMatchService = itemTypeMatchService;
	}

	public void resetState()
	{
		currentAttributesMap = new IntegrationObjectDefinition();
		itemTypeMatchMap.clear();
		attributeDuplicationMap.clear();
		subtypeDataSet.clear();
		isModified = false;

		selectedTypeInstance = null;
		selectedIntegrationObject = null;

		composedTypeTree.getTreechildren().getChildren().clear();
		attributesListBox.getItems().clear();
	}

	public boolean hasRoot()
	{
		return selectedIntegrationObject.getRootItem() != null;
	}

	public boolean isValidIOSelected()
	{
		return selectedIntegrationObject != null && hasRoot();
	}

	public IntegrationFilterState getFilterState()
	{
		return filterState;
	}

	public void setFilterState(final IntegrationFilterState filterState)
	{
		this.filterState = filterState;
	}

	/**
	 * Gets the definition map in the integration modeling view.
	 *
	 * @return the definition map encapsulated by {@link IntegrationObjectDefinition}.
	 */
	public IntegrationObjectDefinition getCurrentAttributesMap()
	{
		return currentAttributesMap;
	}

	/**
	 * Sets the definition map in the integration modeling view.
	 *
	 * @param currentAttributesMap a definition map encapsulated by {@link IntegrationObjectDefinition}.
	 */
	public void setCurrentAttributesMap(final IntegrationObjectDefinition currentAttributesMap)
	{
		this.currentAttributesMap = currentAttributesMap;
	}

	public Map<ComposedTypeModel, ItemTypeMatchEnum> getItemTypeMatchMap()
	{
		return copyItemTypeMatchMap(itemTypeMatchMap);
	}

	public void setItemTypeMatchMap(
			final Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap)
	{
		this.itemTypeMatchMap = itemTypeMatchMap;
	}

	public Set<SubtypeData> getSubtypeDataSet()
	{
		return CollectionUtils.isNotEmpty(subtypeDataSet) ? new HashSet<>(subtypeDataSet) : new HashSet<>();
	}

	public void setSubtypeDataSet(final Set<SubtypeData> subtypeDataSet)
	{
		if (CollectionUtils.isNotEmpty(subtypeDataSet))
		{
			this.subtypeDataSet = new HashSet<>(subtypeDataSet);
		}
		else
		{
			this.subtypeDataSet.clear();
		}
	}

	public boolean isModified()
	{
		return isModified;
	}

	public void setModified(final boolean modified)
	{
		isModified = modified;
	}

	public IntegrationObjectModel getSelectedIntegrationObject()
	{
		return selectedIntegrationObject;
	}

	public void setSelectedIntegrationObject(final IntegrationObjectModel selectedIntegrationObject)
	{
		this.selectedIntegrationObject = selectedIntegrationObject;
	}

	public IntegrationObjectDefinitionDuplicationMap getAttributeDuplicationMap()
	{
		return attributeDuplicationMap;
	}

	public void setAttributeDuplicationMap(
			final IntegrationObjectDefinitionDuplicationMap attributeDuplicationMap)
	{
		this.attributeDuplicationMap = attributeDuplicationMap;
	}

	public boolean isEditModeFlag()
	{
		return editModeFlag;
	}

	public void setEditModeFlag(final boolean editModeFlag)
	{
		this.editModeFlag = editModeFlag;
	}

	public DataStructureBuilder getDataStructureBuilder()
	{
		return dataStructureBuilder;
	}

	public Tree getComposedTypeTree()
	{
		return composedTypeTree;
	}

	public void setComposedTypeTree(final Tree composedTypeTree)
	{
		this.composedTypeTree = composedTypeTree;
	}

	public Listbox getAttributesListBox()
	{
		return attributesListBox;
	}

	public void setAttributesListBox(final Listbox attributesListBox)
	{
		this.attributesListBox = attributesListBox;
	}

	/**
	 * Gets the selected type instance in the integration modeling view.
	 *
	 * @return the selected type instance encapsulated by {@link IntegrationMapKeyDTO}.
	 */
	public IntegrationMapKeyDTO getSelectedTypeInstance()
	{
		return selectedTypeInstance;
	}

	/**
	 * Sets the selected type instance in the integration modeling view.
	 *
	 * @param selectedTypeInstance a type instance encapsulated by {@link IntegrationMapKeyDTO}.
	 */
	public void setSelectedTypeInstance(@NotNull final IntegrationMapKeyDTO selectedTypeInstance)
	{
		Preconditions.checkNotNull(selectedTypeInstance, "Selected type instance can't be null.");
		this.selectedTypeInstance = selectedTypeInstance;
	}

	/**
	 * Gets the list of attributes belonging to the selected type instance.
	 *
	 * @return a list of attributes belonging to the selected type instance.
	 */
	public List<AbstractListItemDTO> getSelectedTypeAttributes()
	{
		return currentAttributesMap.getAttributesByKey(selectedTypeInstance);
	}

	/**
	 * Sets the list of attributes for the selected type instance.
	 *
	 * @param attributes the list of attributes to set.
	 */
	public void setSelectedTypeAttributes(final List<AbstractListItemDTO> attributes)
	{
		currentAttributesMap.setAttributesByKey(selectedTypeInstance, attributes);
	}

	/**
	 * Calls the builder to create an entry in the attributes map
	 *
	 * @param mapKeyDTO Key under which to build the entry
	 * @return Map after new entry is built
	 */
	public IntegrationObjectDefinition buildAttributesMapEntry(final IntegrationMapKeyDTO mapKeyDTO)
	{
		return dataStructureBuilder.populateAttributesMap(mapKeyDTO, currentAttributesMap);
	}

	/**
	 * Constructs a map with entries of duplicate attribute names associated to list items. These entries are tied to any
	 * type instance where a duplicate entry occurs.
	 *
	 * @return A map containing any duplicate entries that must be resolved before persisting the IntegrationObject
	 */
	public IntegrationObjectDefinitionDuplicationMap compileDuplicationMap()
	{
		final List<AbstractListItemDTO> currentAttributes = currentAttributesMap.getAttributesByKey(selectedTypeInstance);

		attributeDuplicationMap.setAttributesByKey(selectedTypeInstance, new HashMap<>());

		currentAttributes.forEach(dto -> {
			if (dto.isSelected())
			{
				if (!attributeDuplicationMap.containsKey(selectedTypeInstance, dto.getAlias()))
				{
					attributeDuplicationMap.setAttributesByKey(selectedTypeInstance, dto.getAlias(), new ArrayList<>());
				}
				attributeDuplicationMap.addDTO(selectedTypeInstance, dto.getAlias(), dto);
			}
		});

		// Remove name entries with no duplicate instances
		final int NAME_INSTANCE_COUNT = 2;
		final Map<String, List<AbstractListItemDTO>> duplicateAttributeMap =
				attributeDuplicationMap.getDuplicateAttributesByKey(selectedTypeInstance);
		duplicateAttributeMap.entrySet()
		                     .removeIf(name -> name.getValue().size() < NAME_INSTANCE_COUNT);
		attributeDuplicationMap.setAttributesByKey(selectedTypeInstance, duplicateAttributeMap);

		attributeDuplicationMap.removeEntryWithEmptyList();
		return attributeDuplicationMap;
	}

	/**
	 * Adds a {@link SubtypeData} element to the set
	 *
	 * @param data Instance of subtype data
	 */
	public void addSubtypeData(@NotNull final SubtypeData data)
	{
		Preconditions.checkNotNull(data, "Data cannot be null.");
		subtypeDataSet.add(data);
	}

	/**
	 * Calls the item type match service's method to assign the search restriction on the {@link IntegrationObjectModel}'s items.
	 *
	 * @param ioModel {@link IntegrationObjectItemModel} who's items will have their match restrictions set for
	 * @return {@link IntegrationObjectModel} with matches set
	 */
	public void assignItemTypeMatchForIntegrationObject(final IntegrationObjectModel ioModel)
	{
		itemTypeMatchService.assignItemTypeMatchForIntegrationObject(ioModel, getItemTypeMatchMap());
	}

	/**
	 * Calls the item type match service to retrieve the search restriction for the types within the {@link IntegrationObjectModel}
	 *
	 * @param ioModel {@link IntegrationObjectModel} to gather item type matches for
	 * @return map containing the {@link ItemTypeMatchEnum} for each {@link ComposedTypeModel}
	 */
	public Map<ComposedTypeModel, ItemTypeMatchEnum> groupItemTypeMatchForIntegrationObject(
			final IntegrationObjectModel ioModel)
	{
		return itemTypeMatchService.groupItemTypeMatchForIntegrationObject(ioModel);
	}

	private Map<ComposedTypeModel, ItemTypeMatchEnum> copyItemTypeMatchMap(
			final Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap)
	{
		return itemTypeMatchMap.entrySet()
		                       .stream()
		                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
