/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.orbeonweb.filter;

import de.hybris.platform.orbeonweb.wrapper.YFormRequestAuthWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Filter to add user auth info to request header.
 */
public class OrbeonAuthFilter implements Filter
{
	private static final Logger LOG = LoggerFactory.getLogger(OrbeonAuthFilter.class);
	private String hybrisProxyHeader;
	private String hybrisProxyValue;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		// Do nothing
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException
	{
		ServletRequest requestWrapper = servletRequest;
		if (servletRequest instanceof HttpServletRequest)
		{
			HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			if (httpServletRequest.getHeader(hybrisProxyHeader) == null)
			{
				LOG.debug("Wrap https request to add auth info to header");
				requestWrapper = new YFormRequestAuthWrapper(httpServletRequest, hybrisProxyHeader, hybrisProxyValue);
			}
		}
		filterChain.doFilter(requestWrapper, servletResponse);
	}

	@Override
	public void destroy()
	{
		// Do nothing
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

