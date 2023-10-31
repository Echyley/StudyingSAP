package de.hybris.platform.outboundsync.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class BatchResponseNotFoundExceptionUnitTest extends JUnitPlatformSpecification {
    private static final String CHANGE_ID = "123456"

    def dtoGroup = Stub(OutboundItemDTOGroup) {
        getChangeId() >> CHANGE_ID
    }
    def exception = new BatchResponseNotFoundException(dtoGroup);

    @Test
    def 'message is initialized'() {
        expect:
        exception.message.contains("The OutboundItemDTOGroup with Change ID [$CHANGE_ID] does not have a " +
                "corresponding response part in the batch response")
    }
}