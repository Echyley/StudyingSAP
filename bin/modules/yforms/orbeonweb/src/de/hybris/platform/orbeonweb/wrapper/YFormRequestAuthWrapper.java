/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.orbeonweb.wrapper;

import de.hybris.platform.servicelayer.user.UserConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Wrapper request to add user info from spring security context to http header
 */
public class YFormRequestAuthWrapper extends HttpServletRequestWrapper
{

	private final Map<String, String> requestHeaders = new HashMap();
	private static final String HYBRIS_USERNAME = "hybris-username";
	private static final String HYBRIS_ROLES = "hybris-roles";
	private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";


	public YFormRequestAuthWrapper(final HttpServletRequest request, final String hybrisProxyHeader, final String hybrisProxyValue)
	{
		super(request);
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& !UserConstants.ANONYMOUS_CUSTOMER_UID.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
			final List<String> userRoles = new ArrayList();
			SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(
					role -> userRoles.add(role.toString()));
			requestHeaders.put(hybrisProxyHeader, hybrisProxyValue);
			requestHeaders.put(HYBRIS_USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
			requestHeaders.put(HYBRIS_ROLES, String.join(",", userRoles));
		}
		else
		{
			requestHeaders.put(hybrisProxyHeader, hybrisProxyValue);
			requestHeaders.put(HYBRIS_USERNAME, UserConstants.ANONYMOUS_CUSTOMER_UID);
			requestHeaders.put(HYBRIS_ROLES, ROLE_ANONYMOUS);
		}
	}

	@Override
	public String getHeader(String name)
	{
		if (name != null && requestHeaders.containsKey(name))
		{
			return requestHeaders.get(name);
		}
		return super.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames()
	{
		final Set<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
		names.addAll(requestHeaders.keySet());

		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name)
	{
		if (name != null && requestHeaders.containsKey(name))
		{
			List<String> values = Arrays.asList(requestHeaders.get(name));
			return Collections.enumeration(values);
		}

		return super.getHeaders(name);
	}
}
