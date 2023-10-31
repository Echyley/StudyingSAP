/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.TypeDuplicatedException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class TypesDuplicatedValidationStrategyTest extends JUnitPlatformSpecification {

	def strategy = new TypesDuplicatedValidationStrategy()

	@Test
	@Unroll
	def "no exception is thrown when #description"() {
		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()

		where:
		description                           | typePermissionsList
		'type permissions list is null'       | null
		'permissions list is null'            | typePermissionsList(null)
		'permissions list is empty'           | typePermissionsList([])
		'there are no duplicates'             | typePermissionsList([typePermissions('type1'), typePermissions('type2')])
		'there are duplicates of empty types' | typePermissionsList([typePermissions(''), typePermissions('')])
		'there are duplicates of null types'  | typePermissionsList([typePermissions(null), typePermissions(null)])
	}

	@Test
	def 'exception is thrown when there is a duplicate with case insensitivity'() {
		given:
		def typePermissionsList = typePermissionsList([typePermissions('type1'), typePermissions('type2'), typePermissions('TyPe1')])

		when:
		strategy.validate typePermissionsList

		then:
		def e = thrown TypeDuplicatedException
		e.message.contains 'type1'
	}

	@Test
	@Unroll
	def "exception is thrown when there are multiple duplicates with case insensitivity"() {
		given:
		def typePermissionsList = typePermissionsList([typePermissions('typE2'), typePermissions('type1'),
													   typePermissions('type2'), typePermissions('tYPe1')])

		when:
		strategy.validate typePermissionsList

		then:
		def e = thrown TypeDuplicatedException
		and: 'order of the doubles are maintained'
		e.message.contains 'typE2'
	}

	def typePermissionsList(List typePermissions) {
		new TypePermissionsDataList(permissionsList: typePermissions)
	}

	def typePermissions(String type) {
		new TypePermissionsData(type: type)
	}
}
