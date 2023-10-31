/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.orbeonweb.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Custom request matcher to exclude requests which contains proxy header and value, request with proxy header are trusted by default
 */
public class ExcludeHttpsRequestMatcher implements RequestMatcher
{
	private String hybrisProxyHeader;
	private String hybrisProxyValue;

	@Override
	public boolean matches(final HttpServletRequest request)
	{
		if (request.getHeader(hybrisProxyHeader) == null)
		{
			return true;
		}

		return !request.getHeader(hybrisProxyHeader).equals(hybrisProxyValue);
	}

	public void setHybrisProxyHeader(final String hybrisProxyHeader)
	{
		this.hybrisProxyHeader = hybrisProxyHeader;
	}

	public void setHybrisProxyValue(final String hybrisProxyValue)
	{
		this.hybrisProxyValue = hybrisProxyValue;
	}
}
