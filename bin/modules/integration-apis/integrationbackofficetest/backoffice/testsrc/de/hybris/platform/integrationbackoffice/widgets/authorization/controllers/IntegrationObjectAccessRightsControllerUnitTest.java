/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.authorization.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredInput(value = "receiveIntegrationObjectComboBox", socketType = IntegrationObjectModel.class)
@DeclaredInput(value = "queryFromSearchBar", socketType = String.class)
@NullSafeWidget(false)
public class IntegrationObjectAccessRightsControllerUnitTest
		extends AbstractWidgetUnitTest<IntegrationObjectAccessRightsController>
{
	@Mock(lenient = true)
	protected Listbox listPaneListbox;
	@Mock(lenient = true)
	protected Listhead listPaneListhead;
	@Mock(lenient = true)
	protected Listheader listPaneListheader;
	@Spy
	@InjectMocks
	private IntegrationObjectAccessRightsController controller;

	@Override
	protected IntegrationObjectAccessRightsController getWidgetController()
	{
		return controller;
	}

	@Test
	public void testHandleItemCreatedEvent()
	{
		final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
		final CockpitEvent globalEvent = new DefaultCockpitEvent(ObjectFacade.OBJECT_CREATED_EVENT, object, null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, globalEvent);
		verify(controller).setSelectedIO(object);

		// clear count
		reset(controller);
		final String eventData = "eventDataHaveToBeIOForTheController";
		final CockpitEvent globalEvent2 = new DefaultCockpitEvent(ObjectFacade.OBJECT_CREATED_EVENT, eventData, null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, globalEvent2);
		verify(controller, never()).setSelectedIO(Mockito.anyObject());

		reset(controller);
		final CockpitEvent globalEvent3 = new DefaultCockpitEvent(ObjectFacade.OBJECT_CREATED_EVENT, List.of(object), null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, globalEvent3);
		verify(controller, never()).setSelectedIO(Mockito.anyObject());
	}

	@Test
	public void testHandleItemUpdatedEvent()
	{
		final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
		final CockpitEvent globalEvent = new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, object, null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION, globalEvent);
		verify(controller).setSelectedIO(object);

		// clear count
		reset(controller);
		final String eventData = "eventDataHaveToBeIOForTheController";
		final CockpitEvent globalEvent2 = new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, eventData, null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION, globalEvent2);
		verify(controller, never()).setSelectedIO(Mockito.anyObject());

		reset(controller);
		final CockpitEvent globalEvent3 = new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, List.of(object), null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION, globalEvent3);
		verify(controller, never()).setSelectedIO(Mockito.anyObject());
	}

	@Test
	public void testHandleItemDeletedEvent()
	{
		WidgetInstanceManager manager = mock(WidgetInstanceManager.class);
		controller.setWidgetInstanceManager(manager);
		final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
		final CockpitEvent globalEvent = new DefaultCockpitEvent(ObjectFacade.OBJECTS_DELETED_EVENT, object, null);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, globalEvent);
		verify(manager).sendOutput("principalPermissionInfos", null);
	}

	@Test
	public void testSetSelectedIO()
	{
		final IntegrationObjectModel object = mock(IntegrationObjectModel.class);
		executeInputSocketEvent("receiveIntegrationObjectComboBox", object);
		verify(controller).setSelectedIO(object);
	}

}
