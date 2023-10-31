/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.createComboItem;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.IntegrationObjectRootUtils.resolveIntegrationObjectRoot;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationSelectorController;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Collection;
import java.util.stream.Collectors;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;

/**
 * Controller for integrationbackoffice modeling toolbar selector.
 * Enables the selection of an {@link IntegrationObjectModel} to model.
 */
public class IntegrationObjectSelectorController extends AbstractIntegrationSelectorController
{
	public static final String SYNC_COMBOBOX_IN_SOCKET = "receiveSyncNotice";
	public static final String SELECT_ITEM_IN_SOCKET = "selectedItem";
	protected static final String MODEL_KEY_CURRENT_OBJECT = "currentObject";

	@WireVariable
	protected transient ReadService readService;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		resetIntegrationObjectInModel();
	}

	@Override
	protected Collection<Comboitem> buildComboitems()
	{
		return readService.getIntegrationObjectModels()
		                  .stream()
		                  .map(integrationObject -> createComboItem(integrationObject.getCode(), integrationObject))
		                  .collect(Collectors.toList());
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectCreatedEvent(final CockpitEvent event)
	{
		updateCombobox(event);
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectUpdatedEvent(final CockpitEvent event)
	{
		updateCombobox(event);
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectDeletedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			loadCombobox();
			integrationComboBox.setValue(null);
			resetIntegrationObjectInModel();
		}
	}

	@SocketEvent(socketId = SYNC_COMBOBOX_IN_SOCKET)
	public void syncComboBoxes(final IntegrationObjectModel selectedIO)
	{
		final Comboitem comboitem = findComboitem(selectedIO);
		integrationComboBox.setSelectedIndex(comboitem.getIndex());
	}

	@SocketEvent(socketId = SELECT_ITEM_IN_SOCKET)
	public void getSelected(final IntegrationMapKeyDTO selectedComposedTypeInstance)
	{
		storeIntegrationObjectInModel(selectedComposedTypeInstance.getType().getCode());
	}

	@ViewEvent(componentID = COMBOBOX_ID, eventName = Events.ON_CHANGE)
	@Override
	public void integrationComboBoxOnChange()
	{
		if (integrationComboBox.getSelectedItem() != null)
		{
			final IntegrationObjectModel selectedIO = integrationComboBox.getSelectedItem().getValue();
			sendOutput("comboBoxOnChange", resolveIntegrationObjectRoot(selectedIO));
			storeIntegrationObjectInModel(selectedIO);
		}
	}

	private void updateCombobox(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			loadCombobox();
			final IntegrationObjectModel integrationObject = (IntegrationObjectModel) event.getData();
			final Comboitem comboitem = findComboitem(integrationObject);
			integrationComboBox.setSelectedIndex(comboitem.getIndex());
			storeIntegrationObjectInModel(integrationObject);
		}
	}

	private void storeIntegrationObjectInModel(final IntegrationObjectModel integrationObject)
	{
		if (integrationObject == null)
		{
			resetIntegrationObjectInModel();
		}
		else
		{
			storeIntegrationObjectInModel(integrationObject.getRootItem().getType().getCode());
		}
	}

	private void storeIntegrationObjectInModel(final String type)
	{
		getModel().setValue(MODEL_KEY_CURRENT_OBJECT, type);
	}

	private void resetIntegrationObjectInModel()
	{
		getModel().setValue(MODEL_KEY_CURRENT_OBJECT, null);
	}
}
