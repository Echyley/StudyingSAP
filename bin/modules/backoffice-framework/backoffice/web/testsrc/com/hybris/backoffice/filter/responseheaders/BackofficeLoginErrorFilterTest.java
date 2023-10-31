/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.filter.responseheaders;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BackofficeLoginErrorFilterTest
{
	@Mock
	private HttpServletRequest servletRequest;
	@Mock
	private HttpServletResponse servletResponse;
	@Mock
	private FilterChain filterChain;

	private final BackofficeLoginErrorFilter backofficeLoginErrorFilter = new BackofficeLoginErrorFilter();

	@Test
	public void shouldDoNothingIfNoLoginError() throws IOException, ServletException
	{
		when(servletRequest.getParameter("login_error")).thenReturn(null);

		backofficeLoginErrorFilter.doFilter(servletRequest, servletResponse, filterChain);

		verify(servletResponse, never()).setHeader(anyString(), anyString());
	}

	@Test
	public void shouldAddLoginErrorWhenErrorHappens() throws IOException, ServletException
	{
		final String errorCode = "1";
		when(servletRequest.getParameter("login_error")).thenReturn(errorCode);

		backofficeLoginErrorFilter.doFilter(servletRequest, servletResponse, filterChain);

		verify(servletResponse, times(1)).setHeader("X-BO-Login-Error-Code", errorCode);
	}

}
