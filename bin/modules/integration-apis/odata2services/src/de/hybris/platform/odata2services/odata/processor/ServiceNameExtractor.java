/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * A service to extract the service name from the {@link ODataContext}
 */
public interface ServiceNameExtractor
{
	/**
	 * Extracts the service name from the context
	 *
	 * @param context Context containing the URL with the service name
	 * @return The service name, which is also code of the integration object presented by the endpoint.
	 */
	String extract(ODataContext context);
	
	/**
	 * Extracts the service name from the pathInfo
	 *
	 * @param httpServletRequest {@link javax.servlet.http.HttpServletRequest} with the information needed.
	 * @return a {@link String} The service name, which is also code of the integration object presented by the endpoint.
	 */
	String extract(final HttpServletRequest httpServletRequest);
}
