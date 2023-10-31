/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class SingleRootClassValidateInterceptorUnitTest extends JUnitPlatformSpecification {
	private static final def IO_CODE = "TestProduct"
	private static final def CLASS_CODE = "Product"
	private static final def CLASS_CODE_2 = "Catalog"

	def io = Stub(IntegrationObjectModel) {
		getCode() >> IO_CODE
	}

	def rootClassInterceptor = new SingleRootClassValidateInterceptor()

	@Test
	def "new root class throws exception when a different root already exists for the same IO"() {
		given:
		io.getRootClass() >> rootClass(io, CLASS_CODE)

		when:
		rootClassInterceptor.onValidate(rootClass(io, CLASS_CODE_2), null)

		then:
		def e = thrown InterceptorException
		e.message.contains(IO_CODE)
	}

	@Test
	def "Successfully validates the same root class that already exists"() {
		given:
		io.getRootClass() >> rootClass(io, CLASS_CODE)

		when:
		rootClassInterceptor.onValidate(rootClass(io, CLASS_CODE), null)

		then:
		noExceptionThrown()
	}

	@Test
	def "validates a new root class when no other classes exist"() {
		given:
		io.getRootClass() >> null

		when:
		rootClassInterceptor.onValidate(rootClass(io, CLASS_CODE), null)

		then:
		noExceptionThrown()
	}

	@Test
	def "validates a new non-root class when no other classes exist"() {
		given:
		def newClass = nonRootClass(CLASS_CODE_2)

		when:
		rootClassInterceptor.onValidate(newClass, null)

		then:
		noExceptionThrown()
	}

	private IntegrationObjectClassModel nonRootClass(final String classCode) {
		new IntegrationObjectClassModel(code: classCode, root: false)
	}

	private IntegrationObjectClassModel rootClass(final IntegrationObjectModel io, final String classCode) {
		new IntegrationObjectClassModel(code: classCode, root: true, integrationObject: io)
	}
}
