/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.client.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundservices.client.OutboundHttpClientFactory
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

@UnitTest
class DefaultClientRequestFactoryUnitTest extends JUnitPlatformSpecification {
	private static final def SOME_URI = new URI('http://ignore.this')
	private static final def SOME_HTTP_METHOD = HttpMethod.POST

	def clientFactory = Stub OutboundHttpClientFactory
	def requestFactory = Spy new DefaultClientRequestFactory(clientFactory)

	@Test
	def 'cannot be instantiated with a null client factory'() {
		when:
		new DefaultClientRequestFactory(null)

		then:
		def e = thrown IllegalArgumentException
		e.message == 'OutboundHttpClientFactory is required'
	}

	@Test
	def 'uses a different request factory for a new request when destination host is different'() {
		given:
		def oneDestination = new URI('http://host1:8080')
		def anotherDestination = new URI('http://host2:8080')
		and:
		def requestFactory1 = null
		def requestFactory2 = null

		when:
		def request1 = requestFactory.createRequest oneDestination, SOME_HTTP_METHOD
		def request2 = requestFactory.createRequest anotherDestination, SOME_HTTP_METHOD

		then:
		1 * requestFactory.createRealFactory() >> { requestFactory1 = callRealMethod() }
		1 * requestFactory.createRealFactory() >> { requestFactory2 = callRealMethod() }
		and:
		!request1.is(request2)
		requestFactory1 != requestFactory2
	}

	@Test
	def 'uses the same request factory for a new request when destination host is same'() {
		given:
		def destination1 = new URI('http://host:8080')
		def destination2 = new URI('https://host:443')
		and:
		def requestFactory1 = null
		def requestFactory2 = null

		when:
		def request1 = requestFactory.createRequest destination1, SOME_HTTP_METHOD
		def request2 = requestFactory.createRequest destination2, SOME_HTTP_METHOD

		then:
		1 * requestFactory.getRequestFactory(destination1) >> { requestFactory1 = callRealMethod() }
		1 * requestFactory.getRequestFactory(destination2) >> { requestFactory2 = callRealMethod() }
		and:
		!request1.is(request2)
		requestFactory1 == requestFactory2
	}

	@Test
	def 'uses different request factory for a new request after clear() is called'() {
		given:
		def requestFactory1 = null
		def requestFactory2 = null
		when: 'a request is created'
		requestFactory.createRequest SOME_URI, SOME_HTTP_METHOD
		and: 'the factory state is cleared'
		requestFactory.clear()
		and: 'another request is created'
		requestFactory.createRequest SOME_URI, SOME_HTTP_METHOD
		then: 'the second request was created with a new factory'
		1 * requestFactory.getRequestFactory(SOME_URI) >> { requestFactory1 = callRealMethod() }
		1 * requestFactory.getRequestFactory(SOME_URI) >> { requestFactory2 = callRealMethod() }
		and:
		!requestFactory1.is(requestFactory2)
	}

	@Test
	@Unroll
	def "creates a request for a '#url' URI without a host"() {
		given:
		def destination = new URI(url)

		expect:
		requestFactory.createRequest destination, SOME_HTTP_METHOD

		where:
		url << ['localhost', '', 'http://:8080']
	}

	@Test
	def 'request factory is not configured when configuration service is not injected'() {
		given:
		requestFactory.configuration = null
		and:
		def expectedRequest = Stub ClientHttpRequest
		def factoryDelegate = Mock(HttpComponentsClientHttpRequestFactory) {
			createRequest(SOME_URI, SOME_HTTP_METHOD) >> expectedRequest
		}
		requestFactory.createRealFactory() >> factoryDelegate

		when:
		def createdRequest = requestFactory.createRequest SOME_URI, SOME_HTTP_METHOD

		then:
		createdRequest.is expectedRequest
		and: 'the factory setters were not called'
		0 * factoryDelegate.set_
	}

	@Test
	def 'request factory is configured when configuration service is injected'() {
		given:
		def configuration = Stub(OutboundServicesConfiguration) {
			getConnectionTimeout() >> Duration.ofSeconds(5)
			getRequestExecutionTimeout() >> 10000
		}
		requestFactory.configuration = configuration
		and:
		def factoryDelegate = Mock(HttpComponentsClientHttpRequestFactory) {
			createRequest(SOME_URI, SOME_HTTP_METHOD) >> Stub(ClientHttpRequest)
		}
		requestFactory.createRealFactory() >> factoryDelegate

		when:
		requestFactory.createRequest SOME_URI, SOME_HTTP_METHOD

		then: 'the factory is configured'
		1 * factoryDelegate.setConnectTimeout(configuration.connectionTimeout.toMillis())
		1 * factoryDelegate.setConnectionRequestTimeout(configuration.requestExecutionTimeout)
		1 * factoryDelegate.setReadTimeout(configuration.requestExecutionTimeout)
	}
}
