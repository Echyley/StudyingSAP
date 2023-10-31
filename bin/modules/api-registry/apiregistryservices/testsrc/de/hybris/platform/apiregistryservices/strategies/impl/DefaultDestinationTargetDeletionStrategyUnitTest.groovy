/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.strategies.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao
import de.hybris.platform.apiregistryservices.services.CredentialService
import de.hybris.platform.apiregistryservices.services.DestinationService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultDestinationTargetDeletionStrategyUnitTest extends JUnitPlatformSpecification {

    @Test
    @Unroll
    def "null #description fails precondition"() {
        when:
        new DefaultDestinationTargetDeletionStrategy(modelService, destinationService, eventConfiguration, credentialService)
        then:
        thrown IllegalArgumentException

        where:
        description             | modelService       | destinationService       | eventConfiguration          | credentialService
        "modelService"          | null               | Stub(DestinationService) | Stub(EventConfigurationDao) | Stub(CredentialService)
        "destinationService"    | Stub(ModelService) | null                     | Stub(EventConfigurationDao) | Stub(CredentialService)
        "eventConfigurationDao" | Stub(ModelService) | Stub(DestinationService) | null                        | Stub(CredentialService)
        "credentialService"     | Stub(ModelService) | Stub(DestinationService) | Stub(EventConfigurationDao) | null
    }
}
