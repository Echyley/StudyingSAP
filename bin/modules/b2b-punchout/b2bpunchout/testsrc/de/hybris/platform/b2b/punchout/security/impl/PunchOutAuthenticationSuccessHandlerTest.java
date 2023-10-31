/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;

import javax.servlet.ServletException;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutAuthenticationSuccessHandlerTest
{
	private static final String PUNCHOUT_USER_ID = "punchoutUserId@cxml.org";

	@InjectMocks
	private PunchOutAuthenticationSuccessHandler punchOutAuthenticationSuccessHandler;

	@Mock
	private CustomerFacade customerFacade;

	private final MockHttpServletResponse response = new MockHttpServletResponse();
	private final MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void testOnAuthenticationSuccess() throws ServletException, IOException
	{
		final Authentication authentication = new UsernamePasswordAuthenticationToken(PUNCHOUT_USER_ID, null);
		punchOutAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

		verify(customerFacade).loginSuccess();
	}
}
