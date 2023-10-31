/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class TypeNotFoundExceptionTest extends JUnitPlatformSpecification {

	@Test
	@Unroll
	def "instantiates the exception with type #type"() {
		given:
		def e = new TypeNotFoundException(type)

		expect:
		e.message == "Type $type is not found in the system."

		where:
		type << [null, '', ' ', 'someType']
	}
}
