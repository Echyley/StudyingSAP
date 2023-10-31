/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.TypeRequiredException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class TypePopulatedValidationStrategyTest extends JUnitPlatformSpecification {

	def strategy = new TypePopulatedValidationStrategy()

	@Test
	@Unroll
	def "no exception is thrown when #description"() {
		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()

		where:
		description                     | typePermissionsList
		'type permissions list is null' | null
		'permissions list is null'      | typePermissionsList(null)
		'permissions list is empty'     | typePermissionsList([])
		'there are no empty types'      | typePermissionsList([typePermissions('type1'), typePermissions('type2')])
	}

	@Test
	@Unroll
	def "exception is thrown when there is no type set in position #position"() {
		given:
		def typePermissionsList = new TypePermissionsDataList(permissionsList: permissionsList)

		when:
		strategy.validate typePermissionsList

		then:
		def e = thrown TypeRequiredException
		e.message == "Attribute 'type' is a required field for type permissions entry $position."

		where:
		position | permissionsList
		1        | [typePermissions(''), typePermissions('aType')]
		2        | [typePermissions('aType'), typePermissions(null)]
	}

	def typePermissionsList(List typePermissions) {
		new TypePermissionsDataList(permissionsList: typePermissions)
	}

	def typePermissions(String type) {
		new TypePermissionsData(type: type)
	}
}
