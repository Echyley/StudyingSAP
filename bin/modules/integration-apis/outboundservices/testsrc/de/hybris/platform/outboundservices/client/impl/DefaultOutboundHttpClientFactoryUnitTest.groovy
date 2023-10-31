/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.junit.Test

import java.time.Duration

@UnitTest
class DefaultOutboundHttpClientFactoryUnitTest extends JUnitPlatformSpecification
{
	@Test
	def 'created http client has characteristics of the configuration the factory was instantiated with'()
	{
		given:
		def configuration = Stub(OutboundServicesConfiguration) {
			getMaxConnectionPoolSize() >> 5
			getIdleConnectionValidityPeriod() >> Duration.ofMinutes(1)
			getConnectionTimeout() >> Duration.ofSeconds(5)
			getConnectionKeepAlive() >> Duration.ofSeconds(30)
		}
		and:
		def factory = Spy(DefaultOutboundHttpClientFactory, constructorArgs: [configuration])
		and:
		def capturedClient = null

		when:
		def client = factory.create()

		then:
		1 * factory.createClient(_, _, _) >> { List args ->
			with(args[0] as RequestConfig) {
				assert cookieSpec == 'ignoreCookies'
				assert connectTimeout == configuration.connectionTimeout.toMillis()
				assert connectionRequestTimeout == configuration.connectionTimeout.toMillis()
				assert socketTimeout == configuration.connectionTimeout.toMillis()
			}
			with(args[1] as PoolingHttpClientConnectionManager) {
				maxTotal == configuration.maxConnectionPoolSize
				defaultMaxPerRoute == configuration.maxConnectionPoolSize
				validateAfterInactivity == configuration.idleConnectionValidityPeriod.toMillis()
			}
			assert args[2] == configuration.connectionKeepAlive.toMillis()
			capturedClient = callRealMethod()
		}
		and:
		client.is capturedClient
	}

	@Test
	def 'created http client has characteristics set on the factory when the factory was instantiated with null configuration'()
	{
		given:
		def factory = Spy(new DefaultOutboundHttpClientFactory(null))
		factory.maxConnections = 4
		factory.timeout = 1000
		factory.validity = 2000
		factory.keepAlive = 3000
		and:
		def capturedClient = null

		when:
		def client = factory.create()

		then:
		1 * factory.createClient(_, _, _) >> { List args ->
			with(args[0] as RequestConfig) {
				assert cookieSpec == 'ignoreCookies'
				assert connectTimeout == factory.timeout
				assert connectionRequestTimeout == factory.timeout
				assert socketTimeout == factory.timeout
			}
			with(args[1] as PoolingHttpClientConnectionManager) {
				assert maxTotal == factory.maxConnections
				assert defaultMaxPerRoute == factory.maxConnections
				assert validateAfterInactivity == factory.validity
			}
			assert args[2] == factory.keepAlive
			capturedClient = callRealMethod()
		}
		and:
		client.is capturedClient
	}

	@Test
	def 'created http client has characteristics set on the factory when the factory was instantiated without a configuration'()
	{
		given:
		def factory = Spy(new DefaultOutboundHttpClientFactory(
				maxConnections: 4,
				timeout: 1000,
				validity: 2000,
				keepAlive: 3000))
		and:
		def capturedClient = null

		when:
		def client = factory.create()

		then:
		1 * factory.createClient(_, _, _) >> { List args ->
			with(args[0] as RequestConfig) {
				assert cookieSpec == 'ignoreCookies'
				assert connectTimeout == factory.timeout
				assert connectionRequestTimeout == factory.timeout
				assert socketTimeout == factory.timeout
			}
			with(args[1] as PoolingHttpClientConnectionManager) {
				assert maxTotal == factory.maxConnections
				assert defaultMaxPerRoute == factory.maxConnections
				assert validateAfterInactivity == factory.validity
			}
			assert args[2] == factory.keepAlive
			capturedClient = callRealMethod()
		}
		and:
		client.is capturedClient
	}
}
