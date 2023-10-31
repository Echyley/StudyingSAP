/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Unroll

@UnitTest
@Issue('https://cxjira.sap.com/browse/IAPI-4703')
class DefaultConsumedDestinationVerifyUsageUnitTest extends JUnitPlatformSpecification {

    @Test
    @Unroll
    def "null #description fails precondition check"()
    {
        when:
        new DefaultConsumedDestinationVerifyUsageService(flexibleSearch, typeSerivce)
        then:
        thrown(IllegalArgumentException)

        where:
        description             | flexibleSearch              | typeSerivce
        "flexibleSearchService" | null                        | Stub(TypeService)
        "typeService"           | Stub(FlexibleSearchService) | null
    }

    @Test
    @Unroll
    def "null #description fails precondition check when looking up items"()
    {
        given:
        def service = new DefaultConsumedDestinationVerifyUsageService(Mock(FlexibleSearchService), Mock(TypeService))

        when:
        service.findModelsAssignedConsumedDestination(typeCode,attributeName,consumedDestinationModel)
        then:
        thrown(IllegalArgumentException)

        where:
        description             | typeCode | attributeName | consumedDestinationModel
        "typeCode"              | null     | ""            | Stub(ConsumedDestinationModel)
        "attributeName"         | ""       | null          | Stub(ConsumedDestinationModel)
        "consumedDestination"   | ""       | ""            | null
    }
}
