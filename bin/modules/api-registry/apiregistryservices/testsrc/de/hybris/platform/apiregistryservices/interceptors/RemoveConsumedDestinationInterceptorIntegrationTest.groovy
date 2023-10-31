/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.interceptors

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class RemoveConsumedDestinationInterceptorIntegrationTest extends ServicelayerSpockSpecification {

    private static final def CONSUMEDDESTINATION_ID = "consumed-destination"

    @Resource
    FlexibleSearchService flexibleSearchService

    @Resource
    ModelService modelService

    def setup() {
        importCsv("/test/consumedDestinationIntegrationTest.impex", "UTF-8")
    }

    @Test
    def "successfully delete a consumed destination when it is not assigned to any item model"() {
        given:
        def sample = new ConsumedDestinationModel()
        sample.setId(CONSUMEDDESTINATION_ID)

        when:
        modelService.remove(flexibleSearchService.getModelByExample(sample))

        then:
        noExceptionThrown()
    }
}
