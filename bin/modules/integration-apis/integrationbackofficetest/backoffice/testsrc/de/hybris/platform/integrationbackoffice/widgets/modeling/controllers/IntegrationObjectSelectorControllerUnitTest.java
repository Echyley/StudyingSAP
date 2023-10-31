/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationSelectorController;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationSelectorControllerUnitTest;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationSelectorController.COMBOBOX_ID;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationSelectorController.REFRESH_COMBOBOX_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectSelectorController.MODEL_KEY_CURRENT_OBJECT;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectSelectorController.SELECT_ITEM_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectSelectorController.SYNC_COMBOBOX_IN_SOCKET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredInput(value = REFRESH_COMBOBOX_IN_SOCKET)
@DeclaredInput(value = SYNC_COMBOBOX_IN_SOCKET, socketType = IntegrationObjectModel.class)
@DeclaredInput(value = SELECT_ITEM_IN_SOCKET, socketType = IntegrationMapKeyDTO.class)
@DeclaredViewEvent(componentID = COMBOBOX_ID, eventName = Events.ON_CHANGE)
@NullSafeWidget(false)
public class IntegrationObjectSelectorControllerUnitTest
		extends AbstractIntegrationSelectorControllerUnitTest<IntegrationObjectSelectorController>
{
	private static final IntegrationObjectModel INTEGRATION_OBJECT_MODEL = new IntegrationObjectModel();
	private final IntegrationObjectModel INTEGRATION_OBJECT_MODEL_2 = integrationObject();
	private static final String PRODUCT_CODE = "testProduct";

	@Mock(lenient = true)
	private ReadService readService;
	@Spy
	@InjectMocks
	private IntegrationObjectSelectorController controller;

	@Override
	protected AbstractIntegrationSelectorController getWidgetController()
	{
		return controller;
	}

	@Before
	public void setup()
	{
		lenient().when(readService.getIntegrationObjectModels()).thenReturn(List.of(INTEGRATION_OBJECT_MODEL, INTEGRATION_OBJECT_MODEL_2));
		super.setup();
	}

	@Test
	public void comboboxUnchangedWhenObjectCreatedGlobalEventAndEventDoesNotContainIntegrationObject()
	{
		final Comboitem comboitem = getCombobox().getSelectedItem();
		final int comboboxSize = getCombobox().getItems().size();
		final CockpitEvent event = cockpitEvent(new Object());
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, event);
		verify(controller).handleIntegrationObjectCreatedEvent(event);
		assertEquals("SelectedItem remains the same if newly created Object is not IO", comboitem,
				getCombobox().getSelectedItem());
		assertEquals("Combobox size remains the same if newly created Object is not IO", comboboxSize,
				getCombobox().getItems().size());
	}

	@Test
	public void comboboxChangedWhenObjectCreatedGlobalEventAndEventContainsIntegrationObject()
	{
		final CockpitEvent event = cockpitEvent(INTEGRATION_OBJECT_MODEL_2);
		executeGlobalEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, CockpitEvent.SESSION, event);
		assertEquals("IO in the event should be selected in combobox",
				INTEGRATION_OBJECT_MODEL_2, getCombobox().getSelectedItem().getValue());
		assertEquals(PRODUCT_CODE, controller.getModel().getValue(MODEL_KEY_CURRENT_OBJECT, String.class));
	}

	@Test
	public void comboboxUnchangedWhenObjectUpdatedGlobalEventAndEventDoesNotContainIntegrationObject()
	{
		final Comboitem comboitem = getCombobox().getSelectedItem();
		final int comboboxSize = getCombobox().getItems().size();
		final CockpitEvent event = cockpitEvent(new Object());
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION, event);
		verify(controller).handleIntegrationObjectUpdatedEvent(event);
		assertEquals("SelectedItem remains the same if newly updated Object is not IO", comboitem,
				getCombobox().getSelectedItem());
		assertEquals("Combobox size remains the same if newly updated Object is not IO", comboboxSize,
				getCombobox().getItems().size());
	}

	@Test
	public void comboboxChangedWhenObjectUpdatedGlobalEventAndEventContainsIntegrationObject()
	{
		final CockpitEvent event = cockpitEvent(INTEGRATION_OBJECT_MODEL_2);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION, event);
		assertEquals("IO in the event should be selected in combobox",
				INTEGRATION_OBJECT_MODEL_2, getCombobox().getSelectedItem().getValue());
		assertEquals(PRODUCT_CODE, controller.getModel().getValue(MODEL_KEY_CURRENT_OBJECT, String.class));
	}

	@Test
	public void comboboxUnchangedWhenObjectDeletedGlobalEventAndEventDoesNotContainIntegrationObject()
	{
		final int comboboxSize = getCombobox().getItems().size();
		final CockpitEvent event = cockpitEvent(new Object());
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, event);
		verify(controller).handleIntegrationObjectDeletedEvent(event);
		assertNotNull("SelectedItem won't be null if newly deleted Object is not IO", getCombobox().getSelectedItem());
		assertEquals("Combobox size remains the same if newly deleted Object is not IO", comboboxSize,
				getCombobox().getItems().size());
	}

	@Test
	public void comboboxChangedWhenObjectDeletedGlobalEventAndEventContainsIntegrationObject()
	{
		final CockpitEvent event = cockpitEvent(INTEGRATION_OBJECT_MODEL_2);
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_DELETED_EVENT, CockpitEvent.SESSION, event);
		assertEquals("nothing selected in combobox", "", getCombobox().getValue());
		assertNull(controller.getModel().getValue(MODEL_KEY_CURRENT_OBJECT, Object.class));
	}

	@Test
	public void comboboxChangedOnSyncEvent()
	{
		getCombobox().setSelectedItem(null);
		executeInputSocketEvent(SYNC_COMBOBOX_IN_SOCKET, INTEGRATION_OBJECT_MODEL);
		assertEquals(INTEGRATION_OBJECT_MODEL, getCombobox().getSelectedItem().getValue());
	}

	@Test
	public void codeSavedInModelOnSelectEvent()
	{
		controller.getModel().setValue(MODEL_KEY_CURRENT_OBJECT, "");
		final String code = "test";
		final ComposedTypeModel type = new ComposedTypeModel();
		type.setCode(code);
		final IntegrationMapKeyDTO keyDTO = new IntegrationMapKeyDTO(type, "notMatters");
		executeInputSocketEvent(SELECT_ITEM_IN_SOCKET, keyDTO);
		assertEquals(code, controller.getModel().getValue(MODEL_KEY_CURRENT_OBJECT, String.class));
	}

	@Test
	public void noInteractionsWhenComboboxOnChangeAndNothingSelected()
	{
		getCombobox().getChildren().clear();
		executeViewEvent(COMBOBOX_ID, Events.ON_CHANGE);
		assertNoSocketOutputInteractions("comboBoxOnChange");
	}

	@Test
	public void outputSentWhenComboboxOnChangeAndItemSelected()
	{
		final Comboitem comboitem = new Comboitem();
		comboitem.setValue(INTEGRATION_OBJECT_MODEL_2);
		getCombobox().getItems().add(comboitem);
		getCombobox().setSelectedItem(comboitem);
		executeViewEvent(COMBOBOX_ID, Events.ON_CHANGE);
		assertSocketOutput("comboBoxOnChange", INTEGRATION_OBJECT_MODEL_2);
		assertEquals(PRODUCT_CODE, controller.getModel().getValue(MODEL_KEY_CURRENT_OBJECT, String.class));
	}

	@Test
	public void comboboxInitializedWhenControllerInitialized()
	{
		assertNotNull("ReadService is injected correctly.", controller.readService);
		assertSame("Combobox initialized correctly a list of IO that readServices offers.",
				INTEGRATION_OBJECT_MODEL, getCombobox().getItems().get(0).getValue());
		assertSame("Combobox initialized correctly a list of IO that readServices offers.",
				INTEGRATION_OBJECT_MODEL_2, getCombobox().getItems().get(1).getValue());
	}

	@Test
	public void comboboxLoaded()
	{
		getCombobox().getChildren().clear();
		controller.loadCombobox();
		assertSame("Combobox loaded correctly a list of IO that readServices offers.",
				INTEGRATION_OBJECT_MODEL, getCombobox().getItems().get(0).getValue());
		assertSame("Combobox loaded correctly a list of IO that readServices offers.",
				INTEGRATION_OBJECT_MODEL_2, getCombobox().getItems().get(1).getValue());
	}

	@Test
	public void comboboxBuilt()
	{
		final Collection<Comboitem> comboboxes = controller.buildComboitems();
		assertEquals(INTEGRATION_OBJECT_MODEL, comboboxes.toArray(Comboitem[]::new)[0].getValue());
	}

	private CockpitEvent cockpitEvent(final Object data)
	{
		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		lenient().when(cockpitEvent.getData()).thenReturn(data);
		lenient().when(cockpitEvent.getDataAsCollection()).thenReturn(Set.of(data));
		return cockpitEvent;
	}

	private IntegrationObjectModel integrationObject()
	{
		final String IO_CODE = "testIO";

		final ComposedTypeModel type = mock(ComposedTypeModel.class);
		lenient().doReturn(IntegrationObjectSelectorControllerUnitTest.PRODUCT_CODE).when(type).getCode();

		final IntegrationObjectItemModel iOI = mock(IntegrationObjectItemModel.class);
		lenient().doReturn(type).when(iOI).getType();

		final IntegrationObjectModel iO = mock(IntegrationObjectModel.class);
		lenient().doReturn(IO_CODE).when(iO).getCode();
		lenient().doReturn(iOI).when(iO).getRootItem();

		return iO;
	}

}
