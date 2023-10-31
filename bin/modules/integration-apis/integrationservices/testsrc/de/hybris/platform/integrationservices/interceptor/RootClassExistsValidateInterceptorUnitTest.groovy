/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class RootClassExistsValidateInterceptorUnitTest extends JUnitPlatformSpecification {

	private static final def IO_CLASS = new IntegrationObjectClassModel()

	def validator = new RootClassExistsValidateInterceptor()

	@Test
	def "no exception when integration object's classes=#classes and rootClass=#rootClass"() {
		given:
		def io = Stub(IntegrationObjectModel) {
			getRootClass() >> rootClass
			getClasses() >> classes
		}

		when:
		validator.onValidate io, Stub(InterceptorContext)

		then:
		noExceptionThrown()

		where:
		classes    | rootClass
		null       | null
		[]         | null
		[IO_CLASS] | IO_CLASS
	}

	@Test
	def 'throws exception when integration object has no root class'() {
		given:
		def io = Stub(IntegrationObjectModel) {
			getRootClass() >> null
			getClasses() >> [IO_CLASS]
			getCode() >> 'IO'
		}

		when:
		validator.onValidate io, Stub(InterceptorContext)

		then:
		def e = thrown InterceptorException
		e.message.contains("IntegrationObject [$io.code] does not have a root class assigned")
	}
}
