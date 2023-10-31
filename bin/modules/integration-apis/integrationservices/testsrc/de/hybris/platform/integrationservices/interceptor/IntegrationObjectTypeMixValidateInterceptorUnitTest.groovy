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
class IntegrationObjectTypeMixValidateInterceptorUnitTest extends JUnitPlatformSpecification {
	private static final def ITEM_CODE = "item"
	private static final def CLASS_CODE = "class"

	private static final def ITEM = new IntegrationObjectItemModel(code: ITEM_CODE)
	private static final def CLASS = new IntegrationObjectClassModel(code: CLASS_CODE)

	def validator = new IntegrationObjectTypeMixValidateInterceptor()

	@Test
	@Unroll
	def "no exception when integration object's classes=#classes and items=#items"() {
		given: "the integration object #condition"
		def io = Stub(IntegrationObjectModel) {
			getItems() >> items
			getClasses() >> classes
		}

		when:
		validator.onValidate io, Stub(InterceptorContext)

		then:
		noExceptionThrown()

		where:
		items  | classes
		null   | null
		[]     | []
		[ITEM] | []
		[]     | [CLASS]
	}

	@Test
	def 'throws exception when integration object has integration object item and class'() {
		given:
		def io = Stub(IntegrationObjectModel) {
			getItems() >> [ITEM]
			getClasses() >> [CLASS]
		}

		when:
		validator.onValidate io, Stub(InterceptorContext)

		then:
		def e = thrown InterceptorException
		e.message.contains(io.code)
		e.message.contains(ITEM_CODE)
		e.message.contains(CLASS_CODE)
	}
}
