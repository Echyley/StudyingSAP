/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.strategy.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.security.PrincipalModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.permissionsfacades.data.PermissionValuesData
import de.hybris.platform.permissionsfacades.data.TypePermissionsData
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionsfacades.validation.strategy.TypePermissionsListValidationStrategy
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Unroll

import java.util.function.Consumer

import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.*

@UnitTest
class DefaultApplyPermissionsStrategyTest extends JUnitPlatformSpecification {

	private static final String PRINCIPAL_UID = 'principalUid'

	def flexibleSearchService = Mock FlexibleSearchService
	def validationStrategy = Mock TypePermissionsListValidationStrategy
	def permissionManagementService = Mock PermissionManagementService
	def typeService = Mock TypeService
	def sessionService = Mock(SessionService) {
		executeInLocalView(_ as SessionExecutionBody) >> { args ->
			(args[0] as SessionExecutionBody).executeWithoutResult()
		}
	}
	def transactionTemplate = Mock(TransactionTemplate) {
		executeWithoutResult(_ as Consumer) >> { args ->
			(args[0] as Consumer).accept(null)
		}
	}
	def permissionsStrategy = new DefaultApplyPermissionsStrategy(
			flexibleSearchService,
			permissionManagementService,
			typeService,
			sessionService,
			transactionTemplate,
			[validationStrategy])

	@Test
	@Unroll
	def "instantiation fails when #nullArg is null"() {
		when:
		new DefaultApplyPermissionsStrategy(
				flexibleSearchSvc,
				permMgntSvc,
				typeSvc,
				sessionSvc,
				transactionTmpl,
				strategies)

		then:
		def e = thrown IllegalArgumentException
		e.message == "Parameter $nullArg can not be null"

		where:
		flexibleSearchSvc            | permMgntSvc                       | typeSvc           | sessionSvc           | transactionTmpl           | strategies                                    | nullArg
		null                        | Stub(PermissionManagementService) | Stub(TypeService) | Stub(SessionService) | Stub(TransactionTemplate) | [Stub(TypePermissionsListValidationStrategy)] | 'flexibleSearchService'
		Stub(FlexibleSearchService) | null                              | Stub(TypeService) | Stub(SessionService) | Stub(TransactionTemplate) | [Stub(TypePermissionsListValidationStrategy)] | 'permissionManagementService'
		Stub(FlexibleSearchService) | Stub(PermissionManagementService) | null              | Stub(SessionService) | Stub(TransactionTemplate) | [Stub(TypePermissionsListValidationStrategy)] | 'typeService'
		Stub(FlexibleSearchService) | Stub(PermissionManagementService) | Stub(TypeService) | null                 | Stub(TransactionTemplate) | [Stub(TypePermissionsListValidationStrategy)] | 'sessionService'
		Stub(FlexibleSearchService) | Stub(PermissionManagementService) | Stub(TypeService) | Stub(SessionService) | null                      | [Stub(TypePermissionsListValidationStrategy)] | 'transactionTemplate'
		Stub(FlexibleSearchService) | Stub(PermissionManagementService) | Stub(TypeService) | Stub(SessionService) | Stub(TransactionTemplate) | null                                          | 'validationStrategies'
	}

	@Test
	def 'exception is thrown when type permissions list is null'() {
		when:
		permissionsStrategy.apply null

		then:
		def e = thrown IllegalArgumentException
		e.message == "Parameter permissionsList can not be null"
	}

	@Test
	def 'permissions are not set when permissions list validation fails'() {
		given: 'mock validation fails'
		def validationException = new RuntimeException()
		validationStrategy.validate(_ as TypePermissionsDataList) >> { throw validationException }

		when:
		permissionsStrategy.apply typePermissionsDataList()

		then: 'assert exception is thrown for validation errors'
		def e = thrown RuntimeException
		e == validationException
		and: 'services not invoked'
		0 * flexibleSearchService._
		0 * permissionManagementService._
		0 * typeService._
		0 * sessionService._
		0 * transactionTemplate._
	}

	@Test
	@Unroll
	def "permissions are not set when searching for the principal throws an exception"() {
		given: 'mock principal error'
		flexibleSearchService.getModelByExample(_ as PrincipalModel) >> { throw new ModelNotFoundException('IGNORE-Testing Exception') }

		when:
		permissionsStrategy.apply typePermissionsDataList()

		then: 'exception is thrown'
		thrown ModelNotFoundException
		and: 'nothing else is invoked'
		0 * permissionManagementService._
		0 * typeService._
		0 * sessionService._
		0 * transactionTemplate._
		1 * validationStrategy.validate(_ as TypePermissionsDataList)
	}

	@Test
	@Unroll
	def "permissions are not set when type is not found"() {
		given: 'mock type error'
		def nonExistingType = 'NonExistingType'
		typeService.getComposedTypeForCode(nonExistingType) >> { throw new UnknownIdentifierException('IGNORE-Testing Exception') }
		and:'mock found principal'
		def principal = principal()
		flexibleSearchService.getModelByExample(_ as PrincipalModel) >> principal
		and:
		def permissionsToUpdate = typePermissionsDataList()
		permissionsToUpdate.permissionsList = [typePermission(nonExistingType, permissions([(CHANGE): true]))]

		when:
		permissionsStrategy.apply permissionsToUpdate

		then: 'exception is thrown'
		thrown UnknownIdentifierException
		and: 'no adding of permissions'
		0 * permissionManagementService._
	}

	@Test
	def 'permissions are applied'() {
		given: 'mock found principal'
		def principal = principal()
		flexibleSearchService.getModelByExample(_ as PrincipalModel) >> principal
		and: 'mock found types'
		def addressType = Stub(ComposedTypeModel) {
			getCode() >> 'Address'
		}
		typeService.getComposedTypeForCode(addressType.code) >> addressType
		def customerType = Stub(ComposedTypeModel) {
			getCode() >> 'Customer'
		}
		typeService.getComposedTypeForCode(customerType.code) >> customerType
		and: 'permissions to update'
		def permissionsToUpdate = typePermissionsDataList()
		permissionsToUpdate.permissionsList = [
				typePermission(addressType.code, permissions([(CHANGE): true])),
				typePermission(customerType.code, permissions([(READ): true, (CREATE): true]))
		]

		when:
		permissionsStrategy.apply permissionsToUpdate

		then: 'assert only 1 permission is being set for address'
		1 * permissionManagementService.addTypePermissions(addressType, _ as Collection) >> { args ->
			def perms = args[1] as Collection<PermissionAssignment>
			assert perms.size() == 1
			and:
			"$CHANGE permissions is assigned to the correct value and principal"
			with(perms.find({ it.permissionName == CHANGE })) {
				granted == permissionsToUpdate.getPermissionsList().get(0).getPermissions().change
				principal == principal
			}
			and:
			"$READ permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == READ })
			and:
			"$CREATE permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == CREATE })
			and:
			"$REMOVE permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == REMOVE })
			and:
			"$CHANGE_PERMISSIONS permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == CHANGE_PERMISSIONS })
		}
		and: 'assert only 2 permissions are being set for customer'
		1 * permissionManagementService.addTypePermissions(customerType, _ as Collection) >> { args ->
			def perms = args[1] as Collection<PermissionAssignment>
			assert perms.size() == 2
			and:
			"$READ permissions is assigned to the correct value and principal"
			with(perms.find({ it.permissionName == READ })) {
				granted == permissionsToUpdate.getPermissionsList().get(1).getPermissions().read
				principal == principal
			}
			and:
			"$CREATE permissions is assigned to the correct value and principal"
			with(perms.find({ it.permissionName == CREATE })) {
				granted == permissionsToUpdate.getPermissionsList().get(1).getPermissions().create
				principal == principal
			}
			and:
			"$CHANGE permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == CHANGE })
			and:
			"$REMOVE permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == REMOVE })
			and:
			"$CHANGE_PERMISSIONS permissions was NOT assigned to principal"
			assert !perms.find({ it.permissionName == CHANGE_PERMISSIONS })
		}
	}

	def typePermissionsDataList(String principalUid = PRINCIPAL_UID, List permissionsList = []) {
		new TypePermissionsDataList(principalUid: principalUid, permissionsList: permissionsList)
	}

	def typePermission(String type, PermissionValuesData permissions) {
		new TypePermissionsData(
				type: type,
				permissions: permissions
		)
	}

	def permissions(Map perm = [(READ): false, (CHANGE): false, (CREATE): false, (REMOVE): false]) {
		new PermissionValuesData(
				read: perm[READ],
				change: perm[CHANGE],
				create: perm[CREATE],
				remove: perm[REMOVE]
		)
	}

	def principal(String uid = PRINCIPAL_UID) {
		Stub(PrincipalModel) {
			getUid() >> uid
		}
	}
}
