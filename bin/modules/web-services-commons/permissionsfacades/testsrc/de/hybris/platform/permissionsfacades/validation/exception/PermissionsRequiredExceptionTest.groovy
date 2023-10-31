/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PermissionsRequiredExceptionTest extends JUnitPlatformSpecification {

	@Test
	@Unroll
	def "Exception message is \"#expectedMessage\" when type is \"#type\""() {
		given:
		def e = new PermissionsRequiredException(type)

		expect:
		expectedMessage == e.getMessage()

		where:
		type      | expectedMessage
		"Product" | "Attribute 'permissions' is a required field for type Product."
		""        | "Attribute 'permissions' is a required field for type permissions entry 0."
		null      | "Attribute 'permissions' is a required field for type permissions entry 0."
	}

	@Test
	@Unroll
	def "Exception message is \"#expectedMessage\" when item index is #index"() {
		given:
		def e = new PermissionsRequiredException(index)

		expect:
		expectedMessage == e.getMessage()

		where:
		index | expectedMessage
		1     | "Attribute 'permissions' is a required field for type permissions entry 1."
		0     | "Attribute 'permissions' is a required field for type permissions entry 0."
		-1    | "Attribute 'permissions' is a required field for type permissions entry 0."
	}
}
