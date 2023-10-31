/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.proxy.impl;

import de.hybris.platform.xyformsbackoffice.proxy.YFormsBuilderProxyService;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.CookieManager;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.impl.XmlUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.MalformedCookieException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.backoffice.user.BackofficeRoleService;
import com.hybris.backoffice.user.BackofficeUserService;


/**
 * Default YForms builder proxy service.
 */
public class DefaultYFormsBuilderProxyService implements YFormsBuilderProxyService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultYFormsBuilderProxyService.class);

	protected static final String ORBEON_PREFIX = "/orbeon";

	protected static final String INVALIDATE_SESSION_PATH = "/logout.jsp";
	protected static final String POST_METHOD = HttpMethod.POST;
	protected static final String PUT_METHOD = HttpMethod.PUT;
	protected static final Integer CLIENT_ERROR_MINIMUM = 400;
	protected static final Integer SERVER_ERROR_MAXIMUM = 599;

	@Resource(name = "backofficeRoleService")
	protected BackofficeRoleService backofficeRoleService;

	@Resource(name = "backofficeUserService")
	protected BackofficeUserService backofficeUserService;

	@Resource(name = "cookieManager")
	protected CookieManager cookieManager;

	private String orbeonWebAddress;
	private String yFormsProxyHeader;
	private String yFormsProxyValue;


	@Override
	public void proxy(final HttpServletRequest request, final HttpServletResponse response)
	{
		HttpURLConnection conn = null;
		InputStream input = null;
		OutputStream output = null;
		InputStream errorStream = null;
		try
		{
			String rewriteUrl = this.rewriteURL(request.getRequestURI());
			conn = this.connect(request, rewriteUrl);

			final int rc = conn.getResponseCode();

			LOG.debug("Setting Response Status Code: {}", rc);
			response.setStatus(rc);
			this.setResponseHeaders(conn, response);

			output = response.getOutputStream();

			if (rc == HttpURLConnection.HTTP_OK)
			{
				input = conn.getInputStream();
				if (input != null) {
					IOUtils.copy(input, output);
					output.flush();
				}
			}
			else
			{
				errorStream = conn.getErrorStream();
				if (errorStream == null)
				{
					LOG.debug("No error stream");
				} else {
					IOUtils.copy(errorStream, output);
					output.flush();
				}
			}
		}
		catch (final URISyntaxException | MalformedCookieException | IOException e)
		{
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			LOG.error("encounter an error when backoffice proxy builder request to orbeonweb",e);
		}
		finally
		{
			safeClose(input);
			safeClose(output);
			safeClose(errorStream);
			if (conn != null)
			{
				conn.disconnect();
			}
		}
	}

	@Override
	public void invalidateOrbeonSession(HttpServletRequest request) {
		String invalidateSessionUrl = orbeonWebAddress + INVALIDATE_SESSION_PATH;
		HttpURLConnection conn = null;
		try
		{
			URL url = new URL(invalidateSessionUrl);
			conn = (HttpURLConnection) url.openConnection();
			this.setRequestHeaders(request, conn);
			this.cookieManager.processRequest(request, conn, invalidateSessionUrl);
			conn.setRequestMethod(HttpMethod.GET);
			conn.connect();
			final int rc = conn.getResponseCode();
			if (rc == HttpURLConnection.HTTP_OK) { // success
				LOG.debug("invalidate orbeon backoffice session successfully");
			} else {
				LOG.error("invalidate orbeon backoffice session failed");
			}
		}
		catch (IOException | URISyntaxException e)
		{
			LOG.error("encounter an error when invalidate orbeon session",e);
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}

	}

	protected String rewriteURL(String uri)
	{
		LOG.debug("origin uri of backoffice yForms builder request: {}", uri);
		String rewriteUrl = orbeonWebAddress + uri.substring(uri.indexOf(ORBEON_PREFIX) + ORBEON_PREFIX.length());
		LOG.debug("rewrite url of backoffice yForms builder request: {}", rewriteUrl);
		return rewriteUrl;
	}

	protected void setResponseHeaders(final HttpURLConnection conn, final HttpServletResponse response)
	{
		LOG.debug("Response headers coming from orbeon:");
		Map<String, List<String>> headerMap = conn.getHeaderFields();

		for (String headerName : headerMap.keySet())
		{
			final String headerValue = conn.getHeaderField(headerName);
			if (headerValue != null)
			{
				response.setHeader(headerName, headerValue);
			}
		}
	}

	protected void setRequestHeaders(final HttpServletRequest request, final HttpURLConnection conn)
	{
		LOG.debug("Request headers send to orbeon:");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames != null && headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			if (headerValue != null)
			{
				conn.setRequestProperty(headerName, headerValue);
			}
		}

		if (yFormsProxyHeader != null)
		{
			conn.setRequestProperty(yFormsProxyHeader, yFormsProxyValue);
			LOG.debug("-- add proxy header --");
		}

		Optional<String> role = this.backofficeRoleService.getActiveRole();
		String uid = this.backofficeUserService.getCurrentUser().getUid();
		conn.setRequestProperty("hybris-Username", uid);
		LOG.debug("-- add user info -- [hybris-Username: {}]", uid);

		role.ifPresent(rolesString -> {
			conn.setRequestProperty("hybris-Roles", rolesString);
			LOG.debug("-- add role info -- [hybris-Roles: {}]", rolesString);
		});
	}

	private void safeClose(Closeable stream)
	{
		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				LOG.warn("error on closing stream", e);
			}
		}
	}

	protected boolean isTextFile(final String contentType)
	{
		final String mediaType = XmlUtil.getContentTypeMediaType(contentType);
		return XmlUtil.isTextOrJSONContentType(mediaType) || XmlUtil.isXMLMediatype(mediaType);
	}

	protected boolean isServerError(final int sc)
	{
		return sc >= CLIENT_ERROR_MINIMUM && sc <= SERVER_ERROR_MAXIMUM; // Client-Error or Server-Error
	}

	public void setYFormsProxyHeader(final String yFormsProxyHeader)
	{
		this.yFormsProxyHeader = yFormsProxyHeader;
	}

	public void setYFormsProxyValue(final String yFormsProxyValue)
	{
		this.yFormsProxyValue = yFormsProxyValue;
	}

	public void setOrbeonWebAddress(final String orbeonWebAddress)
	{
		this.orbeonWebAddress = orbeonWebAddress;
	}

	private HttpURLConnection connect(final HttpServletRequest request, final String url)
			throws URISyntaxException, IOException, MalformedCookieException
	{
		URL orbeonWebURL = new URL(url);
		final HttpURLConnection conn = (HttpURLConnection) orbeonWebURL.openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		String method = request.getMethod();
		conn.setRequestMethod(method);

		this.setRequestHeaders(request, conn);

		// Process request cookies
		this.cookieManager.processRequest(request, conn, url);

		conn.connect();

		// We copy the content, only for PUT and POST
		if ((POST_METHOD.equals(method) || PUT_METHOD.equals(method)))
		{
			InputStream is = request.getInputStream();
			OutputStream os = conn.getOutputStream();
			try
			{
				// If debug is enabled and content type is a text file, we show the output
				if (!LOG.isDebugEnabled() || !isTextFile(request.getContentType()))
				{
					is.transferTo(os);
				}
				else if (LOG.isDebugEnabled())
				{
					final String content = IOUtils.toString(is, Charset.defaultCharset());
					IOUtils.write(content, os, Charset.defaultCharset());
				}
			}
			finally
			{
				safeClose(os);
			}
		}

		final int rc = conn.getResponseCode();
		if (!this.isServerError(rc))
		{
			this.cookieManager.processResponse(conn, url);
		}
		return conn;
	}
}
