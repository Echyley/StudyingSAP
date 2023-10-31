/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.tenant.MockTenant
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class WebhookTenantUtilTest extends JUnitPlatformSpecification {

	private static final def JUNIT_TENANT = "junit"

	@Test
	def "verify the ID of the junit tenant"() {
		given:
		WebhookTenantUtil.setTenant(new MockTenant(JUNIT_TENANT))

		expect:
		WebhookTenantUtil.currentTenantId == JUNIT_TENANT
	}

}
