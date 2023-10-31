/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.userprofile.toggle;

import static com.hybris.backoffice.widgets.userprofile.toggle.ToggleController.OPEN_PROFILE_SETTINGS_BTN;
import static com.hybris.backoffice.widgets.userprofile.toggle.ToggleController.SIGNOUT_BTN;
import static com.hybris.backoffice.widgets.userprofile.toggle.ToggleController.SOCKET_INPUT_AVATAR_CHANGED;
import static com.hybris.backoffice.widgets.userprofile.toggle.ToggleController.SOCKET_OUTPUT_OPEN_PROFILE_SETTINGS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Toolbarbutton;

import com.hybris.backoffice.widgets.quicktogglelocale.controller.IndexedLanguagesResolver;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.util.CockpitSessionService;


@DeclaredInput(value = SOCKET_INPUT_AVATAR_CHANGED, socketType = MediaModel.class)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = OPEN_PROFILE_SETTINGS_BTN)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = SIGNOUT_BTN)
public class ToggleControllerTest extends AbstractWidgetUnitTest<ToggleController>
{
	@Mock
	private CockpitSessionService cockpitSessionService;
	@Mock
	private UserService userService;
	@Mock
	private CockpitLocaleService cockpitLocaleService;
	@Mock
	private IndexedLanguagesResolver indexedLanguagesResolver;
	@Mock
	private Label userNameLabel;
	@Mock
	protected Toolbarbutton userProfileBtn;
	@Mock
	protected Listitem openSettingsBtn;
	@Mock
	protected Listitem signOutBtn;

	@Spy
	@InjectMocks
	private ToggleController controller;

	private static final Locale PL = new Locale("pl");
	private UserModel currentUser;

	@Before
	public void setup()
	{
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(PL);
		currentUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(currentUser);
	}

	@Test
	public void shouldDisplayAvatarIfExist()
	{
		final var avatar = mock(MediaModel.class);
		final var url = "avatarurl";
		when(currentUser.getAvatar()).thenReturn(avatar);
		when(avatar.getURL()).thenReturn(url);

		controller.initialize(mock(Component.class));

		verify(userProfileBtn).setImage(url);
	}

	@Test
	public void shouldDisplayCurrentUserNameIfNotNull()
	{
		final String name = "name";
		when(currentUser.getDisplayName()).thenReturn(name);
		controller.initialize(mock(Component.class));
		verify(userNameLabel).setValue(name);
	}

	@Test
	public void shouldDisplayCurrentUserIdIfNameIsNull()
	{
		final String id = "id";
		when(currentUser.getUid()).thenReturn(id);
		controller.initialize(mock(Component.class));
		verify(controller).triggerLanguageNotIndexedNotification(eq(PL.toString()), eq(true), any());
		verify(userNameLabel).setValue(id);
	}

	@Test
	public void shouldSendOutputToOpenSettingsWhenClickOpenProfileSettings()
	{
		executeViewEvent(OPEN_PROFILE_SETTINGS_BTN, Events.ON_CLICK);
		assertSocketOutput(SOCKET_OUTPUT_OPEN_PROFILE_SETTINGS, (Object) null);
	}

	@Test
	public void shouldLogoutWhenClickLogoutBtn()
	{
		executeViewEvent(SIGNOUT_BTN, Events.ON_CLICK);
		verify(cockpitSessionService).logout();
	}

	@Test
	public void shouldShowPostponedNotificationOnInitWhenSessionLanguageNotIndexed()
	{
		// given
		final Component component = new Div();
		when(indexedLanguagesResolver.isIndexed(PL.getLanguage())).thenReturn(false);
		doNothing().when(controller).triggerLanguageNotIndexedNotification(PL.getLanguage(), true, component);
		final String name = "name";
		when(currentUser.getDisplayName()).thenReturn(name);
		doNothing().when(userNameLabel).setValue(name);

		// when
		controller.initialize(component);

		// then
		verify(controller).triggerLanguageNotIndexedNotification(PL.getLanguage(), true, component);
	}

	@Test
	public void shouldShowNewAvatarIfNotifyAvatarChanged()
	{
		final var media = mock(MediaModel.class);
		final var url = "new avatar url";
		when(media.getURL()).thenReturn(url);
		executeInputSocketEvent(SOCKET_INPUT_AVATAR_CHANGED, media);

		verify(userProfileBtn).setImage(url);
	}

	@Test
	public void shouldNotShowNewAvatarIfNotifyNullAvatarChanged()
	{
		final var media = mock(MediaModel.class);
		executeInputSocketEvent(SOCKET_INPUT_AVATAR_CHANGED, (MediaModel) null);

		verify(userProfileBtn, never()).setImage(any());
	}


	@Override
	protected ToggleController getWidgetController()
	{
		return controller;
	}
}

