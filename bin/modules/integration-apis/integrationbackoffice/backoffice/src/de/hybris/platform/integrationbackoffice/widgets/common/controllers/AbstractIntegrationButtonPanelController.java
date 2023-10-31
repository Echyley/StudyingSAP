/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;

/**
 * Controller with common functionality across integrationbackoffice toolbar button panel controllers.
 * Contains a filter menu, refresh button and save button.
 */
public abstract class AbstractIntegrationButtonPanelController extends DefaultWidgetController
{
	public static final String REFRESH_BUTTON_CLICK_SOCKET = "refreshButtonClick";
	public static final String SAVE_BUTTON_CLICK_SOCKET = "saveButtonClick";
	public static final String FILTER_STATE_IN_SOCKET = "filterStateInput";
	public static final String FILTER_STATE_OUT_SOCKET = "filterStateOutput";
	public static final String LOAD_OBJECT_IN_SOCKET = "receiveObjectComboBox";
	public static final String ENABLE_SAVE_IN_SOCKET = "enableSaveButtonEvent";
	public static final String SAVE_DEFINITIONS_BUTTON_ID = "saveDefinitionsButton";
	public static final String REFRESH_BUTTON_ID = "refreshButton";

	protected static final String FILTER_BUTTON_SCLASS = "yw-integrationbackoffice-filter-btn";
	protected static final String FILTER_MENU_SCLASS = "yw-integrationbackoffice-filterMenu";
	protected static final String SHOWALL_MENUITEM_LABEL_KEY = "integrationbackoffice.editMode.menuItem.showAll";
	protected static final String ONLYSELECTED_MENUITEM_LABEL_KEY = "integrationbackoffice.editMode.menuItem.preview";
	protected static final String FILTER_BUTTON_TITLE_KEY = "integrationbackoffice.toolbar.buttonPanel.filter";

	protected static final String MENUPOPUP_POSITION = "after_end";

	protected org.zkoss.zul.Button saveDefinitionsButton;
	protected Div filterButtonDiv;

	protected Button filterButton;
	protected Menupopup filterMenupopup;
	protected Menuitem showAllMenuItem;
	protected Menuitem onlySelectedMenuItem;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		saveDefinitionsButton.setDisabled(true);
		filterMenupopup = createFilterMenupopup();
		filterButton = new Button();
		filterButton.setDisabled(true);
		filterButton.setSclass(FILTER_BUTTON_SCLASS);
		filterButton.addEventListener(Events.ON_CLICK, event -> filterMenupopup.open(filterButton, MENUPOPUP_POSITION));
		filterButton.setTitle(getLabel(FILTER_BUTTON_TITLE_KEY));
		filterButton.appendChild(filterMenupopup);
		filterButtonDiv.appendChild(filterButton);
	}

	@SocketEvent(socketId = LOAD_OBJECT_IN_SOCKET)
	public void loadObject(final Object object)
	{
		saveDefinitionsButton.setDisabled(true);
		filterButton.setDisabled(false);
	}

	@SocketEvent(socketId = ENABLE_SAVE_IN_SOCKET)
	public void enableSaveButton(final Boolean isEnabled)
	{
		if (isEnabled != null)
		{
			saveDefinitionsButton.setDisabled(!isEnabled);
		}
	}

	@SocketEvent(socketId = FILTER_STATE_IN_SOCKET)
	public void updateFilterState(final IntegrationFilterState state)
	{
		if (state != null)
		{
			if (state == IntegrationFilterState.SHOW_ALL)
			{
				showAllMenuItem.setChecked(true);
				onlySelectedMenuItem.setChecked(false);
			}
			else
			{
				showAllMenuItem.setChecked(false);
				onlySelectedMenuItem.setChecked(true);
			}
		}
	}

	@ViewEvent(componentID = SAVE_DEFINITIONS_BUTTON_ID, eventName = Events.ON_CLICK)
	public void saveDefinitionsButtonOnClick()
	{
		sendOutput(SAVE_BUTTON_CLICK_SOCKET, "");
	}

	@ViewEvent(componentID = REFRESH_BUTTON_ID, eventName = Events.ON_CLICK)
	public void refreshButtonOnClick()
	{
		if (unsavedChangesPresent())
		{
			handleRefreshConfirmation();
		}
		else
		{
			doRefresh();
		}
	}

	protected boolean isSaveDefinitionsButtonDisabled()
	{
		return saveDefinitionsButton.isDisabled();
	}

	protected void setSaveDefinitionsButtonState(final boolean state)
	{
		saveDefinitionsButton.setDisabled(state);
	}

	protected void setFilterButtonState(final boolean state)
	{
		filterButton.setDisabled(state);
	}

	protected abstract boolean unsavedChangesPresent();

	protected abstract void handleRefreshConfirmation();

	protected void showRefreshConfirmation(final String titleKey, final String messageKey)
	{
		final Messagebox.Button[] buttons = { Messagebox.Button.YES, Messagebox.Button.NO };
		Messagebox.show(getLabel(messageKey), getLabel(titleKey), buttons, null, clickEvent -> {
			if (clickEvent.getButton() == Messagebox.Button.YES)
			{
				doRefresh();
			}
		});
	}

	protected void doRefresh()
	{
		saveDefinitionsButton.setDisabled(true);
		sendOutput(REFRESH_BUTTON_CLICK_SOCKET, "");
	}

	protected Menupopup createFilterMenupopup()
	{
		final List<String> labels = new ArrayList<>();
		labels.add(getLabel(SHOWALL_MENUITEM_LABEL_KEY));
		labels.add(getLabel(ONLYSELECTED_MENUITEM_LABEL_KEY));

		final Menupopup menuPopup = EditorUtils.createMenuPopup(labels);
		menuPopup.setSclass(FILTER_MENU_SCLASS);
		showAllMenuItem = (Menuitem) menuPopup.getFirstChild();
		onlySelectedMenuItem = (Menuitem) showAllMenuItem.getNextSibling();
		showAllMenuItem.setCheckmark(true);
		onlySelectedMenuItem.setCheckmark(true);
		showAllMenuItem.setChecked(true);

		addMenuItemEvents();
		return menuPopup;
	}

	private void addMenuItemEvents()
	{
		showAllMenuItem.addEventListener(Events.ON_CLICK, event -> {
			if (!showAllMenuItem.isChecked())
			{
				showAllMenuItem.setChecked(true);
				onlySelectedMenuItem.setChecked(false);
				sendOutput(FILTER_STATE_OUT_SOCKET, IntegrationFilterState.SHOW_ALL);
			}
		});
		onlySelectedMenuItem.addEventListener(Events.ON_CLICK, event -> {
			if (!onlySelectedMenuItem.isChecked())
			{
				onlySelectedMenuItem.setChecked(true);
				showAllMenuItem.setChecked(false);
				sendOutput(FILTER_STATE_OUT_SOCKET, IntegrationFilterState.SELECTED);
			}
		});
	}
}
