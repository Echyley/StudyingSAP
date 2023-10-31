/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.TypeNotFoundException
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class TypeExistValidationStrategyTest extends JUnitPlatformSpecification {

	def typeService = Mock TypeService
	def strategy = new TypeExistValidationStrategy(typeService)

	@Test
	def 'exception is thrown when instantiating the strategy with null type service'() {
		when:
		new TypeExistValidationStrategy(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	@Unroll
	def "no exception is thrown when #description"() {
		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()
		and:
		0 * typeService.getComposedTypeForCode(_ as String)

		where:
		description                     | typePermissionsList
		'type permissions list is null' | null
		'permissions list is null'      | typePermissionsList(null)
		'permissions list is empty'     | typePermissionsList([])
	}

	@Test
	@Unroll
	def "no exception is thrown when the type is #typeName"() {
		given:
		def typePermissionsList = typePermissionsList([typePermissions(typeName)])

		when:
		strategy.validate typePermissionsList

		then:
		noExceptionThrown()
		and: "type service is invoked $typeServiceInvokeTimes times"
		typeServiceInvokeTimes * typeService.getComposedTypeForCode(typeName)

		where:
		typeName    | typeServiceInvokeTimes
		null        | 0
		''          | 0
		'validType' | 1

	}

	@Test
	def 'exception is thrown when the type is not found'() {
		given: 'mocking type service throwing an exception when the type is not found'
		def nonExistingType = 'nonExistingType'
		typeService.getComposedTypeForCode(nonExistingType) >> { throw new UnknownIdentifierException('IGNORE-Testing exception') }
		and:
		def typePermissionsList = typePermissionsList([typePermissions(nonExistingType)])

		when:
		strategy.validate typePermissionsList

		then:
		def e = thrown TypeNotFoundException
		e.message.contains nonExistingType
	}

	def typePermissionsList(List typePermissions) {
		new TypePermissionsDataList(permissionsList: typePermissions)
	}

	def typePermissions(String type) {
		new TypePermissionsData(type: type)
	}
}
