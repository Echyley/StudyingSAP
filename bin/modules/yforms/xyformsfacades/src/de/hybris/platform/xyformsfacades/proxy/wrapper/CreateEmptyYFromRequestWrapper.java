/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.proxy.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class CreateEmptyYFromRequestWrapper extends HttpServletRequestWrapper
{
	protected static final String POST_METHOD = "POST";

	public CreateEmptyYFromRequestWrapper(final HttpServletRequest request)
	{
		super(request);
	}

	@Override
	public String getMethod()
	{
		return POST_METHOD;
	}
}
