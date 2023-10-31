/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.PrincipalRequiredException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PrincipalPopulatedValidationStrategyTest extends JUnitPlatformSpecification {

	def strategy = new PrincipalPopulatedValidationStrategy()

	@Test
	def 'no exception is thrown when principal uid is provided'() {
		given:
		def typePermissionsList = new TypePermissionsDataList(principalUid: 'uid')

		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()
	}

	@Test
	@Unroll
	def "exception is throw when the principal uid is #uid"() {
		given:
		def typePermissionsList = new TypePermissionsDataList(principalUid: uid)

		when:
		strategy.validate typePermissionsList

		then:
		thrown PrincipalRequiredException

		where:
		uid << [null, '', ' ']
	}
}
