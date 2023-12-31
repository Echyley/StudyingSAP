/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.utility;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.utility.QualifierNameUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.ListItemAttributeStatus;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemVirtualAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultListItemAttributeDTOUpdater;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Span;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.hybris.cockpitng.util.UITools;

public final class EditorUtils
{
	private static final String CSS_LISTITEM_DISABLED = "z-listitem-fixed-status";
	private static final String CSS_LISTITEM_NOT_SUPPORTED = "z-listitem-not-supported";
	private static final String CSS_LISTITEM_NOT_SUPPORTED_CELL = "z-listitem-not-supported-cell";
	private static final String CSS_LISTCELL_UNIQUE = "integrationbackoffice-editor-listbox-checkbox-unique";
	private static final String CSS_LISTCELL_AUTOCREATE = "integrationbackoffice-editor-listbox-checkbox-autocreate";
	private static final String CSS_BUTTON_DROPDOWN = "ye-actiondots-integrationbackoffice-btn";
	private static final String CSS_MENUPOPUP = "ye-inline-editor-row-popup";
	private static final String TITLE_BUTTON_DROPDOWN = "integrationbackoffice.editMode.listitem.dropdown.button";

	private static final ListItemAttributeDTOUpdater UPDATER = new DefaultListItemAttributeDTOUpdater();

	private EditorUtils()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Creates a combobox item
	 *
	 * @param label Label of item
	 * @param value Value of item
	 * @return Combobox item
	 */
	public static Comboitem createComboItem(final String label, final Object value)
	{
		final Comboitem item = new Comboitem(label);
		item.setValue(value);
		return item;
	}

	/**
	 * Creates a tree item. Qualifier of the root node will be NULL.
	 *
	 * @param value    Value of tree item
	 * @param expanded Whether node is expanded by default
	 * @return Tree item
	 */
	public static Treeitem createTreeItem(final TreeNodeData value, final boolean expanded)
	{
		final String label = createTreeitemLabel(value);
		final Treeitem treeitem = new Treeitem();
		final Treerow treerow = new Treerow();
		final Treecell treecell = new Treecell(label);
		treerow.appendChild(treecell);
		treeitem.appendChild(treerow);
		treeitem.setValue(value);
		treeitem.setOpen(expanded);
		return treeitem;
	}

	/**
	 * Creates a list item
	 *
	 * @param dto         DTO containing properties to populate list item
	 * @param isComplex   If attribute it a complex type
	 * @param hasSubtypes If attribute has subtypes
	 * @param labels      Menuitem labels
	 * @param inEditMode  If in edit mode
	 * @return A list item
	 */
	public static Listitem createListItem(final AbstractListItemDTO dto, final boolean isComplex, final boolean hasSubtypes,
	                                      final List<String> labels, final boolean inEditMode, final ReadService readService)
	{
		final Listitem listitem = new Listitem();
		final Listcell labelListcell = new Listcell(dto.getAlias());
		final Listcell descriptionListcell = new Listcell(dto.getDescription());
		final Listcell attributeStatusListcell = new Listcell();
		final Span attributeStatusSpan = new Span();
		final Listcell uniqueListcell = new Listcell();
		final Checkbox uniqueCheckbox = new Checkbox();
		final Listcell autocreateListcell = new Listcell();
		final Checkbox autocreateCheckbox = new Checkbox();
		final Listcell dropdownListcell = new Listcell();
		final Button dropdownButton = new Button();
		final Menupopup menuPopup = createMenuPopup(labels);

		uniqueListcell.setSclass(CSS_LISTCELL_UNIQUE);
		autocreateListcell.setSclass(CSS_LISTCELL_AUTOCREATE);
		dropdownButton.setSclass(CSS_BUTTON_DROPDOWN);
		menuPopup.setSclass(CSS_MENUPOPUP);

		dropdownButton.setTitle(Labels.getLabel(TITLE_BUTTON_DROPDOWN));

		attributeStatusListcell.appendChild(attributeStatusSpan);

		uniqueListcell.appendChild(uniqueCheckbox);
		autocreateListcell.appendChild(autocreateCheckbox);

		dropdownButton.appendChild(menuPopup);
		dropdownListcell.appendChild(dropdownButton);

		listitem.appendChild(labelListcell);
		listitem.appendChild(descriptionListcell);
		listitem.appendChild(attributeStatusListcell);
		listitem.appendChild(uniqueListcell);
		listitem.appendChild(autocreateListcell);
		listitem.appendChild(dropdownListcell);

		if (dto instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO attributeDTO = (ListItemAttributeDTO) dto;
			final ListItemAttributeStatus status = determineAttributeStatus(attributeDTO, readService);

			assignStatusBadgeValues(attributeStatusSpan, status);

			assignUniqueCheckboxRules(attributeDTO, uniqueCheckbox);
			assignAutocreateCheckboxRules(attributeDTO, isComplex, autocreateCheckbox);
			assignListitemRules(attributeDTO, listitem);
		}
		else if (dto instanceof ListItemClassificationAttributeDTO)
		{
			assignClassificationRules((ListItemClassificationAttributeDTO) dto, listitem, uniqueCheckbox, autocreateCheckbox);
			assignStatusBadgeValues(attributeStatusSpan, ListItemAttributeStatus.CLASSIFICATION);
		}
		else
		{
			assignVirtualRules((ListItemVirtualAttributeDTO) dto, listitem, uniqueCheckbox, autocreateCheckbox);
			assignStatusBadgeValues(attributeStatusSpan, ListItemAttributeStatus.VIRTUAL);
		}

		assignMenuitemRules(isComplex, hasSubtypes, menuPopup);

		if (!inEditMode)
		{
			assignEditModeRules(listitem, uniqueCheckbox, autocreateCheckbox, menuPopup);
		}

		listitem.setValue(dto);
		return listitem;
	}

	/**
	 * Create menu popup
	 *
	 * @param labels Labels for menu items
	 * @return Menu popup
	 */
	public static Menupopup createMenuPopup(final List<String> labels)
	{
		final Menupopup popup = new Menupopup();
		labels.forEach(label -> {
			final Menuitem item = new Menuitem(label);
			popup.appendChild(item);
		});
		return popup;
	}

	/**
	 * Renames a tree item
	 *
	 * @param treeitem Item to be renamed
	 * @param dto      DTO containing relevant rename information
	 * @return Newly renamed tree item
	 */
	public static Treeitem renameTreeitem(final Treeitem treeitem, final AbstractListItemDTO dto)
	{
		if (treeitem != null)
		{
			final TreeNodeData treeNodeData = treeitem.getValue();
			final String alias = dto.getAlias();
			treeNodeData.setAlias(alias);
			treeitem.setLabel(alias + " [" + treeNodeData.getMapKeyDTO().getCode() + " : " + treeNodeData.getMapKeyDTO()
			                                                                                             .getType()
			                                                                                             .getCode() + "]");
		}
		return treeitem;
	}

	/**
	 * Find a matching Treeitem in a given parent Treeitem's Treechildren by qualifier.
	 *
	 * @param qualifier    the qualifier to match
	 * @param treechildren the Treechildren to search
	 * @return a matching Treeitem, null if not found
	 */
	public static Treeitem findInTreechildren(final String qualifier, final Treechildren treechildren)
	{
		if (treechildren != null)
		{
			final List<Treeitem> children = treechildren.getChildren();
			for (final Treeitem treeitem : children)
			{
				final TreeNodeData treeNodeData = treeitem.getValue();
				if (treeNodeData.getQualifier().equals(qualifier))
				{
					return treeitem;
				}
			}
		}
		return null;
	}

	/**
	 * Updates attributes of a list of DTOs by getting the attributes of another list of DTOs
	 *
	 * @param oldDTOs a list of DTOs with attributes to update
	 * @param newDTOs a list of DTOs containing updated attributes
	 * @return a list of DTOs with updated attributes
	 * @deprecated Please use the {@link de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public List<AbstractListItemDTO> updateDTOs(final List<AbstractListItemDTO> oldDTOs, final List<AbstractListItemDTO> newDTOs)
	{
		return UPDATER.updateDTOs(oldDTOs, newDTOs);
	}

	/**
	 * Determines the structure type of the given AttributeDescriptorModel
	 *
	 * @param readService the ReadService to read from the type system
	 * @param attribute   the AttributeDescriptorModel to evaluate
	 * @return ListItemStructureType enum representing the structure type of the AttributeDescriptorModel
	 */
	public static ListItemStructureType getListItemStructureType(final ReadService readService,
	                                                             final AttributeDescriptorModel attribute)
	{
		final ListItemStructureType structureType;
		if (readService.isCollectionType(attribute.getAttributeType().getItemtype()))
		{
			structureType = ListItemStructureType.COLLECTION;
		}
		else if (readService.isMapType(attribute.getAttributeType().getItemtype()))
		{
			structureType = ListItemStructureType.MAP;
		}
		else
		{
			structureType = ListItemStructureType.NONE;
		}
		return structureType;
	}

	/**
	 * Modifies the sClass of the list items to show highlights on duplicate items.
	 *
	 * @param items            List of items to iterate over
	 * @param duplicateEntries Map containing duplicate entries to match with item.
	 */
	public static boolean markRowsWithDuplicateNames(final Collection<Listitem> items,
	                                                 final Map<String, List<AbstractListItemDTO>> duplicateEntries)
	{
		boolean hasHighlight = false;
		items.forEach(item -> UITools.removeSClass(item, "integration-object-attribute-name-conflict"));
		if (duplicateEntries != null)
		{
			for (final List<AbstractListItemDTO> listItemDTOS : duplicateEntries.values())
			{
				for (final AbstractListItemDTO entry : listItemDTOS)
				{
					hasHighlight = highlightListitem(items, hasHighlight, entry);
				}
			}
		}
		return hasHighlight;
	}

	/**
	 * Determines whether or not a classification exists in the list of DTOs to avoid creating a duplicate of the exact same entry.
	 *
	 * @param classAttributeAssignmentModel Classification being checked
	 * @param dtos                          List of existing DTOs to search for a potential match of type
	 * @return Whether or not a match is present
	 */
	public static boolean isClassificationAttributePresent(final ClassAttributeAssignmentModel classAttributeAssignmentModel,
	                                                       final List<AbstractListItemDTO> dtos)
	{
		for (final AbstractListItemDTO dto : dtos)
		{
			if (dto instanceof ListItemClassificationAttributeDTO)
			{
				final ListItemClassificationAttributeDTO classificationAttributeDTO = (ListItemClassificationAttributeDTO) dto;
				final String dtoAttributeCode = classificationAttributeDTO.getClassificationAttributeCode();
				final String assignmentModelCode = QualifierNameUtils.removeNonAlphaNumericCharacters(
						classAttributeAssignmentModel.getClassificationAttribute().getCode());
				final String dtoCategory = classificationAttributeDTO.getCategoryCode();
				final String assignmentModelCategory = classAttributeAssignmentModel.getClassificationClass().getCode();
				if (dtoAttributeCode.equals(assignmentModelCode) && dtoCategory.equals(assignmentModelCategory))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines the attribute status
	 *
	 * @param dto         Attribute DTO to be evaluated
	 * @param readService ReadService instance
	 * @return Status of attribute
	 */
	public static ListItemAttributeStatus determineAttributeStatus(final ListItemAttributeDTO dto, final ReadService readService)
	{
		if (!dto.isSupported())
		{
			return ListItemAttributeStatus.NOT_SUPPORTED;
		}
		else if (dto.isRequired())
		{
			return ListItemAttributeStatus.REQUIRED;
		}
		else if (readService.getReadOnlyAttributes().contains(dto.getAttributeDescriptor().getQualifier()))
		{
			return ListItemAttributeStatus.READ_ONLY;
		}
		else
		{
			return ListItemAttributeStatus.NONE;
		}
	}

	private static void assignEditModeRules(final Listitem listitem, final Checkbox uniqueCheckbox,
	                                        final Checkbox autocreateCheckbox, final Menupopup menuPopup)
	{
		uniqueCheckbox.setDisabled(true);
		autocreateCheckbox.setDisabled(true);
		listitem.setSclass(CSS_LISTITEM_DISABLED);
		(menuPopup.getChildren().get(EditorConstants.RENAME_MENUITEM_INDEX)).setVisible(false);
		(menuPopup.getChildren().get(EditorConstants.RETYPE_MENUITEM_INDEX)).setVisible(false);
	}

	private static void assignUniqueCheckboxRules(final ListItemAttributeDTO dto, final Checkbox attrUniqueCheckbox)
	{
		final ListItemStructureType structureType = dto.getStructureType();
		if (structureType == ListItemStructureType.COLLECTION || structureType == ListItemStructureType.MAP)
		{
			attrUniqueCheckbox.setDisabled(true);
		}
		else if (dto.getAttributeDescriptor().getUnique())
		{
			attrUniqueCheckbox.setChecked(true);
			attrUniqueCheckbox.setDisabled(true);
		}
		else
		{
			attrUniqueCheckbox.setChecked(dto.isCustomUnique());
		}
	}

	private static void assignAutocreateCheckboxRules(final ListItemAttributeDTO dto, final boolean isComplex,
	                                                  final Checkbox attrAutocreateCheckbox)
	{
		final boolean isAbstract = (dto.getType() instanceof ComposedTypeModel) ? ((ComposedTypeModel) dto.getType()).getAbstract() : false;
		if (!isComplex || isAbstract)
		{
			attrAutocreateCheckbox.setDisabled(true);
		}
		else
		{
			attrAutocreateCheckbox.setChecked(dto.isAutocreate());
		}
	}

	private static void assignListitemRules(final ListItemAttributeDTO dto, final Listitem child)
	{
		if (dto.isSupported())
		{
			if (dto.isRequired())
			{
				child.setSelected(true);
				child.setSclass(CSS_LISTITEM_DISABLED);
			}
			else if (dto.isCustomUnique())
			{
				child.setSelected(true);
			}
			else
			{
				child.setSelected(dto.isSelected());
			}
		}
		else
		{
			child.setSclass(CSS_LISTITEM_NOT_SUPPORTED);
			((Listcell) child.getChildren().get(EditorConstants.ATTRIBUTE_NAME_COMPONENT_INDEX)).setSclass(
					CSS_LISTITEM_NOT_SUPPORTED_CELL);
			((Listcell) child.getChildren().get(EditorConstants.ATTRIBUTE_DESC_COMPONENT_INDEX)).setSclass(
					CSS_LISTITEM_NOT_SUPPORTED_CELL);
		}
	}

	private static void assignClassificationRules(final ListItemClassificationAttributeDTO dto, final Listitem listitem,
	                                              final Checkbox uniqueCheckbox, final Checkbox autocreateCheckbox)
	{
		listitem.setSelected(dto.isSelected());
		uniqueCheckbox.setDisabled(true);
		autocreateCheckbox.setChecked(dto.isAutocreate());
		autocreateCheckbox.setDisabled(dto.getClassAttributeAssignmentModel().getReferenceType() == null);
	}

	private static void assignVirtualRules(final ListItemVirtualAttributeDTO dto, final Listitem listitem,
	                                       final Checkbox uniqueCheckbox, final Checkbox autocreateCheckbox)
	{
		listitem.setSelected(dto.isSelected());
		uniqueCheckbox.setDisabled(true);
		autocreateCheckbox.setDisabled(true);
	}


	private static void assignMenuitemRules(final boolean isComplex, final boolean hasSubtypes, final Menupopup detailsMenu)
	{
		if (!isComplex || !hasSubtypes)
		{
			detailsMenu.getLastChild().setVisible(false); // hide 'Change type' option
		}
	}

	private static boolean highlightListitem(final Collection<Listitem> items, boolean hasHighlight,
	                                         final AbstractListItemDTO entry)
	{
		for (final Listitem item : items)
		{
			if (item.getValue().equals(entry))
			{
				UITools.addSClass(item, "integration-object-attribute-name-conflict");
				hasHighlight = true;
			}
		}
		return hasHighlight;
	}

	private static void assignStatusBadgeValues(final Span statusBadge, final ListItemAttributeStatus status)
	{
		final Label statusLabel = new Label(status.getLabel());

		statusBadge.setSclass(status.getStyling());
		statusBadge.appendChild(statusLabel);
	}

	private static String createTreeitemLabel(final TreeNodeData value)
	{
		return (value.getQualifier() == null) ? value.getMapKeyDTO().getCode() :
				(value.getAlias() + " [" + value.getMapKeyDTO().getCode() + " : " + value.getMapKeyDTO()
				                                                                         .getType()
				                                                                         .getCode() + "]");
	}
}
