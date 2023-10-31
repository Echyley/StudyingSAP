/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionswebservices.controllers

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.permissionsfacades.PermissionsFacade
import de.hybris.platform.permissionsfacades.data.TypePermissionsDataList
import de.hybris.platform.permissionswebservices.dto.TypePermissionsListWsDTO
import de.hybris.platform.permissionswebservices.dto.TypePermissionsWsDTO
import de.hybris.platform.webservicescommons.mapping.DataMapper
import org.junit.Test
import spock.lang.Specification

@UnitTest
class PermissionsControllerTest extends Specification {

	def dataMapper = Mock(DataMapper)
	def permissionsFacade = Mock(PermissionsFacade)
	def controller = new PermissionsController(dataMapper: dataMapper, permissionsFacade: permissionsFacade)

	@Test
	def 'replaceTypePermissions processes the permissions for the principal'() {
		given:
		def permissionsListDto = new TypePermissionsListWsDTO(
				principalUid: 'uid',
				permissionsList: [new TypePermissionsWsDTO()]
		)
		def responsePermissionsListDto = new TypePermissionsListWsDTO()
		def permissionsList = new TypePermissionsDataList()
		def returnPermissionsList = new TypePermissionsDataList()

		when:
		def response = controller.replaceTypePermissions permissionsListDto

		then: 'DTO is converted to the PermissionService data type'
		1 * dataMapper.map(permissionsListDto, TypePermissionsDataList) >> permissionsList
		and: 'PermissionService is invoked, and return the updated permissions list'
		1 * permissionsFacade.applyPermissions(permissionsList) >> returnPermissionsList
		and: 'Updated permissions list is converted to DTO'
		1 * dataMapper.map(returnPermissionsList, TypePermissionsListWsDTO) >> responsePermissionsListDto
		and: 'controller returns the updated permissions list DTO'
		response == responsePermissionsListDto
	}
}
