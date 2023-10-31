/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.integrationservices.util.LoggerProbe
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test

@UnitTest
class DefaultDestinationSearchServiceUnitTest extends JUnitPlatformSpecification {

    private static final def SEARCH_SERVICE_NOT_FOUND_EXCEPTION_MESSAGE = 'searchService must be provided.'
    private static final def DESTINATION_ID_NULL_EXCEPTION_MESSAGE = 'destinationId cannot be empty or null.'
    private static final def DESTINATION_ID = 'destinationID'
    private static final def DESTINATION = new ConsumedDestinationModel()

    @Rule
    LoggerProbe probe = LoggerProbe.create(DefaultDestinationSearchService)

    @Rule
    TestApplicationContext context = new TestApplicationContext()

    def flexibleSearchService = Stub(FlexibleSearchService)
    def service = new DefaultDestinationSearchService(flexibleSearchService)

    @Test
    def "throws NullPointerException when FlexibleSearchService is null"() {
        when:
        new DefaultDestinationSearchService(null)

        then:
        def e = thrown(NullPointerException)
        e.message == SEARCH_SERVICE_NOT_FOUND_EXCEPTION_MESSAGE
    }

    @Test
    def "throws IllegalArgumentException when destinationId is #condition"() {
        when:
        service.findDestination(destId)

        then:
        def e = thrown IllegalArgumentException
        e.message == DESTINATION_ID_NULL_EXCEPTION_MESSAGE

        where:
        destId | condition
        null   | 'null'
        ''     | 'empty'
    }

    @Test
    def "returns a ConsumedDestinationNotFoundModel when flexibleSearchService cannot find the model"() {
        given:
        flexibleSearchService.getModelByExample({ it.id == DESTINATION_ID }) >> { throw new ModelNotFoundException('') }
        String warning = "Failed to find ConsumedDestination with id \'$DESTINATION_ID\'"

        when:
        def model = service.findDestination(DESTINATION_ID)

        then:
        probe.getMessagesLoggedAtWarn().contains(warning)

        then:
        model.id == DESTINATION_ID
    }

    @Test
    def "returns the destination if found"() {
        given:
        flexibleSearchService.getModelByExample({ it.id == DESTINATION_ID }) >> DESTINATION

        expect:
        service.findDestination(DESTINATION_ID) is DESTINATION
    }
}
