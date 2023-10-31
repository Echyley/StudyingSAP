/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.controllers;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.utility.ItemTypeMatchSelector;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.itemtypematchmodal.ItemTypeMatchChangeDetector;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.search.ItemTypeMatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;

/**
 * Used to update the Item Type Match setting of the Integration Object Items of an Integration Object in the Integration UI tool of the backoffice
 */
public class ItemTypeMatchModalController extends DefaultWidgetController
{
	public static final String OPEN_MODAL_INPUT_SOCKET = "openItemTypeIOIModal";
	public static final String SAVE_BUTTON_VIEW_EVENT = "saveButton";

	private static final String SELECT_LISTBOX = "select";
	private static final String DROP_DOWN_COLUMN_ID_SUFFIX = "_ddc";
	private static final String DROP_DOWN_BOX_ID_SUFFIX = "_ddb";

	protected Listbox itemTypeMatcherListBox;
	protected Button saveButton;

	@Resource
	private transient ItemTypeMatchSelector itemTypeMatchSelector;

	/**
	 * Loads the modal and populates it with the correct types and options based on the {@link IntegrationObjectModel} selected.
	 * Due to possible multiple type definitions, items are grouped under their current type regardless of type aliasing. This
	 * results in the possibility of a given type having more that one {@link IntegrationObjectItemModel} tied to it.
	 *
	 * @param integrationObjectModel Object currently selected
	 */
	@SocketEvent(socketId = OPEN_MODAL_INPUT_SOCKET)
	public void loadModal(final IntegrationObjectModel integrationObjectModel)
	{
		saveButton.setVisible(false);
		getItemTypeMatcherListBox().getItems().clear();
		final Map<ComposedTypeModel, List<IntegrationObjectItemModel>> categorizedTypes =
				integrationObjectModel.getItems()
				                      .stream()
				                      .collect(Collectors.groupingBy(IntegrationObjectItemModel::getType));

		fillItemTypeMatcherListbox(categorizedTypes);
	}

	/**
	 * On click event for the save button.
	 */
	@ViewEvent(componentID = SAVE_BUTTON_VIEW_EVENT, eventName = Events.ON_CLICK)
	public void saveItemTypeMatchSetting()
	{
		final List<IntegrationObjectItemModel> changedList = getChangedIntegrationObjectItems();
		sendOutput("saveButtonItemTypeMatch", changedList);
	}

	/**
	 * Builds a listitem for the modal. Since all {@link IntegrationObjectItemModel}s are expected to be of the same type the first
	 * instance is used to determine how to populate the available option in the drop down.
	 *
	 * @param type                        {@link ComposedTypeModel} representing the item's type
	 * @param integrationObjectItemModels List of models that are of the given type
	 * @return Built list item
	 */
	private Listitem buildListItem(final ComposedTypeModel type,
	                               final List<IntegrationObjectItemModel> integrationObjectItemModels)
	{
		final Listitem aRow = new Listitem();
		final Listcell ioiNameColumn = new Listcell(type.getCode());
		final Listcell itemTypeMatchDropDownColumn = createItemTypeMatchDropDown(integrationObjectItemModels.get(0));
		itemTypeMatchDropDownColumn.setId(getIDForDropDownColumn(type.getCode()));
		aRow.appendChild(ioiNameColumn);
		aRow.appendChild(itemTypeMatchDropDownColumn);
		aRow.setValue(integrationObjectItemModels);
		return aRow;
	}

	private void fillItemTypeMatcherListbox(final Map<ComposedTypeModel, List<IntegrationObjectItemModel>> categorizedTypes)
	{
		new ArrayList<>(categorizedTypes.keySet())
				.stream()
				.sorted(Comparator.comparing(TypeModel::getCode))
				.forEach(type -> addItemTypeMatcherListBoxEntry(type, categorizedTypes.get(type)));
	}

	private void addItemTypeMatcherListBoxEntry(final ComposedTypeModel type,
	                                            final List<IntegrationObjectItemModel> integrationObjectItemModels)
	{
		final Listitem listItem = buildListItem(type, integrationObjectItemModels);
		getItemTypeMatcherListBox().getItems().add(listItem);
	}

	private List<IntegrationObjectItemModel> getChangedIntegrationObjectItems()
	{
		return getItemTypeMatcherListBox().getItems()
		                                  .stream()
		                                  .map(this::createChangeDetectorForEachRowOfTheModal)
		                                  .filter(ItemTypeMatchChangeDetector::isModified)
		                                  .map(ItemTypeMatchChangeDetector::getIntegrationObjectItemModels)
		                                  .flatMap(List::stream)
		                                  .collect(Collectors.toList());
	}

	/**
	 * Determines changes made to the selection in the drop down of a given item. Codes of the dropdown are retrieved from the
	 * first instance of an {@link IntegrationObjectItemModel} in the row's value as all models are of the same type, and thus any
	 * item model will suffice.
	 *
	 * @param aRow Represents a listitem in the modal
	 * @return Change detection object
	 */
	private ItemTypeMatchChangeDetector createChangeDetectorForEachRowOfTheModal(final Listitem aRow)
	{
		final List<IntegrationObjectItemModel> ioiModels = aRow.getValue();
		final String codeToDropDownColumn = getIDForDropDownColumn(ioiModels.get(0).getType().getCode());
		final String codeToActualDropDownBox = getIDForDropDownBox(ioiModels.get(0).getType().getCode());

		final Listcell itemTypeMatchDropDownColumn = (Listcell) aRow.getChildren()
		                                                            .stream()
		                                                            .filter(child -> child.getId()
		                                                                                  .equals(codeToDropDownColumn))
		                                                            .findFirst()
		                                                            .orElseThrow();

		final Listbox listboxInsideDropDownCol = (Listbox) itemTypeMatchDropDownColumn.getChildren()
		                                                                              .stream()
		                                                                              .filter(child -> child.getId()
		                                                                                                    .equals(codeToActualDropDownBox))
		                                                                              .findFirst()
		                                                                              .orElseThrow();
		final String itemTypeMatch = listboxInsideDropDownCol.getSelectedItem().getLabel();
		return new ItemTypeMatchChangeDetector(ioiModels, itemTypeMatch);

	}

	private Listcell createItemTypeMatchDropDown(final IntegrationObjectItemModel integrationObjectItemModel)
	{
		final Listcell dropDown = new Listcell();
		final Listbox listBox = new Listbox();
		listBox.setMold(SELECT_LISTBOX);
		listBox.addEventListener(Events.ON_SELECT, event -> saveButton.setVisible(true));
		listBox.setId(getIDForDropDownBox(integrationObjectItemModel.getType().getCode()));
		final ItemTypeMatch itemTypeMatchPreSelect = itemTypeMatchSelector.getToSelectItemTypeMatch(integrationObjectItemModel);
		final Collection<Listitem> itemTypeMatchEnumDropDownValues = integrationObjectItemModel.getAllowedItemTypeMatches()
		                                                                                       .stream()
		                                                                                       .map(allowedItemTypeMatch ->
				                                                                                       createDropDownOption(
						                                                                                       allowedItemTypeMatch,
						                                                                                       itemTypeMatchPreSelect)
		                                                                                       )
		                                                                                       .collect(Collectors.toList());
		listBox.getItems().addAll(itemTypeMatchEnumDropDownValues);
		dropDown.appendChild(listBox);
		return dropDown;

	}

	private Listitem createDropDownOption(final ItemTypeMatchEnum allowedItemTypeMatch, final ItemTypeMatch itemTypeMatchToSelect)
	{
		final Listitem temp = new Listitem(allowedItemTypeMatch.getCode());
		final String valueOfAllowedItemTypeMatch = ItemTypeMatch.valueOf(allowedItemTypeMatch.getCode()).getValue();
		if (itemTypeMatchToSelect.getValue().equals(valueOfAllowedItemTypeMatch))
		{
			temp.setSelected(true);
		}
		return temp;

	}

	private Listbox getItemTypeMatcherListBox()
	{
		return itemTypeMatcherListBox;
	}

	private String getIDForDropDownColumn(final String typeCode)
	{
		return typeCode + DROP_DOWN_COLUMN_ID_SUFFIX;
	}

	private String getIDForDropDownBox(final String typeCode)
	{
		return typeCode + DROP_DOWN_BOX_ID_SUFFIX;
	}
}
