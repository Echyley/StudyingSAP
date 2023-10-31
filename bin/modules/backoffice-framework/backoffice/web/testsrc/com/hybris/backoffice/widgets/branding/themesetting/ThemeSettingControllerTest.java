/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.themesetting;

import static com.hybris.backoffice.widgets.branding.themesetting.ThemeSettingController.CANCEL_BUTTON;
import static com.hybris.backoffice.widgets.branding.themesetting.ThemeSettingController.SAVE_BUTTON;
import static com.hybris.backoffice.widgets.branding.themesetting.ThemeSettingController.SOCKET_INPUT_CUSTOM_THEME_CHANGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import com.hybris.backoffice.model.CustomThemeModel;
import com.hybris.backoffice.model.ThemeModel;
import com.hybris.backoffice.theme.BackofficeThemeLevel;
import com.hybris.backoffice.theme.BackofficeThemeService;
import com.hybris.backoffice.theme.ThemeNotFound;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;


@DeclaredInput(value = SOCKET_INPUT_CUSTOM_THEME_CHANGED)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = SAVE_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CANCEL_BUTTON)
public class ThemeSettingControllerTest extends AbstractWidgetUnitTest<ThemeSettingController>
{
	@Mock
	private UserService userService;
	@Mock
	private BackofficeThemeService backofficeThemeService;
	@Mock
	private Listbox systemThemeList;
	@Mock
	private Listbox userThemeList;
	@Mock
	private NotificationService notificationService;
	@Mock
	private Div systemThemeContent;
	@Mock
	private Div userThemeContent;
	@Mock
	protected Radiogroup themeLevelRadioGroup;
	@Mock
	private Radio systemRadio;
	@Mock
	private Radio userRadio;
	@Mock
	protected Button saveButton;
	@Mock
	protected Button cancelButton;

	@Spy
	@InjectMocks
	private ThemeSettingController controller;

	private final ThemeModel darkTheme = new ThemeModel();
	private final ThemeModel lightTheme = new ThemeModel();

	@Before
	public void before()
	{
		final var currentUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(userService.isAdmin(currentUser)).thenReturn(true);
		darkTheme.setCode("sap_quartz_dark");
		lightTheme.setCode("sap_quartz_light");
	}

	@Test
	public void shouldSetCorrectListModelWhenInitialize()
	{
		doNothing().when(controller).reset();

		controller.initialize(mock(Component.class));

		verify(systemThemeList).setModel(controller.availableSystemThemes);
		verify(userThemeList).setModel(controller.availableUserThemes);
		verify(controller).reset();
	}

	@Test
	public void shouldResetCorrectDataIfCurrentIsUserLevel()
	{
		final var mockTheme = mock(CustomThemeModel.class);
		final List<ThemeModel> themeModelList = Arrays.asList(darkTheme, lightTheme, mockTheme);
		when(backofficeThemeService.getAvailableThemes()).thenReturn(themeModelList);
		when(backofficeThemeService.getSystemTheme()).thenReturn(lightTheme);
		when(backofficeThemeService.getUserLevelDefaultTheme()).thenReturn(mockTheme);
		when(backofficeThemeService.getThemeLevel()).thenReturn(BackofficeThemeLevel.USER);
		controller.availableSystemThemes = new ListModelList<>();
		controller.availableUserThemes = new ListModelList<>();

		controller.reset();

		assertThat(controller.currentThemeLevel).isEqualTo(BackofficeThemeLevel.USER);
		assertThat(controller.currentSystemTheme).isEqualTo(lightTheme);
		assertThat(controller.currentUserTheme).isEqualTo(mockTheme);
		assertThat(controller.availableSystemThemes).isEqualTo(themeModelList);
		assertThat(controller.availableUserThemes).isEqualTo(themeModelList);
		assertThat(controller.availableSystemThemes.getSelection().size()).isEqualTo(1);
		assertThat(controller.availableSystemThemes.getSelection().iterator().next()).isEqualTo(lightTheme);
		assertThat(controller.availableUserThemes.getSelection().size()).isEqualTo(1);
		assertThat(controller.availableUserThemes.getSelection().iterator().next()).isEqualTo(mockTheme);

		verify(themeLevelRadioGroup).setSelectedItem(userRadio);
		verify(systemThemeContent).setVisible(false);
		verify(userThemeContent).setVisible(true);
		verify(saveButton).setDisabled(true);
		verify(cancelButton).setDisabled(true);
	}

	@Test
	public void shouldResetCorrectDataIfCurrentIsSystemLevel()
	{
		final var mockTheme = mock(CustomThemeModel.class);
		final List<ThemeModel> themeModelList = Arrays.asList(darkTheme, lightTheme, mockTheme);
		when(backofficeThemeService.getAvailableThemes()).thenReturn(themeModelList);
		when(backofficeThemeService.getSystemTheme()).thenReturn(lightTheme);
		when(backofficeThemeService.getUserLevelDefaultTheme()).thenReturn(mockTheme);
		when(backofficeThemeService.getThemeLevel()).thenReturn(BackofficeThemeLevel.SYSTEM);
		controller.availableSystemThemes = new ListModelList<>();
		controller.availableUserThemes = new ListModelList<>();

		controller.reset();

		assertThat(controller.currentThemeLevel).isEqualTo(BackofficeThemeLevel.SYSTEM);
		assertThat(controller.currentSystemTheme).isEqualTo(lightTheme);
		assertThat(controller.currentUserTheme).isEqualTo(mockTheme);
		assertThat(controller.availableSystemThemes).isEqualTo(themeModelList);
		assertThat(controller.availableUserThemes).isEqualTo(themeModelList);
		assertThat(controller.availableSystemThemes.getSelection().size()).isEqualTo(1);
		assertThat(controller.availableSystemThemes.getSelection().iterator().next()).isEqualTo(lightTheme);
		assertThat(controller.availableUserThemes.getSelection().size()).isEqualTo(1);
		assertThat(controller.availableUserThemes.getSelection().iterator().next()).isEqualTo(mockTheme);

		verify(themeLevelRadioGroup).setSelectedItem(systemRadio);
		verify(systemThemeContent).setVisible(true);
		verify(userThemeContent).setVisible(false);
		verify(saveButton).setDisabled(true);
		verify(cancelButton).setDisabled(true);
	}

	@Test
	public void shouldDisableSaveAndCancelBtnWhenInit()
	{
		//when
		controller.initialize(mock(Component.class));

		//then
		verify(saveButton).setDisabled(true);
		verify(cancelButton).setDisabled(true);
	}

	@Test
	public void shouldHideSystemContentSettingIfCurrentIsUserLevel()
	{
		//given
		when(backofficeThemeService.getThemeLevel()).thenReturn(BackofficeThemeLevel.USER);

		//when
		controller.initialize(mock(Component.class));

		//then
		verify(systemThemeContent).setVisible(false);
		verify(userThemeContent).setVisible(true);
		verify(themeLevelRadioGroup).setSelectedItem(userRadio);

	}

	@Test
	public void shouldShowSystemContentSettingIfCurrentIsSystemLevel()
	{
		//given
		when(backofficeThemeService.getThemeLevel()).thenReturn(BackofficeThemeLevel.SYSTEM);

		//when
		controller.initialize(mock(Component.class));

		//then
		verify(systemThemeContent).setVisible(true);
		verify(userThemeContent).setVisible(false);
		verify(themeLevelRadioGroup).setSelectedItem(systemRadio);
	}

	@Test
	public void shouldNotEnableSaveIfClickSameCurrentSystemTheme()
	{
		//given
		controller.currentSystemTheme = lightTheme;
		controller.currentThemeLevel = BackofficeThemeLevel.SYSTEM;

		//when
		controller.onThemeClick(lightTheme);

		//then
		verify(saveButton, never()).setDisabled(false);
		verify(cancelButton, never()).setDisabled(false);
	}

	@Test
	public void shouldEnableSaveWhenSystemThemeChange()
	{
		//given
		controller.currentSystemTheme = lightTheme;
		controller.currentThemeLevel = BackofficeThemeLevel.SYSTEM;

		//when
		controller.onThemeClick(darkTheme);

		//then
		verify(saveButton).setDisabled(false);
		verify(cancelButton).setDisabled(false);
	}

	@Test
	public void shouldNotEnableSaveIfClickSameCurrentUserTheme()
	{
		//given
		controller.currentUserTheme = lightTheme;
		controller.currentThemeLevel = BackofficeThemeLevel.USER;

		//when
		controller.onThemeClick(lightTheme);

		//then
		verify(saveButton, never()).setDisabled(false);
		verify(cancelButton, never()).setDisabled(false);
	}

	@Test
	public void shouldEnableSaveWhenUserThemeChange()
	{
		//given
		controller.currentUserTheme = lightTheme;
		controller.currentThemeLevel = BackofficeThemeLevel.USER;

		//when
		controller.onThemeClick(darkTheme);

		//then
		verify(saveButton).setDisabled(false);
		verify(cancelButton).setDisabled(false);
	}

	@Test
	public void shouldEnableSaveAndHideSystemContentSettingIfChangeToUserLevel()
	{
		//given

		//when
		controller.onThemeLevelCheck(BackofficeThemeLevel.USER.name());

		//then
		verify(saveButton).setDisabled(false);
		verify(cancelButton).setDisabled(false);
		verify(systemThemeContent).setVisible(false);
		verify(userThemeContent).setVisible(true);
		assertThat(controller.currentThemeLevel).isEqualTo(BackofficeThemeLevel.USER);
	}

	@Test
	public void shouldEnableSaveAndShowSystemContentSettingIfChangeToSystemLevel()
	{
		//when
		controller.onThemeLevelCheck(BackofficeThemeLevel.SYSTEM.name());

		//then
		verify(saveButton).setDisabled(false);
		verify(cancelButton).setDisabled(false);
		verify(systemThemeContent).setVisible(true);
		verify(userThemeContent).setVisible(false);
		assertThat(controller.currentThemeLevel).isEqualTo(BackofficeThemeLevel.SYSTEM);
	}

	@Test
	public void shouldPerformSaveWhenClickSaveBtn()
	{
		doNothing().when(controller).onSave();
		executeViewEvent(SAVE_BUTTON, Events.ON_CLICK);
		verify(controller).onSave();
	}

	@Test
	public void shouldPerformCancelWhenClickCancelBtn()
	{
		doNothing().when(controller).onCancel();
		executeViewEvent(CANCEL_BUTTON, Events.ON_CLICK);
		verify(controller).onCancel();
	}

	@Test
	public void shouldResetDataIfNotifyCustomThemeChanged()
	{
		doNothing().when(controller).reset();
		executeInputSocketEvent(SOCKET_INPUT_CUSTOM_THEME_CHANGED, (Object) null);
		verify(controller).reset();
	}

	@Test
	public void shouldNotSaveThemeForNoneAdminUser() throws ThemeNotFound
	{
		//given
		final var currentUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(userService.isAdmin(currentUser)).thenReturn(false);

		//when
		controller.onSave();

		//then
		verify(backofficeThemeService, never()).setThemeLevel(any());
		verify(backofficeThemeService, never()).setSystemTheme(any());
	}

	@Test
	public void shouldSaveSystemThemeAndRefreshUIWhenSaveSuccess() throws ThemeNotFound
	{
		//given
		controller.currentThemeLevel = BackofficeThemeLevel.SYSTEM;
		final var listItem = mock(Listitem.class);
		when(listItem.getValue()).thenReturn(darkTheme);
		when(systemThemeList.getSelectedItem()).thenReturn(listItem);

		//when
		controller.onSave();

		//then
		verify(backofficeThemeService).setThemeLevel(BackofficeThemeLevel.SYSTEM);
		verify(backofficeThemeService).setSystemTheme(darkTheme.getCode());
		verify(backofficeThemeService, never()).setUserLevelDefaultTheme(any());
		verify(controller).doRefreshTheUI();
		verify(notificationService).notifyUser(ThemeSettingController.NOTIFICATION_AREA,
				ThemeSettingController.NOTIFICATION_TYPE_THEME_CHANGED, NotificationEvent.Level.SUCCESS);
	}

	@Test
	public void shouldSaveUserThemeAndRefreshUIWhenSaveSuccess() throws ThemeNotFound
	{
		//given
		controller.currentThemeLevel = BackofficeThemeLevel.USER;
		final var listItem = mock(Listitem.class);
		when(listItem.getValue()).thenReturn(darkTheme);
		when(userThemeList.getSelectedItem()).thenReturn(listItem);

		//when
		controller.onSave();

		//then
		verify(backofficeThemeService).setThemeLevel(BackofficeThemeLevel.USER);
		verify(backofficeThemeService, never()).setSystemTheme(any());
		verify(backofficeThemeService).setUserLevelDefaultTheme(darkTheme.getCode());
		verify(controller).doRefreshTheUI();
		verify(notificationService).notifyUser(ThemeSettingController.NOTIFICATION_AREA,
				ThemeSettingController.NOTIFICATION_TYPE_THEME_CHANGED, NotificationEvent.Level.SUCCESS);
	}

	@Test
	public void shouldShowErrorMsgAndNotRefreshIfSaveFailure() throws ThemeNotFound
	{
		//given
		controller.currentThemeLevel = BackofficeThemeLevel.SYSTEM;
		final var listItem = mock(Listitem.class);
		when(listItem.getValue()).thenReturn(darkTheme);
		when(systemThemeList.getSelectedItem()).thenReturn(listItem);
		doThrow(ThemeNotFound.class).when(backofficeThemeService).setSystemTheme(darkTheme.getCode());

		//when
		controller.onSave();

		//then
		verify(controller, never()).doRefreshTheUI();
		verify(notificationService).notifyUser(ThemeSettingController.NOTIFICATION_AREA,
				ThemeSettingController.NOTIFICATION_TYPE_THEME_CHANGED, NotificationEvent.Level.FAILURE);
	}

	@Override
	protected ThemeSettingController getWidgetController()
	{
		return controller;
	}

}
