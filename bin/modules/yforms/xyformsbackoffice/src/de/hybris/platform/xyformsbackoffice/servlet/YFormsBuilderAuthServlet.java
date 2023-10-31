/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.servlet;

import de.hybris.platform.xyformsbackoffice.proxy.YFormsBuilderProxyService;
import de.hybris.platform.xyformsbackoffice.proxy.impl.DefaultYFormsBuilderProxyService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * yForms builder request handling servlet
 */
public class YFormsBuilderAuthServlet extends HttpServlet
{
	private static final Logger LOG = LoggerFactory.getLogger(YFormsBuilderAuthServlet.class);
	public static final String ERROR_MESSAGE = "encounter an error when proxy yForms builder request";

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
			final YFormsBuilderProxyService yFormsBuilderProxyService = context.getBean(DefaultYFormsBuilderProxyService.class);
			yFormsBuilderProxyService.proxy(request, response);
		}
		catch (IllegalStateException | BeansException e)
		{
			LOG.error(ERROR_MESSAGE, e);
		}
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
			final YFormsBuilderProxyService yFormsBuilderProxyService = context.getBean(DefaultYFormsBuilderProxyService.class);
			yFormsBuilderProxyService.proxy(request, response);
		}
		catch (IllegalStateException | BeansException e)
		{
			LOG.error(ERROR_MESSAGE, e);
		}
	}

	@Override
	protected void doPut(final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
			final YFormsBuilderProxyService yFormsBuilderProxyService = context.getBean(DefaultYFormsBuilderProxyService.class);
			yFormsBuilderProxyService.proxy(request, response);
		}
		catch (IllegalStateException | BeansException e)
		{
			LOG.error(ERROR_MESSAGE, e);
		}
	}
}
