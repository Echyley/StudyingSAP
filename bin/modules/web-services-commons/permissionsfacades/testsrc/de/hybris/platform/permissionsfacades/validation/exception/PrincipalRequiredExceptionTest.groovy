/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class PrincipalRequiredExceptionTest extends JUnitPlatformSpecification {

	@Test
	def 'instantiates the exception'() {
		given:
		def e = new PrincipalRequiredException()

		expect:
		e.message == "Attribute 'principalUid' is a required field."
	}
}
