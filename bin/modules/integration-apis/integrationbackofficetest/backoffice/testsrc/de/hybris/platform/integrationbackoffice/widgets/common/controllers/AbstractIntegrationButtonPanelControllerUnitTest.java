/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.ENABLE_SAVE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.FILTER_BUTTON_SCLASS;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.FILTER_STATE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.FILTER_STATE_OUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.LOAD_OBJECT_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.REFRESH_BUTTON_CLICK_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.SAVE_BUTTON_CLICK_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.SAVE_DEFINITIONS_BUTTON_ID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;

import org.junit.Test;
import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;

public abstract class AbstractIntegrationButtonPanelControllerUnitTest<T extends AbstractIntegrationButtonPanelController>
		extends AbstractWidgetUnitTest<AbstractIntegrationButtonPanelController>
{
	private AbstractIntegrationButtonPanelController controller;

	public void setup()
	{
		this.controller = getWidgetController();
		this.controller.saveDefinitionsButton = new org.zkoss.zul.Button();
		this.controller.filterButtonDiv = new Div();
		this.controller.initialize(mock(Component.class));
	}

	protected org.zkoss.zul.Button saveDefinitionsButton()
	{
		return controller.saveDefinitionsButton;
	}

	protected Div filterButtonDiv()
	{
		return controller.filterButtonDiv;
	}

	protected Button filterButton()
	{
		return controller.filterButton;
	}

	protected Menupopup filterMenupopup()
	{
		return controller.filterMenupopup;
	}

	protected Menuitem showAllMenuItem()
	{
		return controller.showAllMenuItem;
	}

	protected Menuitem onlySelectedMenuItem()
	{
		return controller.onlySelectedMenuItem;
	}

	@Test
	public void buttonsInitializedWhenWidgetControllerInitialized()
	{
		assertTrue("Save button is disabled.", saveDefinitionsButton().isDisabled());
		assertEquals("Filter button div has filter button as child.", filterButton(), filterButtonDiv().getFirstChild());
		assertTrue("Filter button is disabled.", filterButton().isDisabled());
		assertEquals("Filter button has proper CSS class.", FILTER_BUTTON_SCLASS, filterButton().getSclass());
		assertEquals("Filter button has filter menu as child.", filterMenupopup(), filterButton().getFirstChild());
	}

	@Test
	public void menupopupItemsInitializedWhenWidgetControllerInitialized()
	{
		final Component firstChild = filterMenupopup().getFirstChild();
		final Component secondChild = firstChild.getNextSibling();
		assertEquals("Filter menu has show all menu item as child.", showAllMenuItem(), firstChild);
		assertEquals("Filter menu has show only selected menu item as child.", onlySelectedMenuItem(), secondChild);
		assertTrue("Show all menu item is checkable.", showAllMenuItem().isCheckmark());
		assertTrue("Only selected menu item is checkable.", onlySelectedMenuItem().isCheckmark());
		assertTrue("Show all menu item is checked.", showAllMenuItem().isChecked());
		assertFalse("Only selected menu item is not checked.", onlySelectedMenuItem().isChecked());
	}

	@Test
	public void filterMenupopupOpenedWhenFilterButtonClicked() throws Exception
	{
		// setup
		final AbstractIntegrationButtonPanelController spyController = spy(controller);
		final Menupopup filterMenupopup = spy(Menupopup.class);
		doReturn(filterMenupopup).when(spyController).createFilterMenupopup();
		spyController.initialize(mock(Component.class));

		// test
		spyController.filterButton.getEventListeners(Events.ON_CLICK).iterator().next().onEvent(null);
		verify(filterMenupopup, times(1)).open(spyController.filterButton,
				AbstractIntegrationButtonPanelController.MENUPOPUP_POSITION);
	}

	@Test
	public void showAllMenuItemCheckedAndSocketOutputNotSentWhenShowAllMenuItemClickedAndChecked() throws Exception
	{
		showAllMenuItem().setChecked(true);
		onlySelectedMenuItem().setChecked(false);
		showAllEventListener().onEvent(null);
		assertTrue("Show all menu item remains checked.",
				showAllMenuItem().isChecked() && !onlySelectedMenuItem().isChecked());
		assertNoSocketOutputInteractions(FILTER_STATE_OUT_SOCKET);
	}

	@Test
	public void showAllMenuItemCheckedAndSocketOutputSentWhenShowAllMenuItemClickedAndNotChecked() throws Exception
	{
		showAllMenuItem().setChecked(false);
		onlySelectedMenuItem().setChecked(true);
		showAllEventListener().onEvent(null);
		assertTrue("Show all menu item is now checked.",
				showAllMenuItem().isChecked() && !onlySelectedMenuItem().isChecked());
		assertSocketOutput(FILTER_STATE_OUT_SOCKET, 1, IntegrationFilterState.SHOW_ALL);
	}

	@Test
	public void onlySelectedMenuItemCheckedAndSocketOutputNotSentWhenOnlySelectedMenuItemClickedAndChecked() throws Exception
	{
		showAllMenuItem().setChecked(false);
		onlySelectedMenuItem().setChecked(true);
		onlySelectedEventListener().onEvent(null);
		assertTrue("Only selected menu item remains checked.",
				!showAllMenuItem().isChecked() && onlySelectedMenuItem().isChecked());
		assertNoSocketOutputInteractions(FILTER_STATE_OUT_SOCKET);
	}

	@Test
	public void onlySelectedMenuItemCheckedAndSocketOutputSentWhenOnlySelectedMenuItemClickedAndNotChecked() throws Exception
	{
		showAllMenuItem().setChecked(true);
		onlySelectedMenuItem().setChecked(false);
		onlySelectedEventListener().onEvent(null);
		assertTrue("Only selected menu item is now checked.",
				!showAllMenuItem().isChecked() && onlySelectedMenuItem().isChecked());
		assertSocketOutput(FILTER_STATE_OUT_SOCKET, 1, IntegrationFilterState.SELECTED);
	}

	@Test
	public void saveButtonDisabledAndFilterButtonEnabledWhenLoadObjectSocketInputReceived()
	{
		executeInputSocketEvent(LOAD_OBJECT_IN_SOCKET, mock(Object.class));
		assertTrue("Save definitions button is disabled.", saveDefinitionsButton().isDisabled());
		assertFalse("Filter button is enabled.", filterButton().isDisabled());
	}

	@Test
	public void saveDefinitionsButtonIsEnabledWhenEnableSaveSocketInputIsTrue()
	{
		saveDefinitionsButton().setDisabled(true);
		executeInputSocketEvent(ENABLE_SAVE_IN_SOCKET, true);
		assertFalse("Save definitions button is enabled.", saveDefinitionsButton().isDisabled());
	}

	@Test
	public void saveDefinitionsButtonIsDisabledWhenEnableSaveSocketInputIsFalse()
	{
		saveDefinitionsButton().setDisabled(false);
		executeInputSocketEvent(ENABLE_SAVE_IN_SOCKET, false);
		assertTrue("Save definitions button is disabled.", saveDefinitionsButton().isDisabled());
	}

	@Test
	public void saveDefinitionsButtonUnchangedWhenEnableSaveSocketInputIsNull()
	{
		final boolean initialState = saveDefinitionsButton().isDisabled();
		executeInputSocketEvent(ENABLE_SAVE_IN_SOCKET, (Object) null);
		assertEquals("Save definitions button unchanged.", initialState, saveDefinitionsButton().isDisabled());
	}

	@Test
	public void showAllMenuItemCheckedWhenFilterStateSocketInputIsShowAll()
	{
		showAllMenuItem().setChecked(false);
		onlySelectedMenuItem().setChecked(true);
		executeInputSocketEvent(FILTER_STATE_IN_SOCKET, IntegrationFilterState.SHOW_ALL);
		assertTrue("Show all menu item is checked.", showAllMenuItem().isChecked() && !onlySelectedMenuItem().isChecked());
	}

	@Test
	public void onlySelectedMenuItemCheckedWhenFilterStateSocketInputIsOnlySelected()
	{
		showAllMenuItem().setChecked(true);
		onlySelectedMenuItem().setChecked(false);
		executeInputSocketEvent(FILTER_STATE_IN_SOCKET, IntegrationFilterState.SELECTED);
		assertTrue("Only selected menu item is checked.", !showAllMenuItem().isChecked() && onlySelectedMenuItem().isChecked());
	}

	@Test
	public void menuItemsUnchangedWhenFilterStateSocketInputIsNull()
	{
		showAllMenuItem().setChecked(true);
		onlySelectedMenuItem().setChecked(false);
		executeInputSocketEvent(FILTER_STATE_IN_SOCKET, (Object) null);
		assertTrue("Menu items unchanged.", showAllMenuItem().isChecked() && !onlySelectedMenuItem().isChecked());
	}

	@Test
	public void saveButtonSocketOutputWhenSaveDefinitionsButtonOnClickViewEvent()
	{
		executeViewEvent(SAVE_DEFINITIONS_BUTTON_ID, Events.ON_CLICK);
		assertSocketOutput(SAVE_BUTTON_CLICK_SOCKET, 1, "");
	}

	@Test
	public void doRefreshWhenRefreshButtonOnClickViewEventAndNoChangesPresent()
	{
		final AbstractIntegrationButtonPanelController spyController = spy(controller);
		lenient().when(spyController.unsavedChangesPresent()).thenReturn(false);
		spyController.refreshButtonOnClick();
		verify(spyController, times(0)).handleRefreshConfirmation();
		verify(spyController, times(1)).doRefresh();
	}

	@Test
	public void askConfirmationWhenRefreshButtonOnClickViewEventAndChangesPresent()
	{
		final AbstractIntegrationButtonPanelController spyController = spy(controller);
		lenient().when(spyController.unsavedChangesPresent()).thenReturn(true);
		doNothing().when(spyController).showRefreshConfirmation(anyString(), anyString());
		spyController.refreshButtonOnClick();
		verify(spyController, times(1)).handleRefreshConfirmation();
		verify(spyController, times(0)).doRefresh();
	}

	@Test
	public void saveDefinitionsButtonDisabledAndRefreshButtonSocketOutputSentWhenDoRefresh()
	{
		controller.doRefresh();
		assertTrue("Save definitions button is disabled.", saveDefinitionsButton().isDisabled());
		assertSocketOutput(REFRESH_BUTTON_CLICK_SOCKET, 1, "");
	}

	private EventListener<? extends Event> showAllEventListener()
	{
		return showAllMenuItem().getEventListeners(Events.ON_CLICK).iterator().next();
	}

	private EventListener<? extends Event> onlySelectedEventListener()
	{
		return onlySelectedMenuItem().getEventListeners(Events.ON_CLICK).iterator().next();
	}
}
