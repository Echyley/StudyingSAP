/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security.impl;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class DefaultPunchOutUserAuthenticationStrategy implements PunchOutUserAuthenticationStrategy
{
	private AuthenticationProvider authenticationProvider;
	private List<AuthenticationSuccessHandler> authenticationSuccessHandlers = Collections.emptyList();

	@Override
	public void authenticate(final String userId, final HttpServletRequest request, final HttpServletResponse response)
	{
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, null);
		token.setDetails(new WebAuthenticationDetails(request));
		try
		{
			final Authentication authentication = getAuthenticationProvider().authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			for (final AuthenticationSuccessHandler successHandler : getAuthenticationSuccessHandlers())
			{
				successHandler.onAuthenticationSuccess(request, response, authentication);
			}
		}
		catch (final AuthenticationException | IOException | ServletException exc)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			throw new PunchOutException(HttpStatus.UNAUTHORIZED, "Could not authenticate user", exc);
		}
	}

	@Override
	public void logout()
	{
		SecurityContextHolder.clearContext();
	}

	protected AuthenticationProvider getAuthenticationProvider()
	{
		return authenticationProvider;
	}

	public void setAuthenticationProvider(final AuthenticationProvider authenticationProvider)
	{
		this.authenticationProvider = authenticationProvider;
	}

	protected List<AuthenticationSuccessHandler> getAuthenticationSuccessHandlers()
	{
		return authenticationSuccessHandlers;
	}

	public void setAuthenticationSuccessHandlers(final List<AuthenticationSuccessHandler> authenticationSuccessHandlers)
	{
		this.authenticationSuccessHandlers = authenticationSuccessHandlers;
	}
}
