/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class TypeRequiredExceptionTest extends JUnitPlatformSpecification {

	@Test
	def "message contains the index of missing type"() {
		given:
		def missingTypeIndex = 4
		def e = new TypeRequiredException(missingTypeIndex)

		expect:
		e.message == "Attribute 'type' is a required field for type permissions entry $missingTypeIndex."
	}
}
