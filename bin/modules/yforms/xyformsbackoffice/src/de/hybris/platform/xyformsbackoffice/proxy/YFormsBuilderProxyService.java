/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.proxy;


import org.apache.commons.lang.NotImplementedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * YForms Builder Proxy Service to proxy request to orbeonweb.
 */
public interface YFormsBuilderProxyService
{
	/**
	 * Proxies content.
	 *
	 * @param request  the {@link HttpServletRequest} associated to the call
	 * @param response the {@link HttpServletResponse} associated to the call
	 */
	void proxy(HttpServletRequest request, HttpServletResponse response);


	/**
	 * Invalidate current logged user orbeon session
	 *
	 * @param request the {@link HttpServletRequest} associated to the call
	 */
	default void invalidateOrbeonSession(HttpServletRequest request)
	{
		throw new NotImplementedException();
	}
}
