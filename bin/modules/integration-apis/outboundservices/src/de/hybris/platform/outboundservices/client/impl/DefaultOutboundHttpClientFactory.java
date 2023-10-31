/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.outboundservices.client.OutboundHttpClientFactory;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Default implementation of the {@link OutboundHttpClientFactory}
 */
public class DefaultOutboundHttpClientFactory implements OutboundHttpClientFactory
{
	private static final String HTTP = "http";
	private static final String HTTPS = "https";

	private final OutboundServicesConfiguration configuration;
	private int maxConnections;
	private int keepAlive;
	private int timeout;
	private int validity;

	/**
	 * Instantiates this factory. The instantiated factory uses a context configuration, which is defined in the
	 * application context as a Spring bean named {@code "outboundServicesConfiguration"}
	 */
	public DefaultOutboundHttpClientFactory()
	{
		this(null);
	}

	/**
	 * Instantiates this factory with the provided configuration service.
	 *
	 * @param cfg implementation of the configuration service to use for retrieving configuration parameters for the
	 *            {@code HttpClient} instances created by this factory. If {@code null}, then connection properties configured for
	 *            this factory will be used.
	 */
	public DefaultOutboundHttpClientFactory(final OutboundServicesConfiguration cfg)
	{
		configuration = cfg;
	}

	@Override
	public HttpClient create()
	{
		final var config = createRequestConfig();
		final var cm = createPoolingHttpClientConnectionManager();

		return createClient(config, cm, getKeepAlive());
	}

	/**
	 * Creates a request configuration to be used by the {@link HttpClient} this factory creates.
	 *
	 * @return a configuration in the state to be used by the client "as is".
	 */
	protected RequestConfig createRequestConfig()
	{
		return RequestConfig.custom()
				.setConnectTimeout(getTimeout())
				.setConnectionRequestTimeout(getTimeout())
				.setSocketTimeout(getTimeout())
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
				.build();
	}

	/**
	 * Creates a connection manager to be used by the {@link HttpClient} this factory creates
	 *
	 * @return a fully configured instance of connection manager to be used "as is" by the client.
	 */
	protected PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager()
	{
		final PoolingHttpClientConnectionManager connectionManager = getConnectionManager();
		connectionManager.setDefaultMaxPerRoute(getMaxConnections());
		connectionManager.setMaxTotal(getMaxConnections());
		connectionManager.setValidateAfterInactivity(getValidity());
		return connectionManager;
	}

	/**
	 * Creates a connection manager that is configured then by the {@link #createPoolingHttpClientConnectionManager()} method.
	 * @return a fresh instance of the connection manager. This implementation also initializes the connection manager to work for
	 * HTTP and HTTPS connections.
	 */
	protected PoolingHttpClientConnectionManager getConnectionManager()
	{
		return new PoolingHttpClientConnectionManager(
					RegistryBuilder.<ConnectionSocketFactory>create()
							.register(HTTP, PlainConnectionSocketFactory.getSocketFactory())
							.register(HTTPS, SSLConnectionSocketFactory.getSystemSocketFactory())
							.build());
	}

	CloseableHttpClient createClient(final RequestConfig cfg, final PoolingHttpClientConnectionManager cm, final int msec)
	{
		return getHttpClientBuilder()
				.setDefaultRequestConfig(cfg)
				.setConnectionManager(cm)
				.setKeepAliveStrategy((httpResponse, httpContext) -> msec)
				.build();
	}

	/**
	 * Creates an {@code HttpClientBuilder} used to create an {@link HttpClient} instance. The builder is then populated with the
	 * request configuration created by {@link #createRequestConfig()} method, and the connection manager created by the
	 * {@link #createPoolingHttpClientConnectionManager()}, the keep alive setting.
	 * @return an instance of the builder.
	 */
	protected HttpClientBuilder getHttpClientBuilder()
	{
		return HttpClientBuilder.create().useSystemProperties();
	}

	protected int getMaxConnections()
	{
		return configuration == null ? maxConnections : configuration.getMaxConnectionPoolSize();
	}

	public void setMaxConnections(final int maxConnections)
	{
		this.maxConnections = maxConnections;
	}

	public int getKeepAlive()
	{
		return configuration == null ? keepAlive : (int) configuration.getConnectionKeepAlive().toMillis();
	}

	public void setKeepAlive(final int keepAlive)
	{
		this.keepAlive = keepAlive;
	}

	public int getTimeout()
	{
		return configuration == null ? timeout : (int) configuration.getConnectionTimeout().toMillis();
	}

	public void setTimeout(final int timeout)
	{
		this.timeout = timeout;
	}

	public int getValidity()
	{
		return configuration == null ? validity
				: (int) configuration.getIdleConnectionValidityPeriod().toMillis();
	}

	public void setValidity(final int validity)
	{
		this.validity = validity;
	}
}
