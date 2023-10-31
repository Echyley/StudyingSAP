/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.masterdetail;

import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.CANCEL_BUTTON;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.CLOSE_BUTTON;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.SAVE_AND_CLOSE_BUTTON;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.SAVE_BUTTON;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.SOCKET_INPUT_VIEW_SWITCHED;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.SOCKET_OUTPUT_CLOSE;
import static com.hybris.backoffice.widgets.masterdetail.AbstractMasterDetailController.SOCKET_OUTPUT_SELECT_VIEW;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import com.hybris.backoffice.masterdetail.MasterDetailService;
import com.hybris.backoffice.masterdetail.SettingButton;
import com.hybris.backoffice.masterdetail.SettingItem;
import com.hybris.backoffice.widgets.viewswitcher.ViewsSwitchedData;
import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.core.ui.WidgetInstance;
import com.hybris.cockpitng.engine.impl.ListContainerCloseListener;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;


@DeclaredInput(value = SOCKET_INPUT_VIEW_SWITCHED, socketType = ViewsSwitchedData.class)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = SAVE_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = SAVE_AND_CLOSE_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CANCEL_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CLOSE_BUTTON)
public class UserProfileSettingsControllerTest extends AbstractWidgetUnitTest<UserProfileSettingsController>
{

	@Mock
	protected MasterDetailService userProfileSettingService;
	@Mock
	private ListContainerCloseListener listContainerCloseListener;
	@Mock
	protected Button saveButton;
	@Mock
	protected Button saveAndCloseButton;
	@Mock
	protected Button cancelButton;
	@Mock
	protected Button closeButton;
	@Mock
	protected Div itemContainer;

	@Spy
	@InjectMocks
	private UserProfileSettingsController controller;

	@Test
	public void shouldRegisterSelfToMasterDetailServiceWhenInit()
	{
		controller.initialize(mock(Component.class));
		verify(userProfileSettingService).registerMaster(controller);
	}

	@Test
	public void shouldNotRenderItemIfNoRegisteredDetailExist()
	{
		controller.addItems(null);
		verify(itemContainer, never()).appendChild(any());
	}

	@Test
	public void shouldNotRenderItemIfGivingEmptyDetailList()
	{
		controller.addItems(Collections.emptyList());
		verify(itemContainer, never()).appendChild(any());
	}

	@Test
	public void shouldSelectFirstViewIfNoActiveViewExistWhenRenderItems()
	{
		final String id1 = "id1";
		final List<SettingItem> settingItemList = Arrays.asList(mockSettingItem(id1), mockSettingItem("id2"));
		controller.addItems(settingItemList);
		verify(itemContainer, times(2)).appendChild(any());
		assertThat(controller.getNavigationItems().size()).isEqualTo(2);
	}

	@Test
	public void shouldSelectActiveViewIfDefaultActiveViewExistWhenRenderItems()
	{
		final String id2 = "id2";
		final SettingItem item2 = mockSettingItem(id2);
		when(item2.isActive()).thenReturn(true);
		final List<SettingItem> settingItemList = Arrays.asList(mockSettingItem("id1"), item2);
		controller.addItems(settingItemList);
		verify(itemContainer, times(2)).appendChild(any());
		assertThat(controller.getNavigationItems().size()).isEqualTo(2);
	}

	@Test
	public void shouldNotSwitchViewIfNotifyNoneExistView()
	{
		final String id = "id";
		final SettingItem item = mockSettingItem(id);
		final List<SettingButton> btns = Arrays.asList(mock(SettingButton.class));
		when(item.getButtons()).thenReturn(btns);
		controller.addItems(Arrays.asList(item));
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		when(viewsSwitchedData.getSelectedViews()).thenReturn(Arrays.asList("id3"));
		controller.setCurrentNavItem(null);
		doNothing().when(controller).initialButtons(btns);

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, viewsSwitchedData);

		assertThat(controller.getCurrentNavItem().getId()).isEqualTo(id);
	}

	@Test
	public void shouldNotSwitchViewIfNotifyNoSwitchedView()
	{
		final String id = "id";
		final SettingItem item = mockSettingItem(id);
		final List<SettingButton> btns = Arrays.asList(mock(SettingButton.class));
		when(item.getButtons()).thenReturn(btns);
		controller.addItems(Arrays.asList(item));
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		when(viewsSwitchedData.getSelectedViews()).thenReturn(Collections.emptyList());
		controller.setCurrentNavItem(null);
		doNothing().when(controller).initialButtons(btns);

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, viewsSwitchedData);

		assertThat(controller.getCurrentNavItem()).isNull();
		verify(controller, never()).initialButtons(btns);
	}

	@Test
	public void shouldDoNothingWhenViewsNavigationItemsIsEmpty()
	{
		final NavigationItem navItem = mock(NavigationItem.class);
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		controller.setCurrentNavItem(navItem);
		when(controller.getNavigationItems()).thenReturn(Collections.emptyList());

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, viewsSwitchedData);

		verify(navItem, never()).removeSelectedStyle();
		verify(userProfileSettingService, never()).resetDetail(navItem.getId());
	}

	@Test
	public void shouldDoNothingWhenViewsSwitchedDataIsNull()
	{
		final String id1 = "id1";
		final SettingItem item1 = mockSettingItem(id1);
		final NavigationItem navItem = mock(NavigationItem.class);
		final List<SettingItem> settingItemList = Arrays.asList(item1);
		controller.addItems(settingItemList);
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		controller.setCurrentNavItem(navItem);

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, (Object) null);

		verify(navItem, never()).removeSelectedStyle();
		verify(userProfileSettingService, never()).resetDetail(navItem.getId());
	}

	@Test
	public void shouldSwitchViewWhenNotifyViewSwitched()
	{
		final String id1 = "id1";
		final String id2 = "id2";
		final SettingItem item1 = mockSettingItem(id1);
		final SettingItem item2 = mockSettingItem(id2);
		final List<SettingButton> btns = Arrays.asList(mock(SettingButton.class));
		final List<SettingItem> settingItemList = Arrays.asList(item1, item2);
		when(item1.getButtons()).thenReturn(btns);
		controller.addItems(settingItemList);
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		when(viewsSwitchedData.getSelectedViews()).thenReturn(Arrays.asList(id1, id2));
		controller.setCurrentNavItem(null);
		doNothing().when(controller).initialButtons(btns);

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, viewsSwitchedData);

		assertThat(controller.getCurrentNavItem().getId()).isEqualTo(id1);
		verify(controller).initialButtons(btns);
	}

	@Test
	public void shouldRedirectToDefaultViewWhenNotifyViewSwitchedButNavigationItemIsNull()
	{
		final String id1 = "id1";
		final String id2 = "id2";
		final String id3 = "id3";
		final SettingItem item1 = mockSettingItem(id1);
		final SettingItem item2 = mockSettingItem(id2);
		final List<SettingButton> btns = Arrays.asList(mock(SettingButton.class));
		final List<SettingItem> settingItemList = Arrays.asList(item1, item2);
		final NavigationItem navItem = mock(NavigationItem.class);
		when(item1.getButtons()).thenReturn(btns);
		controller.addItems(settingItemList);
		final ViewsSwitchedData viewsSwitchedData = mock(ViewsSwitchedData.class);
		when(viewsSwitchedData.getSelectedViews()).thenReturn(Arrays.asList(id3));
		controller.setCurrentNavItem(null);
		doNothing().when(controller).initialButtons(btns);

		executeInputSocketEvent(SOCKET_INPUT_VIEW_SWITCHED, viewsSwitchedData);

		assertThat(controller.getCurrentNavItem().getId()).isEqualTo(id1);
	}

	@Test
	public void shouldHideAllBtnIfNoButtonConfigured()
	{
		doNothing().when(controller).hideAllButtons();
		controller.initialButtons(Collections.emptyList());
		verify(controller).hideAllButtons();
	}

	@Test
	public void shouldHideAllBtnIfConfiguredEmptyButtons()
	{
		doNothing().when(controller).hideAllButtons();
		controller.initialButtons(Collections.emptyList());
		verify(controller).hideAllButtons();
	}

	@Test
	public void shouldInitialCorrectButtonState()
	{
		doNothing().when(controller).hideAllButtons();
		controller.initialButtons(
				Arrays.asList(new SettingButton.Builder().setType(SettingButton.TypesEnum.SAVE).setDisabled(true).build(),
						new SettingButton.Builder().setType(SettingButton.TypesEnum.SAVE_AND_CLOSE).setDisabled(true).build(),
						new SettingButton.Builder().setType(SettingButton.TypesEnum.CANCEL).build()));
		verify(controller).hideAllButtons();
		verify(saveButton).setDisabled(true);
		verify(saveButton).setVisible(true);

		verify(saveAndCloseButton).setDisabled(true);
		verify(saveAndCloseButton).setVisible(true);

		verify(cancelButton).setDisabled(false);
		verify(cancelButton).setVisible(true);

		verify(closeButton, never()).setDisabled(false);
		verify(closeButton, never()).setVisible(true);
	}

	@Test
	public void shouldEnableSaveBtnWhenNotifyEnableSave()
	{
		controller.enableSave(true);
		verify(saveButton).setDisabled(false);
		verify(saveAndCloseButton).setDisabled(false);
	}

	@Test
	public void shouldDisableSaveBtnWhenNotifyDisableSave()
	{
		controller.enableSave(false);
		verify(saveButton).setDisabled(true);
		verify(saveAndCloseButton).setDisabled(true);
	}

	@Test
	public void shouldUpdateCurrentItemWhenNotifyItemUpdated()
	{
		final NavigationItem navItem = mock(NavigationItem.class);
		final SettingItem settingItem = mock(SettingItem.class);
		controller.setCurrentNavItem(navItem);
		controller.updateItem(settingItem);
		verify(navItem).updateItem(settingItem);
	}

	@Test
	public void shouldPerformSaveWithoutCloseWhenClickSaveBtn()
	{
		doNothing().when(controller).performSave(false);
		executeViewEvent(SAVE_BUTTON, Events.ON_CLICK);
		verify(controller).performSave(false);
	}

	@Test
	public void shouldPerformSaveWithCloseWhenClickSaveAndCloseBtn()
	{
		doNothing().when(controller).performSave(true);
		executeViewEvent(SAVE_AND_CLOSE_BUTTON, Events.ON_CLICK);
		verify(controller).performSave(true);
	}

	@Test
	public void shouldNotSaveDetailWhenCurrentNoSelectedItem()
	{
		controller.setCurrentNavItem(null);
		controller.performSave(false);
		controller.performSave(true);
		verify(userProfileSettingService, never()).saveDetail(any());
	}

	@Test
	public void shouldCheckIfNeedRefreshUIBeforeSave()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(false);
		when(userProfileSettingService.saveDetail(id)).thenReturn(true);
		controller.setCurrentNavItem(navItem);

		controller.performSave(false);

		final InOrder order = inOrder(userProfileSettingService);
		order.verify(userProfileSettingService).needRefreshUI(id);
		order.verify(userProfileSettingService).saveDetail(id);
	}

	@Test
	public void shouldOnlySaveDetailWhenPerformSaveWithoutCloseAndRefresh()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(false);
		when(userProfileSettingService.saveDetail(id)).thenReturn(true);
		controller.setCurrentNavItem(navItem);

		controller.performSave(false);

		verify(userProfileSettingService).saveDetail(id);
		verify(controller, never()).doRefreshTheUI();
		verify(controller, never()).onClose();
	}

	@Test
	public void shouldSaveDetailAndCloseWindowWhenPerformSaveAndCloseWithoutRefresh()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(false);
		when(userProfileSettingService.saveDetail(id)).thenReturn(true);
		controller.setCurrentNavItem(navItem);

		controller.performSave(true);

		verify(userProfileSettingService).saveDetail(id);
		verify(controller, never()).doRefreshTheUI();
		verify(controller, times(1)).onClose();
	}

	@Test
	public void shouldSaveDetailAndRefreshWithoutCloseWhenPerformSaveWithRefresh()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(true);
		when(userProfileSettingService.saveDetail(id)).thenReturn(true);
		controller.setCurrentNavItem(navItem);
		doNothing().when(controller).doRefreshTheUI();

		controller.performSave(false);

		verify(userProfileSettingService).saveDetail(id);
		verify(controller, never()).onClose();
		verify(controller, times(1)).doRefreshTheUI();
	}

	@Test
	public void shouldSaveDetailAndRefreshWithCloseWhenPerformSaveAndCloseWithRefresh()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(true);
		when(userProfileSettingService.saveDetail(id)).thenReturn(true);
		doNothing().when(controller).doRefreshTheUI();
		final Window window = mock(Window.class);
		final Widgetslot slot = mock(Widgetslot.class);
		final WidgetInstance widgetInstance = mock(WidgetInstance.class);
		doReturn(Optional.of(window)).when(controller).findTemplateWindow();
		when(controller.getWidgetslot()).thenReturn(slot);
		when(slot.getWidgetInstance()).thenReturn(widgetInstance);
		controller.setCurrentNavItem(navItem);

		controller.performSave(true);

		verify(userProfileSettingService).saveDetail(id);
		verify(listContainerCloseListener).onClose(any(), eq(widgetInstance));
		verify(window).onClose();
		verify(controller).doRefreshTheUI();
		verify(controller, never()).onClose();
	}

	@Test
	public void shouldNotCloseOrRefreshWhenSaveDetailFailure()
	{
		final String id = "id";
		final NavigationItem navItem = mock(NavigationItem.class);
		when(navItem.getId()).thenReturn(id);
		when(userProfileSettingService.needRefreshUI(id)).thenReturn(true);
		when(userProfileSettingService.saveDetail(id)).thenReturn(false);
		final Window window = mock(Window.class);
		doReturn(Optional.of(window)).when(controller).findTemplateWindow();
		controller.setCurrentNavItem(navItem);

		controller.performSave(true);

		verify(userProfileSettingService).saveDetail(id);
		verify(window, never()).onClose();
		verify(controller, never()).doRefreshTheUI();
		verify(controller, never()).onClose();
	}

	@Test
	public void shouldCheckIfDataChangedWhenClickCancelBtn()
	{
		doNothing().when(controller).confirmBeforeLeavingView(controller::onClose);
		executeViewEvent(CANCEL_BUTTON, Events.ON_CLICK);
		verify(controller).confirmBeforeLeavingView(any());
	}

	@Test
	public void shouldResetMDServiceAndSendCloseSocketWhenClickCloseBtn()
	{
		executeViewEvent(CLOSE_BUTTON, Events.ON_CLICK);
		verify(userProfileSettingService).reset();
		assertSocketOutput(SOCKET_OUTPUT_CLOSE, (Object) null);
	}

	@Override
	protected UserProfileSettingsController getWidgetController()
	{
		return controller;
	}

	private SettingItem mockSettingItem(final String id)
	{
		final SettingItem settingItem = mock(SettingItem.class);
		when(settingItem.getId()).thenReturn(id);
		return settingItem;
	}
}
