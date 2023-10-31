/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Apache HTTP client wrapper, used for setting proxy stuff.
 */
public class HttpClientFactory
{
	private final HttpClientBuilder builder;

	public HttpClientFactory()
	{
		builder = HttpClientBuilder.create();
	}

	/**
	 * Construct HttpClient with proxy settings.
	 */
	public HttpClient getHttpClient()
	{
		final String proxyHost = Config.getParameter("http.proxy.host");
		final String proxyPort = Config.getParameter("http.proxy.port");
		if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort))
		{
			builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
		}
		return builder.build();
	}
}
