/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.job.SyncFilter
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification

@UnitTest
class FilteringServiceUnitTest extends JUnitPlatformSpecification {
    def integrationObject = Stub(IntegrationObjectModel)
    def changeConsumer = Mock(OutboundItemConsumer)
    def filteringService = new DefaultFilteringService(changeConsumer)

    @Test
    def "FilteringService cannot be created with a null OutboundItemConsumer"() {
        when:
        new DefaultFilteringService(null)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def "FilteringService does not filter out the OutboundItemDTO when syncFilterList is empty"() {
        given:
        def outboundItemDTO = Stub(OutboundItemDTO)

        expect:
        filteringService.applyFilters(outboundItemDTO, integrationObject) == Optional.of(outboundItemDTO)
    }

    @Test
    def "FilteringService does not filter out the OutboundItemDTO when an applicable SyncFilter does not filter out the OutboundItemDTO"() {
        given:
        def outboundItemDTO = Stub(OutboundItemDTO)
        def optionalOfDTO = Optional.of(outboundItemDTO)
        def syncFilter = Stub(SyncFilter) {
            isApplicable(outboundItemDTO) >> true
            evaluate(outboundItemDTO, integrationObject) >> optionalOfDTO
        }
        filteringService.setSyncFilterList([syncFilter])

        when:
        def actualValue = filteringService.applyFilters(outboundItemDTO, integrationObject)

        then:
        actualValue == optionalOfDTO
        0 * changeConsumer.consume(outboundItemDTO)
    }

    @Test
    def "FilteringService returns optional empty when an applicable syncFilter filters out the OutboundItemDTO"() {
        given:
        def outboundItemDTO = Stub(OutboundItemDTO)
        def syncFilter = Stub(SyncFilter) {
            isApplicable(outboundItemDTO) >> true
            evaluate(outboundItemDTO, integrationObject) >> Optional.empty()
        }
        filteringService.setSyncFilterList([syncFilter])

        when:
        def actualValue = filteringService.applyFilters(outboundItemDTO, integrationObject)

        then:
        actualValue == Optional.empty()
        1 * changeConsumer.consume(outboundItemDTO)
    }
}
