/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.client.impl

import de.hybris.platform.core.Registry
import org.apache.http.impl.client.InternalHttpClient
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.pool.AbstractConnPool
import org.apache.http.pool.PoolStats
import org.springframework.http.client.ClientHttpRequestFactory

import java.lang.reflect.Field

/**
 * A utility for retrieving a connection pool in the context of a {@link org.springframework.web.client.RestTemplate}.
 */
final class ContextConnectionPool
{
	private ContextConnectionPool()
	{
		// non-instantiable
	}

	static PoolStats getPoolStats(String destinationUrl)
	{
		getPool(destinationUrl).totalStats
	}

	static AbstractConnPool getPool(String destinationUrl)
	{
		def requestFactory = getRequestFactory()
		def urlSpecificFactory = requestFactory.getRequestFactory new URI(destinationUrl)
		extractPool(urlSpecificFactory)
	}

	private static DefaultClientRequestFactory getRequestFactory()
	{
		Registry.applicationContext.getBean("defaultOutboundClientHttpRequestFactory", DefaultClientRequestFactory)
	}

	private static AbstractConnPool extractPool(ClientHttpRequestFactory factory)
	{
		Field cmField = InternalHttpClient.getDeclaredField('connManager')
		cmField.accessible = true
		def cm = cmField.get(factory.httpClient)

		Field poolField= PoolingHttpClientConnectionManager.getDeclaredField('pool')
		poolField.accessible = true
		poolField.get(cm) as AbstractConnPool
	}
}
