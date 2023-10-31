/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.PermissionValuesData
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.PermissionsRequiredException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PermissionsPopulatedValidationStrategyTest extends JUnitPlatformSpecification {

	def strategy = new PermissionsPopulatedValidationStrategy()

	@Test
	@Unroll
	def "no exception is thrown when there is #description"() {
		when:
		strategy.validate(permissions)

		then:
		noExceptionThrown()

		where:
		description              | permissions
		"null object"            | null
		"null permissions list"  | typePermissionsDataList(null)
		"empty permissions list" | typePermissionsDataList([])
		"permissions exist"      | typePermissionsDataList([typePermissions("Product", new PermissionValuesData())])
	}

	@Test
	@Unroll
	def "exception is thrown when there is #description"() {
		when:
		strategy.validate(permissions)

		then:
		def e = thrown(PermissionsRequiredException)
		e.message == errorMessage

		where:
		description                                  | permissions                                                                            | errorMessage
		"null permissions"                           | typePermissionsDataList([typePermissions("Product", null)])                            | "Attribute 'permissions' is a required field for type Product."
		"empty type and null permissions"            | typePermissionsDataList([typePermissions("", null)])                                   | "Attribute 'permissions' is a required field for type permissions entry 1."
		"null type and permissions"                  | typePermissionsDataList([typePermissions(null, null)])                                 | "Attribute 'permissions' is a required field for type permissions entry 1."
		"missing permissions, type, and permissions" | typePermissionsDataList([typePermissions("Product", null), typePermissions("", null)]) | "Attribute 'permissions' is a required field for type Product."
		"missing type, permissions, and permissions" | typePermissionsDataList([typePermissions("", null), typePermissions("Product", null)]) | "Attribute 'permissions' is a required field for type permissions entry 1."
	}

	def typePermissionsDataList(List<TypePermissionsData> permissionsList) {
		new TypePermissionsDataList(permissionsList: permissionsList)
	}

	def typePermissions(String type, PermissionValuesData permissions) {
		new TypePermissionsData(type: type, permissions: permissions)
	}
}
