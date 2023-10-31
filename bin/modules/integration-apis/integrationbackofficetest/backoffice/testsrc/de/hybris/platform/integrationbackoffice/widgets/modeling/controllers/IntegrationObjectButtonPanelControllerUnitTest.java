/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.ENABLE_SAVE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.FILTER_STATE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.LOAD_OBJECT_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.REFRESH_BUTTON_ID;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.SAVE_DEFINITIONS_BUTTON_ID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelControllerUnitTest;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.zk.ui.event.Events;

import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;

@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredInput(value = LOAD_OBJECT_IN_SOCKET)
@DeclaredInput(value = FILTER_STATE_IN_SOCKET, socketType = IntegrationFilterState.class)
@DeclaredInput(value = ENABLE_SAVE_IN_SOCKET, socketType = Boolean.class)
@DeclaredViewEvent(componentID = SAVE_DEFINITIONS_BUTTON_ID, eventName = Events.ON_CLICK)
@DeclaredViewEvent(componentID = REFRESH_BUTTON_ID, eventName = Events.ON_CLICK)
public class IntegrationObjectButtonPanelControllerUnitTest
		extends AbstractIntegrationButtonPanelControllerUnitTest<IntegrationObjectButtonPanelController>
{
	private final IntegrationObjectButtonPanelController buttonPanelController = new IntegrationObjectButtonPanelController();

	@Before
	public void setup()
	{
		super.setup();
	}

	@Override
	protected IntegrationObjectButtonPanelController getWidgetController()
	{
		return buttonPanelController;
	}

	@Test
	public void componentsUnchangedWhenObjectCreatedGlobalEventAndEventDoesNotContainIntegrationObject()
	{
		saveDefinitionsButton().setDisabled(false);
		filterButton().setDisabled(true);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, cockpitEvent(new Object()));
		assertFalse("Save definitions button still enabled.", saveDefinitionsButton().isDisabled());
		assertTrue("Filter button still disabled.", filterButton().isDisabled());
	}

	@Test
	public void componentsChangedWhenObjectCreatedGlobalEventAndEventContainsIntegrationObject()
	{
		saveDefinitionsButton().setDisabled(false);
		filterButton().setDisabled(true);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, cockpitEvent(new IntegrationObjectModel()));
		assertTrue("Save definitions button disabled.", saveDefinitionsButton().isDisabled());
		assertFalse("Filter button enabled.", filterButton().isDisabled());
	}

	@Test
	public void componentsUnchanged_WhenObjectDeletedGlobalEventAndEventDoesNotContainIntegrationObject()
	{
		saveDefinitionsButton().setDisabled(false);
		filterButton().setDisabled(false);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, cockpitEvent(new Object()));
		assertFalse("Save definitions button still enabled.", saveDefinitionsButton().isDisabled());
		assertFalse("Filter button still enabled.", filterButton().isDisabled());
	}

	@Test
	public void componentsChangedWhenObjectDeletedGlobalEventAndEventContainsIntegrationObject()
	{
		saveDefinitionsButton().setDisabled(false);
		filterButton().setDisabled(false);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, cockpitEvent(new IntegrationObjectModel()));
		assertTrue("Save definitions button disabled.", saveDefinitionsButton().isDisabled());
		assertTrue("Filter button disabled.", filterButton().isDisabled());
	}

	@Test
	public void unsavedChangesPresentTrueWhenSaveDefinitionsButtonEnabled()
	{
		saveDefinitionsButton().setDisabled(false);
		assertTrue("Unsaved changes present when button enabled.", buttonPanelController.unsavedChangesPresent());
	}

	@Test
	public void unsavedChangesPresentFalseWhenSaveDefinitionsButtonDisabled()
	{
		saveDefinitionsButton().setDisabled(true);
		assertFalse("Unsaved changes not present when button disabled.", buttonPanelController.unsavedChangesPresent());
	}

	private CockpitEvent cockpitEvent(final Object data)
	{
		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		lenient().when(cockpitEvent.getData()).thenReturn(data);
		lenient().when(cockpitEvent.getDataAsCollection()).thenReturn(Set.of(data));
		return cockpitEvent;
	}
}
