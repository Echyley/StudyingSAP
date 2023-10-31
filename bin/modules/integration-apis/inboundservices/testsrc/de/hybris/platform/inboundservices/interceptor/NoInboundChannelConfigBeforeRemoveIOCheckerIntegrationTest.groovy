package de.hybris.platform.inboundservices.interceptor

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test

@IntegrationTest
class NoInboundChannelConfigBeforeRemoveIOCheckerIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = "NoInboundChannelConfigBeforeRemoveIOCheckerIntegrationTest"
    private static final def IO_WITH_INBOUND_CHANNEL_CONFIG = "${TEST_NAME}_IO_ICC"
    private static final def IO_NO_REFERENCE = "${TEST_NAME}_IO_NO_REF"

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject       ; code[unique = true]',
                "                                      ; $IO_WITH_INBOUND_CHANNEL_CONFIG ",
                "                                      ; $IO_NO_REFERENCE ",
                'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
                "                                         ; $IO_WITH_INBOUND_CHANNEL_CONFIG       ; BASIC",
        )
    }

    def cleanup() {
        IntegrationTestUtil.remove InboundChannelConfigurationModel, { it.integrationObject.code == IO_WITH_INBOUND_CHANNEL_CONFIG }

        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == IO_WITH_INBOUND_CHANNEL_CONFIG }
        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == IO_NO_REFERENCE }
    }

    @Test
    def 'cannot delete Integration Object when InboundChannelConfiguration references it'() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_WITH_INBOUND_CHANNEL_CONFIG"
        )
        then:
        def e = thrown AssertionError
        e.message.contains "Please delete the related InboundChannelConfiguration and try again"
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no InboundChannelConfiguration referencing it"() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_NO_REFERENCE"
        )

        then:
        noExceptionThrown()
    }

}
