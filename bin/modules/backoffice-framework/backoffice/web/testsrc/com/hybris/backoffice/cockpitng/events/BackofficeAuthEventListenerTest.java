/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.hybris.backoffice.cockpitng.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.jalo.user.CookieBasedLoginToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import com.hybris.cockpitng.util.tracking.ClickTrackingService;
import com.hybris.cockpitng.util.tracking.login.LoginTrackingService;
import com.hybris.cockpitng.util.tracking.login.LoginType;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeAuthEventListenerTest
{

	private static final String SSO_TOKEN = "LoginToken";

	@InjectMocks
	@Spy
	private BackofficeAuthEventListener backofficeAuthEventListener;

	@Mock
	private ClickTrackingService clickTrackingService;

	@Mock
	private LoginTrackingService loginTrackingService;

	@Test
	public void shouldDoNothingForClickTrackingIfDisabled()
	{
		final Authentication eventMock = mock(Authentication.class);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(false);

		backofficeAuthEventListener.onAuthEvent(eventMock);

		verify(loginTrackingService, never()).setLoginType(any(LoginType.class));
	}

	@Test
	public void shouldSetCorrectLoginTypeWhenDefaultLogin()
	{
		final Authentication authMock = mock(Authentication.class);
		final String credential = "";
		when(authMock.getCredentials()).thenReturn(credential);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(true);

		backofficeAuthEventListener.onAuthEvent(authMock);

		verify(loginTrackingService, times(1)).setLoginType(LoginType.DEFAULT);
		verify(loginTrackingService, never()).setLoginType(LoginType.SSO);
	}

	@Test
	public void shouldSetCorrectLoginTypeWhenSSOLogin()
	{
		final Authentication authMock = mock(Authentication.class);
		final CookieBasedLoginToken credential = mock(CookieBasedLoginToken.class);
		when(credential.getName()).thenReturn(SSO_TOKEN);
		when(authMock.getCredentials()).thenReturn(credential);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(true);
		when(backofficeAuthEventListener.getSSOCookieName()).thenReturn(SSO_TOKEN);

		backofficeAuthEventListener.onAuthEvent(authMock);

		verify(loginTrackingService, times(1)).setLoginType(LoginType.DEFAULT);
		verify(loginTrackingService, times(1)).setLoginType(LoginType.SSO);
	}

}
