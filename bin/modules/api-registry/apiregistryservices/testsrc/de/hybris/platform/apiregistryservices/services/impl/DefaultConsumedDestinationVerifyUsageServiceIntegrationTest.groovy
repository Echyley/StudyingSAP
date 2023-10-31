/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class DefaultConsumedDestinationVerifyUsageServiceIntegrationTest extends ServicelayerSpockSpecification{

    private static final def CONSUMEDDESTINATION_ID = "consumed-destination"

    @Resource
    DefaultConsumedDestinationVerifyUsageService consumedDestinationVerifyUsageService

    @Resource
    private FlexibleSearchService flexibleSearchService

    def setup() {
        importCsv("/test/consumedDestinationIntegrationTest.impex", "UTF-8")
    }

    @Test
    def "throw an exception when the type code is not found or the attribute name is not valid"() {
        given:
        def sample = new ConsumedDestinationModel()
        sample.setId(CONSUMEDDESTINATION_ID)

        when:
        consumedDestinationVerifyUsageService.findModelsAssignedConsumedDestination(typeCode, attributeName,
                flexibleSearchService.getModelByExample(sample))
        then:
        def e = thrown IllegalArgumentException
        e.message.contains String.format("The item model with type code: [%s] has not been found or the attribute: [%s] is not valid consumed destination attribute", typeCode, attributeName)

        where:
        typeCode                    | attributeName
        ""                          | ""
        "WebhookConfiguration"      | "eventType"
        "NotFoundWebConfiguration"  | ""
    }
}
