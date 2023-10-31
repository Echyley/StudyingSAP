/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class PermissionsListRequiredExceptionTest extends JUnitPlatformSpecification {

	@Test
	def 'instantiate the exception'() {
		given:
		def e = new PermissionsListRequiredException()

		expect:
		e.message == "Attribute 'permissionsList' is a required field."
	}
}
