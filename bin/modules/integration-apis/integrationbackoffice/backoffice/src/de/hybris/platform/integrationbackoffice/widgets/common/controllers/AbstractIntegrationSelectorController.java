/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.components.Actions;
import com.hybris.cockpitng.util.DefaultWidgetController;

/**
 * Controller with common functionality across integrationbackoffice toolbar selector controllers.
 * Contains a combobox for object selection and a reference to custom actions.
 */
public abstract class AbstractIntegrationSelectorController extends DefaultWidgetController
{
	public static final String REFRESH_COMBOBOX_IN_SOCKET = "refreshCombobox";
	public static final String COMBOBOX_ID = "integrationComboBox";
	protected static final String SETTING_ACTIONS_SLOT = "actions";

	@WireVariable
	private transient IntegrationBackofficeEventSender integrationBackofficeEventSender;

	@Wire
	protected Actions actions;

	protected Combobox integrationComboBox;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		initActions();
		loadCombobox();
	}

	@SocketEvent(socketId = REFRESH_COMBOBOX_IN_SOCKET)
	public void refreshCombobox()
	{
		final Comboitem selectedItem = integrationComboBox.getSelectedItem();
		loadCombobox();
		if (selectedItem != null)
		{
			final Comboitem comboitem = findComboitem(selectedItem.getValue());
			sendOnChangeEvent(comboitem);
		}
	}

	@ViewEvent(componentID = COMBOBOX_ID, eventName = Events.ON_CHANGE)
	public abstract void integrationComboBoxOnChange();

	public void loadCombobox()
	{
		clearCombobox();
		buildComboitems().forEach(comboitem -> integrationComboBox.appendChild(comboitem));
	}

	public void sendOnChangeEvent(final Comboitem comboitem)
	{
		integrationBackofficeEventSender.sendEvent(Events.ON_CHANGE, integrationComboBox, comboitem);
	}

	protected abstract Collection<Comboitem> buildComboitems();

	protected void clearCombobox()
	{
		integrationComboBox.getItems().clear();
	}

	protected Comboitem findComboitem(final Object comboitemValue)
	{
		return integrationComboBox.getItems()
		                          .stream()
		                          .filter(item -> item.getValue().equals(comboitemValue))
		                          .findFirst()
		                          .orElseThrow(() -> new NoSuchElementException(
				                          String.format("No item was found with value %s", comboitemValue)));
	}

	private void initActions()
	{
		actions.setConfig(String.format("component=%s", getWidgetSettings().getString(SETTING_ACTIONS_SLOT)));
		actions.reload();
	}
}
