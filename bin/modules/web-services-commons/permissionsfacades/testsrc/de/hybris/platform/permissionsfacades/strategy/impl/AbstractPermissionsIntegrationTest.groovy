/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.permissionsfacades.strategy.impl

import de.hybris.platform.core.model.security.PrincipalModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService
import de.hybris.platform.servicelayer.type.TypeService

import javax.annotation.Resource

abstract class AbstractPermissionsIntegrationTest extends ServicelayerSpockSpecification {

	@Resource
	private FlexibleSearchService flexibleSearchService
	@Resource
	TypeService typeService
	@Resource
	PermissionManagementService permissionManagementService

	PrincipalModel findPrincipal(final String principalUid) {
		final PrincipalModel example = new PrincipalModel()
		example.setUid(principalUid)
		return flexibleSearchService.getModelByExample(example)
	}

	ComposedTypeModel composedType(final String code) {
		typeService.getComposedTypeForCode(code)
	}

}
