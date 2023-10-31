/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.logosetting;

import static com.hybris.backoffice.widgets.branding.logosetting.LogoSettingController.CANCEL_BUTTON;
import static com.hybris.backoffice.widgets.branding.logosetting.LogoSettingController.SAVE_BUTTON;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import com.hybris.backoffice.media.BackofficeMediaConstants;
import com.hybris.backoffice.media.MediaUtil;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;


@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = SAVE_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CANCEL_BUTTON)
public class LogoSettingControllerTest extends AbstractWidgetUnitTest<LogoSettingController>
{
	@Mock
	private UserService userService;
	@Mock
	private MediaUtil backofficeMediaUtil;
	@Mock
	private NotificationService notificationService;
	@Mock
	protected Div logoSettingDiv;
	@Mock
	protected Button saveButton;
	@Mock
	protected Button cancelButton;

	@Spy
	@InjectMocks
	private LogoSettingController controller;

	@Before
	public void setUp()
	{
		final var currentUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(userService.isAdmin(currentUser)).thenReturn(true);
	}

	@Test
	public void shouldRenderTwoLogoItemWhenInit()
	{
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_LOGINPAGE_LOGO_CODE)).thenReturn(Optional.empty());
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE)).thenReturn(Optional.empty());

		controller.initialize(mock(Component.class));

		assertThat(controller.logoSettingItems.size()).isEqualTo(2);
		final var loginLogo = controller.logoSettingItems.get(0);
		final var shellLogo = controller.logoSettingItems.get(1);
		final var defaultLoginUrl = String.format("%s%s", controller.getWidgetRoot(), "/images/logo_transparent.png");
		final var defaultShellUrl = String.format("%s%s", controller.getWidgetRoot(), "/images/logo-sap.png");
		assertThat(loginLogo.getController()).isEqualTo(controller);
		assertThat(loginLogo.getLogoCode()).isEqualTo(BackofficeMediaConstants.BACKOFFICE_LOGINPAGE_LOGO_CODE);
		assertThat(loginLogo.getDefaultLogoUrl()).isEqualTo(defaultLoginUrl);
		assertThat(loginLogo.getLogoImage().getSrc()).isEqualTo(defaultLoginUrl);

		assertThat(shellLogo.getController()).isEqualTo(controller);
		assertThat(shellLogo.getLogoCode()).isEqualTo(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE);
		assertThat(shellLogo.getDefaultLogoUrl()).isEqualTo(defaultShellUrl);
		assertThat(shellLogo.getLogoImage().getSrc()).isEqualTo(defaultShellUrl);
	}

	@Test
	public void shouldRenderCorrectLogoIfLogoSavedBefore()
	{
		final var loginLogoMedia = mock(MediaModel.class);
		final var shellLogoMedia = mock(MediaModel.class);
		final var loginLogoUrl = "loginLogoUrl";
		final var shellLogoUrl = "shellLogoUrl";
		when(loginLogoMedia.getURL()).thenReturn(loginLogoUrl);
		when(shellLogoMedia.getURL()).thenReturn(shellLogoUrl);
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_LOGINPAGE_LOGO_CODE))
				.thenReturn(Optional.of(loginLogoMedia));
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE))
				.thenReturn(Optional.of(shellLogoMedia));

		controller.initialize(mock(Component.class));

		assertThat(controller.logoSettingItems.size()).isEqualTo(2);
		final var loginLogo = controller.logoSettingItems.get(0);
		final var shellLogo = controller.logoSettingItems.get(1);
		assertThat(loginLogo.getLogoImage().getSrc()).isEqualTo(loginLogoUrl);
		assertThat(shellLogo.getLogoImage().getSrc()).isEqualTo(shellLogoUrl);
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
	public void shouldNotSaveLogoForNoneAdminUser()
	{
		final var currentUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(userService.isAdmin(currentUser)).thenReturn(false);
		final var logoItem = mock(LogoSettingItem.class);
		when(logoItem.isDataChanged()).thenReturn(true);
		controller.logoSettingItems = Arrays.asList(logoItem);

		controller.onSave();

		verify(logoItem, never()).save();
	}


	@Test
	public void shouldReturnFalseIfAnyLogoSavedFailure()
	{
		final var logoItem1 = mock(LogoSettingItem.class);
		final var logoItem2 = mock(LogoSettingItem.class);
		when(logoItem1.isDataChanged()).thenReturn(true);
		when(logoItem2.isDataChanged()).thenReturn(true);
		when(logoItem1.save()).thenReturn(true);
		when(logoItem2.save()).thenReturn(false);
		controller.logoSettingItems = Arrays.asList(logoItem1, logoItem2);

		controller.onSave();

		verify(logoItem1).save();
		verify(logoItem2).save();
		verify(notificationService).notifyUser(LogoSettingController.NOTIFICATION_AREA,
				LogoSettingController.NOTIFICATION_TYPE_LOGO_CHANGED, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldReturnTrueIfAllTheLogoSavedSuccess()
	{
		final var logoItem1 = mock(LogoSettingItem.class);
		final var logoItem2 = mock(LogoSettingItem.class);
		when(logoItem1.isDataChanged()).thenReturn(true);
		when(logoItem2.isDataChanged()).thenReturn(true);
		when(logoItem1.save()).thenReturn(true);
		when(logoItem2.save()).thenReturn(true);
		controller.logoSettingItems = Arrays.asList(logoItem1, logoItem2);

		controller.onSave();

		verify(logoItem1).save();
		verify(logoItem2).save();
		verify(notificationService).notifyUser(LogoSettingController.NOTIFICATION_AREA,
				LogoSettingController.NOTIFICATION_TYPE_LOGO_CHANGED, NotificationEvent.Level.SUCCESS);
	}

	@Test
	public void shouldNotifyShellBarLogoChanged()
	{
		final var media = mock(MediaModel.class);
		controller.onLogoSaved(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE, media);
		assertSocketOutput(LogoSettingController.SOCKET_OUTPUT_LOGO_CHANGED, media);
	}

	@Test
	public void shouldResetData()
	{
		final var logoItem1 = mock(LogoSettingItem.class);
		final var logoItem2 = mock(LogoSettingItem.class);
		when(logoItem1.isDataChanged()).thenReturn(false);
		when(logoItem2.isDataChanged()).thenReturn(false);
		controller.logoSettingItems = Arrays.asList(logoItem1, logoItem2);

		controller.reset();

		verify(logoItem1).reset();
		verify(logoItem2).reset();
	}

	@Override
	protected LogoSettingController getWidgetController()
	{
		return controller;
	}
}
