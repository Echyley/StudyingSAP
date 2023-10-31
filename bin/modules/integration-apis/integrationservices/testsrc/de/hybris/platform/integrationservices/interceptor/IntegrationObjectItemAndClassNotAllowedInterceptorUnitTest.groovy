/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class IntegrationObjectItemAndClassNotAllowedInterceptorUnitTest extends JUnitPlatformSpecification {

	private static final def IO_CODE = "io"
	private static final def CLASS_CODE = "class"

	private static final def CLASS = new IntegrationObjectClassModel(code: CLASS_CODE)

	def validator = new IntegrationObjectItemAndClassNotAllowedInterceptor()

	@Test
	@Unroll
	def "no exception when assigning Model type to integration object without POJO type"() {
		given: "the integration object #condition"
		def item = Stub(IntegrationObjectItemModel) {
			getIntegrationObject() >> Stub(IntegrationObjectModel) {
				getClasses() >> classes
			}
		}

		when:
		validator.onValidate item, Stub(InterceptorContext)

		then:
		noExceptionThrown()

		where:
		classes << [null, []]
	}

	@Test
	def 'throws exception when assigning Model type to integration object with POJO type'() {
		given:
		def item = Stub(IntegrationObjectItemModel) {
			getIntegrationObject() >> Stub(IntegrationObjectModel) {
				getCode() >> IO_CODE
				getClasses() >> [CLASS]
			}
		}

		when:
		validator.onValidate item, Stub(InterceptorContext)

		then:
		def e = thrown InterceptorException
		e.message.contains("Integration object [$IO_CODE] has integration object class(s) [$CLASS_CODE] assigned")
	}
}
