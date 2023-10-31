/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.client.OutboundHttpClientFactory;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@code ClientHttpRequestFactory} in the outboundservices. This implementation lazily
 * creates {@link org.apache.http.client.HttpClient}s per each request destination. Two destinations are the same,
 * if they have equal host, and such destinations will share the {@code HttpClient} and the connection pool, if the
 * client uses a connection pool manager.
 *
 * @see URI#getHost()
 */
public class DefaultClientRequestFactory implements ClientHttpRequestFactory
{
	private static final Logger LOG = Log.getLogger(DefaultClientRequestFactory.class);

	private final ConcurrentMap<String, ClientHttpRequestFactory> createdClients;
	private final OutboundHttpClientFactory clientFactory;
	private OutboundServicesConfiguration configuration;

	/**
	 * Instantiates this request factory.
	 *
	 * @param factory a factory to create {@link HttpClient} instances to be used by the requests.
	 */
	public DefaultClientRequestFactory(@NotNull final OutboundHttpClientFactory factory)
	{
		Preconditions.checkArgument(factory != null, "OutboundHttpClientFactory is required");
		clientFactory = factory;
		createdClients = new ConcurrentHashMap<>();
	}

	@Override
	@Nonnull
	public ClientHttpRequest createRequest(@Nonnull final URI uri, @Nonnull final HttpMethod httpMethod) throws IOException
	{
		final var delegate = getRequestFactory(uri);
		return delegate.createRequest(uri, httpMethod);
	}

	ClientHttpRequestFactory getRequestFactory(final URI uri)
	{
		final String host = extractHost(uri);
		return host != null
				? createdClients.computeIfAbsent(host, key -> createConfiguredRealFactory())
				: null;
	}

	private String extractHost(final URI uri)
	{
		if (uri == null)
		{
			return null;
		}
		return uri.getHost() != null
				? uri.getHost()
				: uri.toString();
	}

	private ClientHttpRequestFactory createConfiguredRealFactory()
	{
		final var factory = createRealFactory();
		configureFactory(factory);
		return factory;
	}

	/**
	 * Creates a request factory implementation to delegate the request creation to. This is a real factory
	 * that creates requests.
	 *
	 * @return a factory instance to use for request creation. Note: this implementation returns
	 * {@link HttpComponentsClientHttpRequestFactory}. If a subclass overrides this method and returns a different
	 * implementation, then it should override the {@link #configureFactory(ClientHttpRequestFactory)} method too.
	 */
	protected ClientHttpRequestFactory createRealFactory()
	{
		final HttpClient client = clientFactory.create();
		return new HttpComponentsClientHttpRequestFactory(client);
	}

	/**
	 * Configures the factory instance created by {@link #createRealFactory()} method, if the configuration
	 * was injected into this class.
	 *
	 * @param f a factory implementation to configure
	 * @see #setConfiguration(OutboundServicesConfiguration)
	 */
	protected void configureFactory(final ClientHttpRequestFactory f)
	{
		LOG.info("Configuring {} with {}", f, configuration);
		if (configuration != null)
		{
			final var factory = (HttpComponentsClientHttpRequestFactory) f;
			factory.setConnectTimeout((int) configuration.getConnectionTimeout().toMillis());
			factory.setConnectionRequestTimeout((int) configuration.getRequestExecutionTimeout());
			factory.setReadTimeout((int) configuration.getRequestExecutionTimeout());
			LOG.info("Set configuration parameters:\nConnectionTimeout: {}\nConnectionRequestTimeout:  {}\nReadTimeout:{}",
					configuration.getConnectionTimeout(),
					configuration.getRequestExecutionTimeout(),
					configuration.getRequestExecutionTimeout());
		}
	}

	/**
	 * Injects configuration to be used for creating the requests. If the configuration is not provided, then
	 * the request factory won't be configured and will use default values.
	 *
	 * @param cfg configuration service implementation specifying the request parameters.
	 */
	public void setConfiguration(final OutboundServicesConfiguration cfg)
	{
		configuration = cfg;
	}

	/**
	 * Clears internal state. The call to this method is useful to apply configuration changes to the future HTTP requests.
	 */
	public void clear()
	{
		createdClients.clear();
	}
}
