/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.strategy.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.security.PrincipalModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.permissionsfacades.data.PermissionValuesData
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.strategy.ApplyPermissionsStrategy
import de.hybris.platform.permissionsfacades.validation.exception.*
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import org.junit.Test

import javax.annotation.Resource
import java.nio.charset.StandardCharsets

import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.*

@IntegrationTest
class ApplyPermissionsStrategyIntegrationTest extends AbstractPermissionsIntegrationTest {

	private static final PRINCIPAL_UID = "nonAdminUser"
	private static final TYPE = 'Address'
	PrincipalModel defaultPrincipal
	ComposedTypeModel defaultType
	@Resource
	ApplyPermissionsStrategy applyPermissionsStrategy

	def setup() {
		importData("/permissionsfacades/test/testpermissions.impex", StandardCharsets.UTF_8.name())
		defaultPrincipal = findPrincipal(PRINCIPAL_UID)
		defaultType = composedType(TYPE)
	}

	def cleanup() {
		permissionManagementService.removeTypePermissionsForPrincipal(defaultType, defaultPrincipal)
	}

	@Test
	def 'validation exception is thrown when principal is null'() {
		when:
		applyPermissionsStrategy.apply(typePermissionsList(
				[typePermissions('Product', permissions([(CREATE): true]))],
				null))

		then:
		thrown PrincipalRequiredException
	}

	@Test
	def 'exception is thrown when principal is not found'() {
		given:
		def nonExistingPrincipal = 'NonExistingPrincipal'

		when:
		applyPermissionsStrategy.apply(typePermissionsList(
				[typePermissions('Product', permissions([(CREATE): true]))],
				nonExistingPrincipal))

		then:
		thrown ModelNotFoundException
	}

	@Test
	def 'validation exception is thrown when permissionsList is not provided'() {
		given: 'null permissionsList causing PermissionsListRequiredException'
		def typePermissionsList = typePermissionsList(null)

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'validation exception is thrown'
		thrown PermissionsListRequiredException
		and: 'Product permissions is not persisted'
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Product'), defaultPrincipal)
	}

	@Test
	def 'validation exception is thrown when there are multiple of the same types'() {
		given: 'duplicate types causing TypeDuplicatedException'
		def unitTypePermission1 = typePermissions('unit', permissions([(READ): true]))
		def unitTypePermission2 = typePermissions('Unit', permissions([(CHANGE): false]))
		def productTypePermission = typePermissions('Product', permissions([(REMOVE): true]))
		def typePermissionsList = typePermissionsList([unitTypePermission1,
													   unitTypePermission2,
													   productTypePermission])

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'validation exception is thrown'
		def e = thrown TypeDuplicatedException
		e.message.contains 'unit'
		and: 'Product permissions is not persisted'
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Product'), defaultPrincipal)
	}

	@Test
	def 'validation exception is thrown when type is not provided'() {
		given: 'empty type causing TypeRequiredException'
		def noTypeTypePermission = typePermissions('', permissions([(CREATE): false]))
		def productTypePermission = typePermissions('Product', permissions([(REMOVE): true]))
		def typePermissionsList = typePermissionsList([noTypeTypePermission,
													   productTypePermission])

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'validation exception is thrown'
		def e = thrown TypeRequiredException
		e.message.contains 'entry 1'
		and: 'Product permissions is not persisted'
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Product'), defaultPrincipal)
	}

	@Test
	def 'validation exception is thrown when permissions are not provided'() {
		given: 'no permissions causing PermissionsRequiredException'
		def nullPermissionTypePermission = typePermissions('Catalog', null)
		def productTypePermission = typePermissions('Product', permissions([(REMOVE): true]))
		def typePermissionsList = typePermissionsList([nullPermissionTypePermission,
													   productTypePermission])

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'validation exception is thrown'
		def e = thrown PermissionsRequiredException
		e.message.contains 'Catalog'
		and: 'Product permissions is not persisted'
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Product'), defaultPrincipal)
	}

	@Test
	def 'permissions are not added when type is not found'() {
		given: 'non existing type causing TypeNotFoundException'
		def nonExistingType = 'NonExistingType'
		def nonExistingTypePermission = typePermissions(nonExistingType, permissions([(REMOVE): true]))
		def productTypePermission = typePermissions('Product', permissions([(REMOVE): true]))
		def typePermissionsList = typePermissionsList([nonExistingTypePermission,
													   productTypePermission])

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'exception is thrown'
		def e = thrown TypeNotFoundException
		e.message.contains nonExistingType
		and: 'Product permissions is not persisted'
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Product'), defaultPrincipal)
	}

	@Test
	def 'permissions are added to type when all validations passed'() {
		given:
		def addressTypePermissions = typePermissions(TYPE, permissions([(CREATE): true, (READ): true, (REMOVE): true, (CHANGE): true, (CHANGE_PERMISSIONS): true]))
		def typePermissionsList = typePermissionsList([addressTypePermissions], PRINCIPAL_UID)

		when:
		applyPermissionsStrategy.apply(typePermissionsList)

		then:
		def assignmentPermissions = permissionManagementService.getTypePermissionsForPrincipal(composedType(TYPE), defaultPrincipal)

		assignmentPermissions.find({ it.permissionName == CREATE }).granted
		assignmentPermissions.find({ it.permissionName == READ }).granted
		assignmentPermissions.find({ it.permissionName == REMOVE }).granted
		assignmentPermissions.find({ it.permissionName == CHANGE }).granted
		assignmentPermissions.find({ it.permissionName == CHANGE_PERMISSIONS }).granted
	}

	@Test
	def 'permissions are updated for a type when all validations passed'() {
		given: 'permissions to add for type'
		def originalAddressTypePermissions = typePermissions(TYPE, permissions([(CREATE): true, (READ): true, (REMOVE): true, (CHANGE): true, (CHANGE_PERMISSIONS): true]))
		def originalTypePermissionsList = typePermissionsList([originalAddressTypePermissions], PRINCIPAL_UID)
		and: 'permissions to update for the same type'
		def updateAddressTypePermissions = typePermissions(TYPE, permissions([(CREATE): false]))
		def updateTypePermissionsList = typePermissionsList([updateAddressTypePermissions], PRINCIPAL_UID)

		when: 'add the permissions for the type'
		applyPermissionsStrategy.apply(originalTypePermissionsList)

		then: 'added the permissions for the type'
		def originalAssignmentPermissions = permissionManagementService.getTypePermissionsForPrincipal(composedType(TYPE), defaultPrincipal)
		originalAssignmentPermissions.find({ it.permissionName == CREATE }).granted
		originalAssignmentPermissions.find({ it.permissionName == READ }).granted
		originalAssignmentPermissions.find({ it.permissionName == REMOVE }).granted
		originalAssignmentPermissions.find({ it.permissionName == CHANGE }).granted
		originalAssignmentPermissions.find({ it.permissionName == CHANGE_PERMISSIONS }).granted

		when: 'update the permissions for the same type'
		applyPermissionsStrategy.apply(updateTypePermissionsList)
		then: 'permissions are updated for the same type'
		def updatedAssignmentPermissions = permissionManagementService.getTypePermissionsForPrincipal(composedType(TYPE), defaultPrincipal)

		!updatedAssignmentPermissions.find({ it.permissionName == CREATE }).granted
		updatedAssignmentPermissions.find({ it.permissionName == READ }).granted
		updatedAssignmentPermissions.find({ it.permissionName == REMOVE }).granted
		updatedAssignmentPermissions.find({ it.permissionName == CHANGE }).granted
		updatedAssignmentPermissions.find({ it.permissionName == CHANGE_PERMISSIONS }).granted
	}

	@Test
	def 'all the permissions added in a transaction are rolled back if an error occurs'() {
		given: 'error will occur during adding read permission because it does not exist'
		removeReadPermission()
		and: 'permission that will be added without read permission'
		def addressTypePermissions = typePermissions(TYPE, permissions([(CREATE): true, (REMOVE): true, (CHANGE): true]))
		and: 'permission that will cause an error because of the read permission'
		def customerTypePermissions = typePermissions('Customer', permissions([(READ): true]))
		def typePermissionsList = typePermissionsList([addressTypePermissions, customerTypePermissions], PRINCIPAL_UID)

		when:
		applyPermissionsStrategy.apply typePermissionsList

		then: 'an exception is thrown'
		thrown IllegalArgumentException
		and: 'transaction is rolled back'
		!permissionManagementService.getTypePermissionsForPrincipal(defaultType, defaultPrincipal)
		!permissionManagementService.getTypePermissionsForPrincipal(composedType('Customer'), defaultPrincipal)
	}

	def permissions(Map permissionsToSet = [:]) {
		def permissions = new PermissionValuesData()
		permissions.change = permissionsToSet[CHANGE]
		permissions.create = permissionsToSet[CREATE]
		permissions.read = permissionsToSet[READ]
		permissions.remove = permissionsToSet[REMOVE]
		permissions.changerights = permissionsToSet[CHANGE_PERMISSIONS]
		permissions
	}

	def typePermissions(String type, PermissionValuesData permissions) {
		def typePermissions = new TypePermissionsData()
		typePermissions.type = type
		typePermissions.permissions = permissions
		typePermissions
	}

	def typePermissionsList(List typePermissions, String uid = PRINCIPAL_UID) {
		def typePermissionsList = new TypePermissionsDataList()
		typePermissionsList.permissionsList = typePermissions
		typePermissionsList.principalUid = uid
		typePermissionsList
	}

	def removeReadPermission() {
		importData("/permissionsfacades/test/removeReadPermission.impex", StandardCharsets.UTF_8.name())
	}
}
