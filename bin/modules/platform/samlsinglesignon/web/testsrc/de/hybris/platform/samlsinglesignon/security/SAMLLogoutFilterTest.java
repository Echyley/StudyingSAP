/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class SAMLLogoutFilterTest
{
	SAMLLogoutTestFilter samlLogoutFilter;
	@Mock
	private LogoutHandler localLogoutHandler;
	@Mock
	private LogoutHandler globalLogoutHandler;
	@Mock
	private LogoutSuccessHandler localLogoutSuccessHandler;
	@Mock
	private LogoutSuccessHandler globalLogoutSuccessHandler;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockFilterChain chain;

	@Before
	public void setUp()
	{
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		samlLogoutFilter = new SAMLLogoutTestFilter(globalLogoutSuccessHandler, localLogoutSuccessHandler, globalLogoutHandler,
				localLogoutHandler);
	}

	@Test
	public void shouldCallOnlyLocalLogoutHandlers() throws ServletException, IOException
	{
		withLocal(true);
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, times(1)).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, never()).logout(any(), any(), any());
	}

	@Test
	public void shouldCallOnlyLocalLogoutHandlersWhenAuthIsNullAndLocalTrue() throws ServletException, IOException
	{
		withLocal(true);
		samlLogoutFilter = getSamlLogoutTestFilterNullAuth();
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, times(1)).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, never()).logout(any(), any(), any());
	}

	@Test
	public void shouldCallOnlyLocalLogoutHandlersWhenAuthIsNullAndLocalFalse() throws ServletException, IOException
	{
		withLocal(false);
		samlLogoutFilter = getSamlLogoutTestFilterNullAuth();
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, times(1)).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, never()).logout(any(), any(), any());
	}

	@Test
	public void shouldCallOnlyLocalLogoutHandlersWhenAuthIsNullAndNoLocalParam() throws ServletException, IOException
	{
		samlLogoutFilter = getSamlLogoutTestFilterNullAuth();
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, times(1)).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, never()).logout(any(), any(), any());
	}

	@Test
	public void shouldCallOnlyLocalLogoutHandlersWhenNoLocalParam() throws ServletException, IOException
	{
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, times(1)).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, never()).logout(any(), any(), any());
	}

	@Test
	public void shouldCallOnlyGlobalLogoutHandlers() throws ServletException, IOException
	{
		withLocal(false);
		samlLogoutFilter.doFilter(request, response, chain);

		verify(localLogoutSuccessHandler, never()).onLogoutSuccess(any(), any(), any());
		verify(localLogoutHandler, never()).logout(any(), any(), any());

		verify(globalLogoutSuccessHandler, times(1)).onLogoutSuccess(any(), any(), any());
		verify(globalLogoutHandler, times(1)).logout(any(), any(), any());
	}

	private void withLocal(final boolean local)
	{
		request.setParameter("local", String.valueOf(Boolean.valueOf(local)));
	}

	private SAMLLogoutTestFilterNullAuth getSamlLogoutTestFilterNullAuth()
	{
		return new SAMLLogoutTestFilterNullAuth(globalLogoutSuccessHandler, localLogoutSuccessHandler,
				globalLogoutHandler,
				localLogoutHandler);
	}

	public static class SAMLLogoutTestFilterNullAuth extends SAMLLogoutTestFilter
	{

		public SAMLLogoutTestFilterNullAuth(
				final LogoutSuccessHandler singleLogoutSuccessHandler,
				final LogoutSuccessHandler localLogoutSuccessHandler,
				final LogoutHandler singleLogoutHandlers,
				final LogoutHandler localLogoutHandlers)
		{
			super(singleLogoutSuccessHandler, localLogoutSuccessHandler, singleLogoutHandlers, localLogoutHandlers);
		}

		@Override
		protected Authentication getAuthentication()
		{
			return null;
		}
	}

	public static class SAMLLogoutTestFilter extends SAMLLogoutFilter
	{

		public SAMLLogoutTestFilter(
				final LogoutSuccessHandler singleLogoutSuccessHandler,
				final LogoutSuccessHandler localLogoutSuccessHandler,
				final LogoutHandler singleLogoutHandlers,
				final LogoutHandler localLogoutHandlers)
		{
			super(singleLogoutSuccessHandler, localLogoutSuccessHandler, new LogoutHandler[]{ singleLogoutHandlers },
					new LogoutHandler[]{ localLogoutHandlers });
		}

		@Override
		protected boolean requiresLogout(final HttpServletRequest request, final HttpServletResponse response)
		{
			return true;
		}

		@Override
		protected Authentication getAuthentication()
		{
			return new MockAuthentication(Collections.list(new SimpleGrantedAuthority("USER_ROLE")));
		}

		@Override
		protected boolean isSaml2Authentication(final Authentication auth)
		{
			return true;
		}
	}


	protected static class MockAuthentication extends AbstractAuthenticationToken
	{
		public MockAuthentication(final Collection<? extends GrantedAuthority> authorities)
		{
			super(authorities);
		}

		@Override
		public Object getCredentials()
		{
			return null;
		}

		@Override
		public Object getPrincipal()
		{
			return null;
		}
	}
}
