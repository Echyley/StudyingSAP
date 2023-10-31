/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException
import org.junit.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@UnitTest
class PojoIntegrationObjectInterceptorUnitTest extends JUnitPlatformSpecification {
	private static final def IO = 'IntegrationObj'
	private static final def CLASS_CODE = 'class'
	private static final def ROOT_CLASS = 'Root'
	private static final def CLASS = new IntegrationObjectClassModel(code: CLASS_CODE)
	private static final def METADATA = '$metadata'
	private static final def NOT_ALLOWED_ERROR_MSG = 'Only $metadata requests are supported for Integration Objects modeled with IntegrationObjectClass'
    private static final def ERROR_CODE = 'inaccessible_integration_object'

    def response = Stub(HttpServletResponse)
	def service = Stub(IntegrationObjectService)

	def interceptor = new PojoIntegrationObjectInterceptor(service)

	@Test
	void "Cannot be instantiated when IntegrationObjectService is null"() {
		when:
		new PojoIntegrationObjectInterceptor(null)

		then:
		def e = thrown NullPointerException
		e.message == 'IntegrationObjectService cannot be null'
	}

	@Test
	void "no exception reported for GET metadata when IO is modeled with IOC"() {
		given:
		def request = request('get', uri)

		and:
		service.findIntegrationObject(IO) >> Stub(IntegrationObjectModel) {
			getClasses() >> [CLASS]
		}

		expect:
		interceptor.preHandle request, response, new Object()

		where:
		uri << ["/$IO/$METADATA", "/$IO/$METADATA?$ROOT_CLASS"]
	}

	@Test
	void "no exception reported when #method request is made for IO is modeled with IOI"() {
		given:
		def request = request(method, "/$IO/$ROOT_CLASS")

		and:
		service.findIntegrationObject(IO) >> Stub(IntegrationObjectModel) {
			getClasses() >> []
		}

		expect:
		interceptor.preHandle request, response, new Object()

		where:
		method << ['get', 'delete', 'post','patch', null]
	}

	@Test
	void "exception is thrown when #method request is made for IO modeled with IOC"() {
		given:
		def request = request(method, "/$IO/$ROOT_CLASS")

		and:
		service.findIntegrationObject(IO) >> Stub(IntegrationObjectModel) {
			getClasses() >> [CLASS]
		}

		when:
		interceptor.preHandle request, response, new Object()

		then:
		def exception = thrown ODataRuntimeApplicationException
        exception.httpStatus == HttpStatusCodes.METHOD_NOT_ALLOWED
        exception.code == ERROR_CODE
        exception.locale == Locale.ENGLISH
		exception.message == NOT_ALLOWED_ERROR_MSG

		where:
		method << ['get', 'patch', 'delete', 'post', null]
	}

	def request(String method, String uri) {
		Stub(HttpServletRequest) {
			getMethod() >> method
			getPathInfo() >> uri
		}
	}
}
