/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutUserAuthenticationStrategyTest
{
	private static final String PUNCHOUT_USER_ID = "punchoutUserId@cxml.org";

	@InjectMocks
	private DefaultPunchOutUserAuthenticationStrategy defaultPunchOutUserAuthenticationStrategy;

	@Mock
	private AuthenticationProvider authenticationProvider;
	@Mock
	AuthenticationSuccessHandler authenticationSuccessHandler;

	private final MockHttpServletResponse response = new MockHttpServletResponse();
	private final MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void setUp()
	{
		final List<AuthenticationSuccessHandler> authenticationSuccessHandlers = new ArrayList<>();
		authenticationSuccessHandlers.add(authenticationSuccessHandler);
		defaultPunchOutUserAuthenticationStrategy.setAuthenticationSuccessHandlers(authenticationSuccessHandlers);
	}

	@Test
	public void testAuthenticate() throws ServletException, IOException
	{
		final Authentication authentication = new UsernamePasswordAuthenticationToken(PUNCHOUT_USER_ID, null);
		doReturn(authentication).when(authenticationProvider).authenticate(any());

		defaultPunchOutUserAuthenticationStrategy.authenticate(PUNCHOUT_USER_ID, request, response);

		verify(authenticationProvider).authenticate(any());
		verify(authenticationSuccessHandler).onAuthenticationSuccess(request, response, authentication);
	}
}
