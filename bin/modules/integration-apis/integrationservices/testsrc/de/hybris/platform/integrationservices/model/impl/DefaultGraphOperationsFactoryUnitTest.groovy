/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectGraphOperations
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultGraphOperationsFactoryUnitTest extends JUnitPlatformSpecification {

    @Test
    def "create method returns instance of IntegrationObjectGraphOperations when IntegrationObjectDescriptor is given"() {
        given:
        def factory = new DefaultGraphOperationsFactory()

        when:
        def operations = factory.create(Stub(IntegrationObjectDescriptor))

        then:
        operations instanceof IntegrationObjectGraphOperations
    }
}
