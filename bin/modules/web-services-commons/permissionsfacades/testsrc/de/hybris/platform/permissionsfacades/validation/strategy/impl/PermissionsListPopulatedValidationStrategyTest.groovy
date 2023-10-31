/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.PermissionsListRequiredException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PermissionsListPopulatedValidationStrategyTest extends JUnitPlatformSpecification {

	def strategy = new PermissionsListPopulatedValidationStrategy()

	@Test
	@Unroll
	def "exception is not thrown when #description"() {
		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()

		where:
		description                     | typePermissionsList
		'type permissions list is null' | null
		'permissionsList is provided'   | new TypePermissionsDataList(permissionsList: [new TypePermissionsData()])
	}

	@Test
	@Unroll
	def "exception is thrown when #description"() {
		when:
		strategy.validate typePermissionsList

		then:
		thrown PermissionsListRequiredException

		where:
		description                | typePermissionsList
		'permissionsList is null'  | new TypePermissionsDataList(permissionsList: null)
		'permissionsList is empty' | new TypePermissionsDataList(permissionsList: [])
	}
}
