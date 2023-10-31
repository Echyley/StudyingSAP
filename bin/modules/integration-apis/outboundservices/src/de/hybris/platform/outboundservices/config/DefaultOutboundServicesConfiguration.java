/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.config;

import de.hybris.platform.integrationservices.config.BaseIntegrationServicesConfiguration;

import java.time.Duration;

/**
 * Provides access methods to configurations related to the Outbound Services
 */
public class DefaultOutboundServicesConfiguration extends BaseIntegrationServicesConfiguration implements OutboundServicesConfiguration
{
	private static final String PAYLOAD_RETENTION_SUCCESS_KEY = "outboundservices.monitoring.success.payload.retention";
	private static final String PAYLOAD_RETENTION_ERROR_KEY = "outboundservices.monitoring.error.payload.retention";

	private static final boolean PAYLOAD_RETENTION_SUCCESS_FALLBACK = false;
	private static final boolean PAYLOAD_RETENTION_ERROR_FALLBACK = true;

	private static final String MONITORING_ENABLED_KEY = "outboundservices.monitoring.enabled";
	private static final boolean MONITORING_ENABLED_FALLBACK = false;

	private static final String MAX_RESPONSE_PAYLOAD_SIZE_KEY = "outboundservices.response.payload.max.size.bytes";
	private static final int MAX_RESPONSE_PAYLOAD_SIZE_FALLBACK = 1024;

	private static final String REQUEST_EXECUTION_TIMEOUT_KEY = "outboundservices.request.execution.timeout.millisecs";
	private static final long REQUEST_EXECUTION_TIMEOUT_MILLISECS_FALLBACK = DEFAULT_REQUEST_EXECUTION_TIMEOUT_MS;

	private static final String MAX_CONNECTION_POOL_SIZE_KEY = "outboundservices.httpclient.max.connections";
	private static final String CONNECTION_KEEP_ALIVE_MILLISEC_KEY = "outboundservices.httpclient.connections.keep-alive";
	private static final String CONNECTION_TIMEOUT_MILLISEC_KEY = "outboundservices.httpclient.connections.connectionTimeout";
	private static final String CONNECTION_VALID_MILLISEC_KEY = "outboundservices.httpclient.connections.validity";

	@Override
	public boolean isPayloadRetentionForSuccessEnabled()
	{
		return getBooleanProperty(PAYLOAD_RETENTION_SUCCESS_KEY, PAYLOAD_RETENTION_SUCCESS_FALLBACK);
	}

	@Override
	public boolean isPayloadRetentionForErrorEnabled()
	{
		return getBooleanProperty(PAYLOAD_RETENTION_ERROR_KEY, PAYLOAD_RETENTION_ERROR_FALLBACK);
	}

	@Override
	public boolean isMonitoringEnabled()
	{
		return getBooleanProperty(MONITORING_ENABLED_KEY, MONITORING_ENABLED_FALLBACK);
	}

	@Override
	public int getMaximumResponsePayloadSize()
	{
		return getIntegerProperty(MAX_RESPONSE_PAYLOAD_SIZE_KEY, MAX_RESPONSE_PAYLOAD_SIZE_FALLBACK);
	}

	/**
	 * Sets the value of the maximum payload size accepted in outbound responses.
	 *
	 * @param maxPayloadSize Number of bytes allowed in the response payload
	 */
	public void setMaximumResponsePayloadSize(final int maxPayloadSize)
	{
		setProperty(MAX_RESPONSE_PAYLOAD_SIZE_KEY, maxPayloadSize);
	}

	@Override
	public long getRequestExecutionTimeout()
	{
		return getLongProperty(REQUEST_EXECUTION_TIMEOUT_KEY, REQUEST_EXECUTION_TIMEOUT_MILLISECS_FALLBACK);
	}

	/**
	 * Sets the outbound request timeout in milliseconds
	 *
	 * @param timeout The number of milliseconds the outbound request can execute before timing out
	 */
	public void setRequestExecutionTimeout(final long timeout)
	{
		final var value = timeout < 0 ? REQUEST_EXECUTION_TIMEOUT_MILLISECS_FALLBACK : timeout;
		setProperty(REQUEST_EXECUTION_TIMEOUT_KEY, value);
	}

	@Override
	public int getMaxConnectionPoolSize()
	{
		return getIntegerProperty(MAX_CONNECTION_POOL_SIZE_KEY, DEFAULT_MAX_POOL_CONNECTIONS);
	}

	/**
	 * Changes configuration for the maximum connection pool size. Changing this value does not guarantee being applied to the
	 * already existing connections pools. However, it should affect new pools created by services using this configuration.
	 *
	 * @param value new maximum number of connections in the pool.
	 */
	public void setMaxConnectionPoolSize(final int value)
	{
		setProperty(MAX_CONNECTION_POOL_SIZE_KEY, value);
	}

	@Override
	public Duration getConnectionKeepAlive()
	{
		final var timeMillisec = getLongProperty(CONNECTION_KEEP_ALIVE_MILLISEC_KEY, -1);
		return timeMillisec < 0 ? DEFAULT_CONNECTION_KEEP_ALIVE : Duration.ofMillis(timeMillisec);
	}

	/**
	 * Configures period of time to keep outbound connections alive.
	 *
	 * @param d duration to keep the connections alive. {@code null} value resets the configuration to default.
	 * @see #getConnectionKeepAlive()
	 */
	public void setConnectionKeepAlive(final Duration d)
	{
		final var time = d == null ? DEFAULT_CONNECTION_KEEP_ALIVE : d;
		setConnectionKeepAlive(time.toMillis());
	}

	/**
	 * Configures period of time to keep outbound connections alive.
	 *
	 * @param millisec number of milliseconds to keep the connections alive. A negative value resets the configuration to default.
	 * @see #getConnectionKeepAlive()
	 */
	public void setConnectionKeepAlive(final long millisec)
	{
		setProperty(CONNECTION_KEEP_ALIVE_MILLISEC_KEY, millisec);
	}

	@Override
	public Duration getConnectionTimeout()
	{
		final var timeoutMillisec = getLongProperty(CONNECTION_TIMEOUT_MILLISEC_KEY, -1);
		return timeoutMillisec < 0 ? DEFAULT_CONNECTION_TIMEOUT : Duration.ofMillis(timeoutMillisec);
	}

	/**
	 * Configures timeout for establishing an outbound connection.
	 *
	 * @param d time to wait for a connection can be established. {@code null} value resets the configuration to default.
	 */
	public void setConnectionTimeout(final Duration d)
	{
		final var time = d == null ? DEFAULT_CONNECTION_TIMEOUT : d;
		setConnectionTimeout(time.toMillis());
	}

	/**
	 * Configures timeout for establishing an outbound connection.
	 *
	 * @param millisec number of milliseconds to wait for a connection to be established. A negative value resets the
	 *                 configuration to default.
	 */
	public void setConnectionTimeout(final long millisec)
	{
		setProperty(CONNECTION_TIMEOUT_MILLISEC_KEY, millisec);
	}

	@Override
	public Duration getIdleConnectionValidityPeriod()
	{
		final var timeoutMillisec = getLongProperty(CONNECTION_VALID_MILLISEC_KEY, -1);
		return timeoutMillisec < 0 ? DEFAULT_CONNECTION_VALID_PERIOD : Duration.ofMillis(timeoutMillisec);
	}

	/**
	 * Configures a time period, during which an idle connection in a pool is considered valid.
	 *
	 * @param d time, during which an idle connection is valid. {@code null} value resets the configuration to default.
	 * @see #getIdleConnectionValidityPeriod()
	 */
	public void setIdleConnectionValidityPeriod(final Duration d)
	{
		final var time = d == null ? DEFAULT_CONNECTION_VALID_PERIOD : d;
		setIdleConnectionValidityPeriod(time.toMillis());
	}

	/**
	 * Configures a time period, during which an idle outbound connection in a connection pools is considered to be valid.
	 *
	 * @param millisec number of milliseconds an idle connection should be valid. A negative value resets the
	 *                 configuration to default.
	 */
	public void setIdleConnectionValidityPeriod(final long millisec)
	{
		setProperty(CONNECTION_VALID_MILLISEC_KEY, millisec);
	}
}
