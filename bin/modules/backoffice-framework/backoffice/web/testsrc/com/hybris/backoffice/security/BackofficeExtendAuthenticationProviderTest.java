/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.hybris.backoffice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
public class BackofficeExtendAuthenticationProviderTest
{

	private static final String SSO_TOKEN = "LoginToken";
	@InjectMocks
	@Spy
	private BackofficeExtendAuthenticationProvider backofficeExtendAuthenticationProvider;

	@Mock
	private ClickTrackingService clickTrackingService;

	@Mock
	private LoginTrackingService loginTrackingService;

	@Test
	public void shouldDoNothingForClickTrackingIfDisabled()
	{
		final Authentication authMock1 = mock(Authentication.class);
		final Authentication authMock2 = mock(Authentication.class);
		doReturn(authMock2).when(backofficeExtendAuthenticationProvider).doCoreAuthenticate(authMock1);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(false);

		final Authentication actualAuth = backofficeExtendAuthenticationProvider.coreAuthenticate(authMock1);

		verify(loginTrackingService, never()).setLoginType(any(LoginType.class));
		assertThat(actualAuth).isEqualTo(authMock2);
	}

	@Test
	public void shouldSetCorrectLoginTypeWhenDefaultLogin()
	{
		final String credential = "";

		final Authentication authMock1 = mock(Authentication.class);
		final Authentication authMock2 = mock(Authentication.class);
		when(authMock2.getCredentials()).thenReturn(credential);
		doReturn(authMock2).when(backofficeExtendAuthenticationProvider).doCoreAuthenticate(authMock1);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(true);

		final Authentication actualAuth = backofficeExtendAuthenticationProvider.coreAuthenticate(authMock1);

		verify(loginTrackingService, times(1)).setLoginType(LoginType.DEFAULT);
		verify(loginTrackingService, never()).setLoginType(LoginType.SSO);
		assertThat(actualAuth).isEqualTo(authMock2);
	}

	@Test
	public void shouldSetCorrectLoginTypeWhenSSOLogin()
	{
		final CookieBasedLoginToken credential = mock(CookieBasedLoginToken.class);
		when(credential.getName()).thenReturn(SSO_TOKEN);

		final Authentication authMock1 = mock(Authentication.class);
		final Authentication authMock2 = mock(Authentication.class);
		when(authMock2.getCredentials()).thenReturn(credential);
		doReturn(authMock2).when(backofficeExtendAuthenticationProvider).doCoreAuthenticate(authMock1);
		when(clickTrackingService.isClickTrackingEnabled()).thenReturn(true);
		when(backofficeExtendAuthenticationProvider.getSSOCookieName()).thenReturn(SSO_TOKEN);

		final Authentication actualAuth = backofficeExtendAuthenticationProvider.coreAuthenticate(authMock1);

		verify(loginTrackingService, times(1)).setLoginType(LoginType.DEFAULT);
		verify(loginTrackingService, times(1)).setLoginType(LoginType.SSO);
		assertThat(actualAuth).isEqualTo(authMock2);
	}

}
