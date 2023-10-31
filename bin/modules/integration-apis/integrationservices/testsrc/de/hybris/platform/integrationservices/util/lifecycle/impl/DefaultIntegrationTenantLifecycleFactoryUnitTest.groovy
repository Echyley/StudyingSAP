/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.lifecycle.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.Registry
import de.hybris.platform.core.Tenant
import de.hybris.platform.core.TenantListener
import de.hybris.platform.integrationservices.util.lifecycle.TenantLifecycle
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultIntegrationTenantLifecycleFactoryUnitTest extends JUnitPlatformSpecification {
    def tenantLifecycleFactory = new DefaultIntegrationTenantLifecycleFactory()

    @Test
    def 'factory create method fails if tenant is not provided'() {
        when:
        tenantLifecycleFactory.create(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Tenant must be provided.'
    }

    @Test
    def "factory creates a tenantLifecycle and registers it to Registry"() {
        when:
        def tenantLifecycle = tenantLifecycleFactory.create(Stub(Tenant))

        then:
        Registry.tenantListeners.contains(tenantLifecycle)

        cleanup:
        Registry.unregisterTenantListener((TenantListener)tenantLifecycle)
    }

    @Test
    def "factory creates a tenantLifecycle only once with the same tenant"() {
        given:
        def tenant = Stub(Tenant)

        when:
        def firstLifecycle = tenantLifecycleFactory.create(tenant)
        def secondLifecycle = tenantLifecycleFactory.create(tenant)

        then:
        firstLifecycle == secondLifecycle
        Registry.tenantListeners.contains(firstLifecycle)

        cleanup:
        Registry.unregisterTenantListener((TenantListener)firstLifecycle)
    }
}
