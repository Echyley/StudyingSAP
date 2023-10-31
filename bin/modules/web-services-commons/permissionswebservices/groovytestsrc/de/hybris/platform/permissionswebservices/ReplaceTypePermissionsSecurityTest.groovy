/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionswebservices


import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.security.PrincipalModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants
import de.hybris.platform.permissionswebservices.dto.TypePermissionsListWsDTO
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.servicelayer.user.UserService
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import groovy.json.JsonBuilder
import org.junit.Test
import org.springframework.http.MediaType
import spock.lang.Unroll

import javax.annotation.Resource
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Response
import java.nio.charset.StandardCharsets

import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CHANGE
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CHANGE_PERMISSIONS
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.CREATE
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.READ
import static de.hybris.platform.servicelayer.security.permissions.PermissionsConstants.REMOVE

@NeedsEmbeddedServer(webExtensions = [PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME])
@IntegrationTest
class ReplaceTypePermissionsSecurityTest extends ServicelayerSpockSpecification {

	private static final String BASE_PERMISSION_PATH = "v1/permissions/types"

	private static final String CLIENT_ID = "mobile_android"
	private static final String CLIENT_SECRET = "secret"

	private static final String GROUP_ADMIN = "admingroup"
	private static final String GROUP_NON_ADMIN = "supergroup"
	private static final String ADMIN_USER = "adminEmployee"
	private static final String NON_ADMIN_USER = "nonAdminEmployee"
	private static final String PASSWORD = "1234"

	private static final String PRODUCT = "Product"

	@Resource
	UserService userService
	@Resource
	PermissionManagementService permissionManagementService
	@Resource
	TypeService typeService

	PrincipalModel nonAdminPrincipal
	ComposedTypeModel productType

	def setup() {
		importData("/permissionswebservices/test/testpermissions.impex", StandardCharsets.UTF_8.name())
		nonAdminPrincipal = userService.getUserForUID(NON_ADMIN_USER)
		productType = typeService.getComposedTypeForCode(PRODUCT)
	}

	def cleanup() {
		permissionManagementService.removeTypePermissionsForPrincipal productType, nonAdminPrincipal
	}

	@Test
	@Unroll
	def "#user user gets #statusCode for replace permissions for self"() {
		given:
		def json = new JsonBuilder(
				principalUid: user,
				permissionsList: [permissionForTypeWithReadTrue(PRODUCT)]
		)

		when:
		def response = putRequestWithBody(json, user)

		then:
		WebservicesAssert.assertResponse(statusCode, response)

		where:
		user           | group           | statusCode
		ADMIN_USER     | GROUP_ADMIN     | Response.Status.OK
		NON_ADMIN_USER | GROUP_NON_ADMIN | Response.Status.FORBIDDEN
	}

	@Test
	def "User gets expected response indicating all permissions when replace read permission for other non-admin user"() {
		given: "a non-admin principalUid"
		def json = new JsonBuilder(
				principalUid: NON_ADMIN_USER,
				permissionsList: [permissionsForType(PRODUCT,
						[(READ)              : true,
						 (CHANGE)            : true,
						 (CREATE)            : true,
						 (REMOVE)            : true,
						 (CHANGE_PERMISSIONS): true])]
		)

		when: "user replaces permissions for other non-admin user"
		def response = putRequestWithBody(json)

		then: "a success response code is returned"
		WebservicesAssert.assertResponse(Response.Status.OK, response)
		and: "the response payload includes all permissions for the specified type"
		final TypePermissionsListWsDTO entity = response.readEntity(TypePermissionsListWsDTO.class)
		entity.getPermissionsList().size() == 1
		with(entity.getPermissionsList().get(0)) {
			type == PRODUCT
			with(permissions) {
				read
				change
				create
				remove
				changerights
			}
		}
	}

	@Test
	def "Replace permissions for type with empty string sets permission to false"() {
		given:
		permissionManagementService.addTypePermission productType,
				permissionAssignment(nonAdminPrincipal, READ, true)
		def productPermissions = permissionManagementService.getTypePermissionsForPrincipal productType, nonAdminPrincipal
		productPermissions.size() == 1
		productPermissions.find({ it.permissionName == READ }).granted
		and: "a non-admin principalUid"
		def json = new JsonBuilder(
				principalUid: NON_ADMIN_USER,
				permissionsList: [[type: PRODUCT, permissions: [read: ""]]]
		)

		when: "user replaces permissions with read set to empty string"
		def response = putRequestWithBody(json)

		then: "a success response code is returned"
		WebservicesAssert.assertResponse(Response.Status.OK, response)
		and: "the response payload includes all permissions for the specified type"
		final TypePermissionsListWsDTO entity = response.readEntity(TypePermissionsListWsDTO.class)
		entity.getPermissionsList().size() == 1
		with(entity.getPermissionsList().get(0)) {
			type == PRODUCT
			with(permissions) {
				!read
				!change
				!create
				!remove
				!changerights
			}
		}
	}

	@Test
	def "User gets expected error response when sending replace for a user that does not exist"() {
		given: "an non-existing principalUid"
		def nonExistingUid = 'NON-EXISTING-USER-ID'
		def json = new JsonBuilder(
				principalUid: nonExistingUid,
				permissionsList: [permissionForTypeWithReadTrue(PRODUCT)]
		)

		when: "a replace is made for a non-existing User.uid"
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		errorsList.errors[0].type == 'ModelNotFoundError'
	}

	@Test
	def "User gets expected error response when sending replace for a null user"() {
		given: "a null principalUid"
		def json = new JsonBuilder(
				principalUid: null,
				permissionsList: [permissionForTypeWithReadTrue(PRODUCT)]
		)

		when: "a replace is made for a null uid"
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			message == "Attribute 'principalUid' is a required field."
			type == 'PrincipalRequiredError'
		}
	}

	@Test
	def "User gets expected error response when sending replace for a type that does not exist"() {
		given: "an non-existing type code"
		def nonExistingTypeCode = 'NON-EXISTING-TYPE-CODE'
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				permissionsList: [[type: nonExistingTypeCode, permissions: [read: false]]]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			type == 'TypeNotFoundError'
			message == "Type $nonExistingTypeCode is not found in the system."
		}
	}

	@Test
	def "User gets expected error response when sending replace with not providing the 'permissionsList' property"() {
		given:
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				NOTpermissionsList: [permissionForTypeWithReadTrue(PRODUCT)]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		and: 'permissionsList not provided error'
		with(errorsList.errors[0]) {
			type == 'PermissionsListRequiredError'
			message == "Attribute 'permissionsList' is a required field."
		}
	}

	@Test
	def "User gets expected error response when attempting to replace permission with non-boolean value"() {
		given:
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				permissionsList: [[type: PRODUCT, permissions: [read: "notABooleanValue"]]]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			message == 'The application has encountered an error'
			type == 'HttpMessageNotReadableError'
		}
	}

	@Test
	def "User gets expected error response when sending replace with duplicate types"() {
		given:
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				permissionsList: [permissionForTypeWithReadTrue(PRODUCT),
								  permissionForTypeWithReadTrue('prodUCT')]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			type == 'TypeDuplicatedError'
			message == "Type $PRODUCT is duplicated."
		}
	}

	@Test
	def "User gets expected error response when sending replace with missing type"() {
		given:
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				permissionsList: [[permissions: [read: true]]]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			type == 'TypeRequiredError'
			message == "Attribute 'type' is a required field for type permissions entry 1."
		}
	}

	@Test
	def "User gets expected error response when attempting to replace permission with no permissions provided"() {
		given:
		def json = new JsonBuilder(
				principalUid: ADMIN_USER,
				permissionsList: [[type: PRODUCT]]
		)

		when:
		def response = putRequestWithBody(json)

		then:
		WebservicesAssert.assertResponse(Response.Status.BAD_REQUEST, response)
		and:
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class)
		errorsList.errors.size() == 1
		with(errorsList.errors[0]) {
			message == "Attribute 'permissions' is a required field for type $PRODUCT."
			type == 'PermissionsRequiredError'
		}
	}

	def putRequestWithBody(json) {
		oAuth2Request(ADMIN_USER)
				.build()
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.put(Entity.json(json.toString()))
	}

	def putRequestWithBody(json, user) {
		oAuth2Request(user)
				.build()
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.put(Entity.json(json.toString()))
	}

	def oAuth2Request(user) {
		new WsSecuredRequestBuilder()
				.extensionName(PermissionswebservicesConstants.EXTENSIONNAME)
				.client(CLIENT_ID, CLIENT_SECRET)
				.scope('permissionswebservices')
				.resourceOwner(user, PASSWORD)
				.grantResourceOwnerPasswordCredentials()
				.path(BASE_PERMISSION_PATH)
	}

	def permissionForTypeWithReadTrue(type) {
		permissionsForType(type, [(READ): true])
	}

	def permissionsForType(String type, Map permissions) {
		[type       : type,
		 permissions: [
				 (READ)              : permissions[READ],
				 (CHANGE)            : permissions[CHANGE],
				 (CREATE)            : permissions[CREATE],
				 (REMOVE)            : permissions[REMOVE],
				 (CHANGE_PERMISSIONS): permissions[CHANGE_PERMISSIONS]
		 ]]
	}

	def permissionAssignment(principal, permissionName, granted) {
		new PermissionAssignment(permissionName, principal, !granted)
	}
}
