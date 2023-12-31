/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsservices.proxy;

import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @deprecated
 * Class that implements some util methods to use the proxy engine.
 */
@Deprecated(since = "2211", forRemoval = true)
public interface ProxyService
{
	/**
	 * Rewrites a URL to be used for the proxy.
	 *
	 * @param applicationId
	 *          the application Id
	 * @param formId
	 *          the form Id
	 * @param formDataId
	 *          the form data Id
	 * @param editable
	 * 			the flag that specifies whether form should be editable or not
	 * @param withEmptyData
	 * 			the flag that specifies whether create empty data for form via background api
	 * @return the URL of the proxy
	 * @throws MalformedURLException if result URL is invalid
	 */
	public String rewriteURL(final String applicationId, final String formId, final String formDataId, final boolean editable, final boolean withEmptyData)
			throws MalformedURLException;

	/**
	 * Rewrites a URL to be used for the proxy.
	 *
	 * @param applicationId
	 *          the application Id
	 * @param formId
	 *          the form Id
	 * @param formDataId
	 *          the form data Id
	 * @return the URL of the proxy
	 * @throws MalformedURLException if result URL is invalid
	 */
	public String rewriteURL(final String applicationId, final String formId, final String formDataId)
			throws MalformedURLException;

	/**
	 * Rewrites a URL to be used for the proxy.
	 *
	 * @param applicationId
	 *          the application Id
	 * @param formId
	 *          the form Id
	 * @param formDataId
	 *          the form data Id
	 * @param editable
	 * 			the flag that specifies whether form should be editable or not
	 * @return the URL of the proxy
	 * @throws MalformedURLException if result URL is invalid
	 */
	public String rewriteURL(final String applicationId, final String formId, final String formDataId, final boolean editable)
			throws MalformedURLException;

	/**
	 * Rewrites a URL to be used for the proxy.
	 *
	 * @param url
	 * 			the url path to be rewritten
	 * @param embeddable
	 *         	the flag that specifies if the rewriter should produce a URL that produces embeddable content
	 * @return the URL of the proxy
	 * @throws MalformedURLException if result URL is invalid
	 */
	public String rewriteURL(final String url, final boolean embeddable) throws MalformedURLException;

	/**
	 * Generates a random namespace.
	 *
	 * @return the random namespace string
	 */
	public String getNextRandomNamespace();

	/**
	 * Extracts the namespace from the client request.
	 *
	 * @param request
	 * 			the request to get namespace from
	 * @return the extracted namespace
	 */
	public String extractNamespace(HttpServletRequest request);

	/**
	 * Returns the extra headers configured for the application.
	 *
	 * @return a new instance of the extra headers as map
	 */
	public Map<String, String> getExtraHeaders();

	/**
	 * Proxies content.
	 *
	 * @param request
	 *           the {@link HttpServletRequest} associated to the call
	 * @param response
	 *           the {@link HttpServletResponse} associated to the call
	 * @param namespace
	 *           the namespace for HTML element's id generation
	 * @param url
	 *           the url to be called
	 * @param forceGetMethod
	 *           the flag for specifying if only "GET" method should be used, useful when proxying content
	 * @param headers
	 *           extra headers to be passed to the proxy service
	 * @throws ProxyException if request cannot be proxied
	 */
	void proxy(HttpServletRequest request, HttpServletResponse response, String namespace, String url, boolean forceGetMethod,
			final Map<String, String> headers) throws ProxyException;
}
