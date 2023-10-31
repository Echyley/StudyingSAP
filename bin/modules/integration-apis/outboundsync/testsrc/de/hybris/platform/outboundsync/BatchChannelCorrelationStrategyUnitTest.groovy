package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class BatchChannelCorrelationStrategyUnitTest extends JUnitPlatformSpecification {

    def correlationStrategy = new BatchChannelCorrelationStrategy()

    @Test
    def "creates a correlation key using the cron job PK when #condition"() {
        given:
        def dtoGroup = Stub(OutboundItemDTOGroup) {
            getCronJobPk() >> cronJobPK
        }

        expect:
        correlationStrategy.correlationKey(dtoGroup) == expectedKey

        where:
        condition         | cronJobPK        | expectedKey
        "PK is null"      | null             | ""
        'PKs is provided' | PK.fromLong(123) | "123"
    }

    @Test
    def "Exception is thrown when dto group is null"() {
        when:
        correlationStrategy.correlationKey(null)

        then:
        thrown IllegalArgumentException
    }
}
