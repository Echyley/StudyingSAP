/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.proxy.servlet;

import de.hybris.platform.xyformsfacades.proxy.ProxyFacade;
import de.hybris.platform.xyformsservices.proxy.ProxyException;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * @deprecated
 * Servlet in charge of accessing proxied resources:
 * <ul>
 * <li>Javascript</li>
 * <li>CSS</li>
 * <li>Images</li>
 * <li>xforms-server filter</li>
 * </ul>
 */
@Deprecated(since = "2211", forRemoval = true)
public class YFormsProxyServlet extends HttpServlet
{
	private static final Logger LOG = Logger.getLogger(YFormsProxyServlet.class);

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		final ProxyFacade proxyFacade = context.getBean(ProxyFacade.class);

		try
		{
			proxyFacade.proxy(request, response);
		}
		catch (final ProxyException e)
		{
			LOG.error(e.getMessage(), e);
			// At this point the proxy has already taken care of the response.
		}
	}
}