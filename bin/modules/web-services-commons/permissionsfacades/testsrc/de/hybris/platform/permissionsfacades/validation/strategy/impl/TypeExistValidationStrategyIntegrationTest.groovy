/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.validation.strategy.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.exception.TypeNotFoundException
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class TypeExistValidationStrategyIntegrationTest extends ServicelayerSpockSpecification {

	@Resource
	TypeExistValidationStrategy typeExistValidationStrategy

	@Test
	def 'no exception is thrown when the type is found'() {
		given:
		def typePermissionsList = typePermissionsList([typePermissions('Product')])

		when:
		typeExistValidationStrategy.validate typePermissionsList

		then:
		noExceptionThrown()
	}

	@Test
	def 'exception is thrown when the type is not found'() {
		given:
		def nonExistingType = 'nonExistingType'
		def typePermissionsList = typePermissionsList([typePermissions(nonExistingType)])

		when:
		typeExistValidationStrategy.validate typePermissionsList

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
