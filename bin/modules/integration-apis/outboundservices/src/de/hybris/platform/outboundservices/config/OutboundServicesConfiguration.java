/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.config;

import de.hybris.platform.integrationservices.monitoring.MonitoringConfiguration;

import java.time.Duration;

/**
 * Configuration for Outbound Services extension
 */
public interface OutboundServicesConfiguration extends MonitoringConfiguration
{
	int DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE = 1024;
	long DEFAULT_REQUEST_EXECUTION_TIMEOUT_MS = 5000;
	int DEFAULT_MAX_POOL_CONNECTIONS = 5;
	Duration DEFAULT_CONNECTION_KEEP_ALIVE = Duration.ofMillis(500);
	Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(3);
	Duration DEFAULT_CONNECTION_VALID_PERIOD = Duration.ofMillis(500);

	/**
	 * Retrieves the maximum response payload size
	 *
	 * @return the maximum payload size
	 */
	default int getMaximumResponsePayloadSize()
	{
		return DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE;
	}

	/**
	 * Gets the outbound request timeout in milliseconds
	 *
	 * @return timeout value
	 */
	default long getRequestExecutionTimeout()
	{
		return DEFAULT_REQUEST_EXECUTION_TIMEOUT_MS;
	}

	/**
	 * Retrieves maximum number of connections in a connection pool for outbound communications.
	 *
	 * @return configured value for the max connection pool size or 5, if the max connection pool size is not configured or
	 * the value cannot be parsed.
	 */
	default int getMaxConnectionPoolSize()
	{
		return DEFAULT_MAX_POOL_CONNECTIONS;
	}

	/**
	 * Retrieves duration for keeping outbound connection alive. Read <a href="https://en.wikipedia.org/wiki/Keepalive">
	 * Connection Keep-Alive</a> for more information.
	 *
	 * @return configured value for keeping the connections alive or duration of half second, if the duration is
	 * not configured or cannot be parsed.
	 */
	default Duration getConnectionKeepAlive()
	{
		return DEFAULT_CONNECTION_KEEP_ALIVE;
	}

	/**
	 * Retrieves timeout for establishing an outbound connection.
	 *
	 * @return configured value for the connection timeout or duration of 3 second, if the timeout value is
	 * not configured or cannot be parsed.
	 */
	default Duration getConnectionTimeout()
	{
		return DEFAULT_CONNECTION_TIMEOUT;
	}

	/**
	 * Retrieves period of time, during which an idle connection in a connection pool is considered to be valid.
	 * Once that period is over, the connection may be validated before leasing it from the pool.
	 *
	 * @return configured value for the connection validity period or duration of half second, if the value is
	 * not configured or cannot be parsed.
	 */
	default Duration getIdleConnectionValidityPeriod()
	{
		return DEFAULT_CONNECTION_VALID_PERIOD;
	}
}
