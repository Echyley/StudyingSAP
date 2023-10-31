/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.ASCENDING;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.AUTO_SELECT_RELATION_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CHECK_FOR_STRUCTURE_TYPE_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_LIST_BOX;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.DESCENDING;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.ENABLE_SAVE_BUTTON_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_ATTR_DETAILS_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_CLASSIFICATION_ATTR_DETAILS_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_RENAME_MODAL_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_RETYPE_MODAL_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_VIRTUAL_ATTR_DETAILS_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.RENAME_TREE_NODES_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.RETYPE_TREE_NODES_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.createListItem;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.markRowsWithDuplicateNames;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.RenameAttributeModalData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RenameTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RetypeTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemVirtualAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants;
import de.hybris.platform.integrationservices.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Treeitem;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

public class IntegrationObjectEditorListboxController extends DefaultWidgetController
{
	private static final Logger LOG = Log.getLogger(IntegrationObjectEditorListboxController.class);
	public static final String SELECTED_ITEM_IN_SOCKET = "selectedItem";
	public static final String RETYPE_ATTRIBUTE_IN_SOCKET = "retypeAttributeEvent";
	public static final String RENAME_ATTRIBUTE_IN_SOCKET = "renameAttributeEvent";
	private static final String MENUPOPUP_POSITION = "after_end";

	@WireVariable
	protected transient IntegrationObjectPresentationPopulator integrationObjectPresentationPopulator;
	protected transient IntegrationObjectPresentation iOPresentation;
	@WireVariable
	protected transient ReadService readService;
	@WireVariable
	protected transient NotificationService notificationService;
	@WireVariable
	protected transient IntegrationBackofficeEventSender integrationBackofficeEventSender;

	protected Listbox attributesListBox;
	protected Listheader attributeNameListheader;
	protected Listheader descriptionListheader;
	protected transient List<String> attributeMenuPopupLabels;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		iOPresentation = integrationObjectPresentationPopulator.getIOEditorPresentation();
		iOPresentation.setAttributesListBox(attributesListBox);
		attributeMenuPopupLabels = new ArrayList<>();
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.viewDetails"));
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.renameAttribute"));
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.retypeAttribute"));
	}

	@SocketEvent(socketId = SELECTED_ITEM_IN_SOCKET)
	public void populateListBox(final IntegrationMapKeyDTO key)
	{
		clearListBox();
		addToListBox(key);
		checkDuplicates();
	}

	@SocketEvent(socketId = RETYPE_ATTRIBUTE_IN_SOCKET)
	public void retypeAttribute(final SubtypeData subtypeData)
	{
		final String alias = subtypeData.getAttributeAlias();
		final IntegrationMapKeyDTO parentKey = subtypeData.getParentNodeKey();
		final ComposedTypeModel newComposedType = readService.getComposedTypeModelFromTypeModel(subtypeData.getSubtype());
		final Treeitem currentTreeitem = iOPresentation.getComposedTypeTree().getSelectedItem();
		final ListItemAttributeDTO dto = iOPresentation.getCurrentAttributesMap()
		                                               .getAttributeByAlias(parentKey, alias);

		// Update data structures
		dto.setType(newComposedType);
		dto.createDescription();
		dto.setSelected(true);
		if (dto.isAutocreate() && newComposedType.getAbstract())
		{
			dto.setAutocreate(false);
			notificationService.notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.abstractTypeAutocreate", Arrays.array(dto.getAlias())));
		}
		sendOutput(AUTO_SELECT_RELATION_OUTPUT_SOCKET, currentTreeitem);
		final String typeString = dto.getTypeCode();
		final IntegrationMapKeyDTO mapKeyDTO = new IntegrationMapKeyDTO(newComposedType, typeString);
		final IntegrationObjectDefinition attributesMap = iOPresentation.getDataStructureBuilder()
		                                                                .populateAttributesMap(
				                                                                mapKeyDTO,
				                                                                iOPresentation.getCurrentAttributesMap());
		iOPresentation.setCurrentAttributesMap(attributesMap);
		iOPresentation.addSubtypeData(subtypeData);

		final RetypeTreeData retypeTreeData = new RetypeTreeData(parentKey, newComposedType, currentTreeitem, dto);
		sendOutput(RETYPE_TREE_NODES_OUTPUT_SOCKET, retypeTreeData);

		LOG.info("Base type {} updated to subtype {} under parent code {}", subtypeData.getParentNodeKey().getCode(),
				newComposedType.getCode(), parentKey.getCode());
		sendOnSelectEvent(currentTreeitem);
		focusOnListitem(alias);
		enableSaveButton();
	}

	@SocketEvent(socketId = RENAME_ATTRIBUTE_IN_SOCKET)
	public void renameAttribute(final RenameAttributeModalData renameAttributeModalData)
	{
		final IntegrationMapKeyDTO parentKey = renameAttributeModalData.getParent();
		final AbstractListItemDTO updatedDTO = renameAttributeModalData.getDto();
		final String alias = updatedDTO.getAlias();
		final AbstractListItemDTO matchedDTO;

		matchedDTO = updateAliasOfMatchingDTO(parentKey, updatedDTO, alias);

		if (matchedDTO.isComplexType(readService))
		{
			notifyTreeForRename(parentKey, matchedDTO);
		}

		sendOutput(AUTO_SELECT_RELATION_OUTPUT_SOCKET, iOPresentation.getComposedTypeTree().getSelectedItem());

		final String typeCode = iOPresentation.getSelectedTypeInstance().getType().getCode();
		if (readService.isProductType(typeCode))
		{
			iOPresentation.setAttributeDuplicationMap(iOPresentation.compileDuplicationMap());
		}

		populateListBox(parentKey);
		focusOnListitem(alias);
		enableSaveButton();
	}

	@SocketEvent(socketId = CLEAR_LIST_BOX)
	public void handlerClearListBox()
	{
		attributesListBox.getItems().clear();
	}

	public void sendOnSelectEvent(final Treeitem treeitem)
	{
		integrationBackofficeEventSender.sendEvent(Events.ON_SELECT, iOPresentation.getComposedTypeTree(), treeitem);
	}

	public void focusOnListitem(final String alias)
	{
		for (final Listitem listitem : getAttributesListBox().getItems())
		{
			final AbstractListItemDTO dto = listitem.getValue();
			if (dto.getAlias().equals(alias))
			{
				Clients.scrollIntoView(listitem);
				break;
			}
		}
	}

	private void checkDuplicates()
	{
		if (markRowsWithDuplicateNames(getAttributesListBox().getItems(), getSelectedTypeInstanceDuplicates()))
		{
			notificationService.notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.nameDuplication"));
		}
	}

	private void addToListBox(final IntegrationMapKeyDTO key)
	{
		final String attributeNamesSortingDirection = attributeNameListheader.getSortDirection();
		final String descriptionsSortingDirection = descriptionListheader.getSortDirection();
		final Listbox listBox = getAttributesListBox();
		getCurrentAttributesMap()
				.getAttributesByKey(key)
				.stream()
				.sorted((dto1, dto2) -> compareItems(dto1, dto2, attributeNamesSortingDirection, descriptionsSortingDirection))
				.forEach(dto -> listBox.appendChild(setupListItem(key, dto)));
	}

	private int compareItems(final AbstractListItemDTO dto1, final AbstractListItemDTO dto2,
	                         final String attributeNamesSortingDirection, final String descriptionsSortingDirection)
	{
		if (ASCENDING.equals(attributeNamesSortingDirection))
		{
			return dto1.getAlias().compareToIgnoreCase(dto2.getAlias());
		}
		else if (DESCENDING.equals(attributeNamesSortingDirection))
		{
			return dto2.getAlias().compareToIgnoreCase(dto1.getAlias());
		}
		else if (ASCENDING.equals(descriptionsSortingDirection))
		{
			return dto1.getDescription().compareToIgnoreCase(dto2.getDescription());
		}
		else if (DESCENDING.equals(descriptionsSortingDirection))
		{
			return dto2.getDescription().compareToIgnoreCase(dto1.getDescription());
		}
		else
		{
			return dto1.getAlias().compareToIgnoreCase(dto2.getAlias());
		}
	}

	private void notifyTreeForRename(final IntegrationMapKeyDTO parentKey, final AbstractListItemDTO matchedDTO)
	{
		final RenameTreeData renameTreeData;
		final String qualifier = matchedDTO.getQualifier();

		sendOutput(CHECK_FOR_STRUCTURE_TYPE_OUTPUT_SOCKET, matchedDTO);
		renameTreeData = new RenameTreeData(parentKey, matchedDTO, qualifier);
		sendOutput(RENAME_TREE_NODES_OUTPUT_SOCKET, renameTreeData);
	}

	private AbstractListItemDTO updateAliasOfMatchingDTO(final IntegrationMapKeyDTO parentKey,
	                                                     final AbstractListItemDTO updatedDTO,
	                                                     final String alias)
	{
		final AbstractListItemDTO matchedDTO;

		matchedDTO = updatedDTO.findMatch(iOPresentation.getCurrentAttributesMap(), parentKey);
		matchedDTO.setAlias(alias);
		matchedDTO.setSelected(true);
		return matchedDTO;
	}

	private void clearListBox()
	{
		getAttributesListBox().getItems().clear();
	}

	private Listitem setupListItem(final IntegrationMapKeyDTO key, final AbstractListItemDTO abstractListItemDTO)
	{
		boolean isComplex = false;
		boolean hasSubtypes = false;
		if (abstractListItemDTO instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO dto = (ListItemAttributeDTO) abstractListItemDTO;
			isComplex = dto.isComplexType(readService);
			if (isComplex)
			{
				hasSubtypes = !readService.getComposedTypeModelFromTypeModel(dto.getBaseType()).getSubtypes().isEmpty();
			}
		}
		final Listitem listItem = createListItem(abstractListItemDTO, isComplex, hasSubtypes, attributeMenuPopupLabels,
				getEditModeFlag(), readService);
		final Button optionsBtn = (Button) listItem.getLastChild().getFirstChild();
		final List<AbstractListItemDTO> itemAttributeDTOs = new ArrayList<>(getCurrentAttributesMap().getAttributesByKey(key));
		itemAttributeDTOs.remove(abstractListItemDTO);
		final RenameAttributeModalData renameAttributeModalData = new RenameAttributeModalData(itemAttributeDTOs,
				abstractListItemDTO, key);

		if (getEditModeFlag())
		{
			if (abstractListItemDTO instanceof ListItemAttributeDTO)
			{
				addListItemEvents((ListItemAttributeDTO) abstractListItemDTO, listItem);
			}
			else if (abstractListItemDTO instanceof ListItemClassificationAttributeDTO || abstractListItemDTO instanceof ListItemVirtualAttributeDTO)
			{
				listItem.addEventListener(Events.ON_CLICK, event -> {
					if (!listItem.isSelected())
					{
						getAutoCreateCheckbox(listItem).setChecked(false);
					}
					updateAttribute(listItem);
				});
			}
			addCheckboxEvents(listItem);
			addDuplicationMarkerEvent(listItem);
		}
		else
		{
			addMaintainStateEvents(abstractListItemDTO, listItem);
		}

		addButtonEvents(listItem, optionsBtn);
		addMenuItemEvents(renameAttributeModalData, optionsBtn);

		if (!getEditModeFlag())
		{
			getUniqueCheckbox(listItem).setDisabled(true);
			getAutoCreateCheckbox(listItem).setDisabled(true);
			getRenameAttribute(optionsBtn).setVisible(false);
		}
		return listItem;
	}

	private void addMenuItemEvents(final RenameAttributeModalData ramd, final Button optionsBtn)
	{
		final Menuitem viewDetails = getViewDetails(optionsBtn);
		final Menuitem renameAttribute = getRenameAttribute(optionsBtn);
		final Menuitem retypeAttribute = getRetypeAttribute(optionsBtn);
		renameAttribute.addEventListener(Events.ON_CLICK, event -> sendOutput(OPEN_RENAME_MODAL_OUTPUT_SOCKET, ramd));

		if (ramd.getDto() instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO ramdDTO = (ListItemAttributeDTO) ramd.getDto();
			final SubtypeData data = new SubtypeData(ramd.getParent(), ramdDTO.getType(), ramdDTO.getBaseType(),
					ramd.getDto().getAlias(), ramdDTO.getAttributeDescriptor().getQualifier());
			retypeAttribute.addEventListener(Events.ON_CLICK, event -> sendOutput(OPEN_RETYPE_MODAL_OUTPUT_SOCKET, data));

			viewDetails.addEventListener(Events.ON_CLICK, event -> sendOutput(OPEN_ATTR_DETAILS_OUTPUT_SOCKET, ramd.getDto()));
		}
		else if (ramd.getDto() instanceof ListItemClassificationAttributeDTO)
		{
			viewDetails.addEventListener(Events.ON_CLICK,
					event -> sendOutput(OPEN_CLASSIFICATION_ATTR_DETAILS_OUTPUT_SOCKET, ramd.getDto()));
		}
		else
		{
			viewDetails.addEventListener(Events.ON_CLICK,
					event -> sendOutput(OPEN_VIRTUAL_ATTR_DETAILS_OUTPUT_SOCKET, ramd.getDto()));
		}
	}


	private void addCheckboxEvents(final Listitem listItem)
	{
		final Checkbox uniqueCheckbox = getUniqueCheckbox(listItem);
		final Checkbox autocreateCheckbox = getAutoCreateCheckbox(listItem);
		uniqueCheckbox.addEventListener(Events.ON_CHECK, event -> checkboxEventActions(listItem, uniqueCheckbox));
		autocreateCheckbox.addEventListener(Events.ON_CHECK, event -> checkboxEventActions(listItem, autocreateCheckbox));
	}

	private void checkboxEventActions(final Listitem listItem, final Checkbox checkbox)
	{
		if (!listItem.isDisabled())
		{
			if (checkbox.isChecked())
			{
				listItem.setSelected(true);
				if (listItem.getValue() instanceof ListItemAttributeDTO)
				{
					sendOutput(CHECK_FOR_STRUCTURE_TYPE_OUTPUT_SOCKET, listItem.getValue());
				}
			}
			updateAttribute(listItem);
		}
	}

	private void addButtonEvents(final Listitem listItem, final Button detailsBtn)
	{
		detailsBtn.addEventListener(Events.ON_CLICK, event -> {
			if (!listItem.isDisabled())
			{
				final Menupopup menuPopup = (Menupopup) listItem.getLastChild().getFirstChild().getFirstChild();
				menuPopup.open(detailsBtn, MENUPOPUP_POSITION);
			}
		});
	}

	private void addListItemEvents(final ListItemAttributeDTO dto, final Listitem listItem)
	{
		final Checkbox uniqueCheckbox = getUniqueCheckbox(listItem);
		final Checkbox autocreateCheckbox = getAutoCreateCheckbox(listItem);
		if (dto.isRequired())
		{
			listItem.addEventListener(Events.ON_CLICK, event ->
					maintainSelectionState(listItem, true));
		}
		else if (!dto.isSupported())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> {
				maintainSelectionState(listItem, false);
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.illegalMapTypeAttribute",
								Arrays.array(dto.getAttributeDescriptor().getQualifier())));
			});
		}
		else if (!listItem.isDisabled())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> {
				if (listItem.isSelected())
				{
					sendOutput(CHECK_FOR_STRUCTURE_TYPE_OUTPUT_SOCKET, dto);
				}
				else
				{
					if (!dto.getAttributeDescriptor().getUnique())
					{
						uniqueCheckbox.setChecked(false);
					}
					autocreateCheckbox.setChecked(false);
				}
				updateAttribute(listItem);
			});
		}
	}

	private void addMaintainStateEvents(final AbstractListItemDTO dto, final Listitem listItem)
	{
		if (dto.isSelected())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> maintainSelectionState(listItem, true));
		}
		else
		{
			listItem.addEventListener(Events.ON_CLICK, event -> maintainSelectionState(listItem, false));
		}
	}

	private void maintainSelectionState(final Listitem listItem, final boolean selected)
	{
		listItem.setSelected(selected);
	}

	private void addDuplicationMarkerEvent(final Listitem listItem)
	{
		listItem.addEventListener(Events.ON_CLICK, event -> checkForDuplicates());
	}

	private void checkForDuplicates()
	{
		iOPresentation.setAttributeDuplicationMap(iOPresentation.compileDuplicationMap());
		markRowsWithDuplicateNames(getAttributesListBox().getItems(), getSelectedTypeInstanceDuplicates());
	}

	private void updateAttribute(final Listitem listitem)
	{
		final AbstractListItemDTO dto = listitem.getValue();
		final List<Component> components = listitem.getChildren();
		final Checkbox uCheckbox = ((Checkbox) components.get(EditorConstants.UNIQUE_CHECKBOX_COMPONENT_INDEX).getFirstChild());
		final Checkbox aCheckbox = ((Checkbox) components.get(EditorConstants.AUTOCREATE_CHECKBOX_COMPONENT_INDEX)
		                                                 .getFirstChild());
		final Listcell attributeLabel = (Listcell) components.get(EditorConstants.ATTRIBUTE_NAME_COMPONENT_INDEX);
		dto.setAlias(attributeLabel.getLabel());
		dto.setSelected(listitem.isSelected());
		dto.setCustomUnique(uCheckbox.isChecked());
		dto.setAutocreate(aCheckbox.isChecked());
		if (getEditModeFlag())
		{
			enableSaveButton();
		}
	}

	private void enableSaveButton()
	{
		if (!iOPresentation.isModified())
		{
			iOPresentation.setModified(true);
			sendOutput(ENABLE_SAVE_BUTTON_OUTPUT_SOCKET, true);
		}
	}

	private Menuitem getRetypeAttribute(final Button optionsBtn)
	{
		return (Menuitem) optionsBtn.getFirstChild().getLastChild();
	}

	private Menuitem getRenameAttribute(final Button optionsBtn)
	{
		return (Menuitem) optionsBtn.getFirstChild().getFirstChild().getNextSibling();
	}

	private Menuitem getViewDetails(final Button optionsBtn)
	{
		return (Menuitem) optionsBtn.getFirstChild().getFirstChild();
	}

	private Checkbox getAutoCreateCheckbox(final Listitem listItem)
	{
		return (Checkbox) listItem.getChildren()
		                          .get(EditorConstants.AUTOCREATE_CHECKBOX_COMPONENT_INDEX)
		                          .getFirstChild();
	}

	private Checkbox getUniqueCheckbox(final Listitem listItem)
	{
		return (Checkbox) listItem.getChildren()
		                          .get(EditorConstants.UNIQUE_CHECKBOX_COMPONENT_INDEX)
		                          .getFirstChild();
	}

	private IntegrationObjectDefinition getCurrentAttributesMap()
	{
		return iOPresentation.getCurrentAttributesMap();
	}

	private boolean getEditModeFlag()
	{
		return iOPresentation.isEditModeFlag();
	}

	private Map<String, List<AbstractListItemDTO>> getSelectedTypeInstanceDuplicates()
	{
		return iOPresentation.getAttributeDuplicationMap()
		                     .getDuplicateAttributesByKey(iOPresentation.getSelectedTypeInstance());
	}

	private Listbox getAttributesListBox()
	{
		return iOPresentation.getAttributesListBox();
	}
}
