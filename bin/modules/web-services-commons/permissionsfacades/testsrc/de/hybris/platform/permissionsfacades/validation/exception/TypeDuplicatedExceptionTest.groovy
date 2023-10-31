/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.permissionsfacades.validation.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class TypeDuplicatedExceptionTest extends JUnitPlatformSpecification {
	
	@Test
	@Unroll
	def "exception message is in the expected format when type is #type"() {
		given:
		def e = new TypeDuplicatedException(type)

		expect:
		e.getMessage() == "Type $typeInMessage is duplicated."

		where:
		type          | typeInMessage
		"TestProduct" | "TestProduct"
		""            | ""
		null          | "null"
	}
}
