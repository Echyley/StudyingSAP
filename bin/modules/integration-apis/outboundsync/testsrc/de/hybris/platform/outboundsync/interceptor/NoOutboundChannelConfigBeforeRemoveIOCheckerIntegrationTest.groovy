package de.hybris.platform.outboundsync.interceptor

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test

@IntegrationTest
class NoOutboundChannelConfigBeforeRemoveIOCheckerIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = "NoOutboundChannelConfigBeforeRemoveIOCheckerIntegrationTest"
    private static final def IO_WITH_OUTBOUND_CHANNEL_CONFIG = "${TEST_NAME}_IO_ICC"
    private static final def OUTBOUND_CHANNEL_CONFIG_NAME = "${TEST_NAME}_OCC"
    private static final def DEST_TARGET_ID = "${TEST_NAME}_DT"
    private static final def BASIC_ID = "${TEST_NAME}_CRED"
    private static final def DESTINATION_ID = "${TEST_NAME}_CD"
    private static final def ENDPOINT = "${TEST_NAME}_EP"
    private static final def VERSION = 'unknown'
    private static final def IO_NO_REFERENCE = "${TEST_NAME}_IO_NO_REF"

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject       ; code[unique = true]',
                "                                      ; $IO_WITH_OUTBOUND_CHANNEL_CONFIG ",
                "                                      ; $IO_NO_REFERENCE ",

                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                "                      ; $ENDPOINT           ; $VERSION               ; local-hybris ; https://localhost:9002",
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                "                               ; $DEST_TARGET_ID",
                'INSERT_UPDATE BasicCredential; id[unique = true]; username; password',
                "                             ; $BASIC_ID        ; will    ; blah",
                'INSERT_UPDATE ConsumedDestination; id[unique = true] ; url              ; endpoint(id, version)         ; credential(id); destinationTarget(id)',
                "                                 ; $DESTINATION_ID   ; https://localUrl ; $ENDPOINT:$VERSION            ; $BASIC_ID     ; $DEST_TARGET_ID",
                "INSERT_UPDATE OutboundChannelConfiguration; code[unique = true]                    ; integrationObject(code)                         ; destination(id)",
                "                                          ; $OUTBOUND_CHANNEL_CONFIG_NAME          ; $IO_WITH_OUTBOUND_CHANNEL_CONFIG                ; $DESTINATION_ID",
        )
    }

    def cleanup() {
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { it.integrationObject.code == IO_WITH_OUTBOUND_CHANNEL_CONFIG }
        IntegrationTestUtil.remove ConsumedDestinationModel, { it.id == DESTINATION_ID }
        IntegrationTestUtil.remove BasicCredentialModel, { it.id == BASIC_ID }
        IntegrationTestUtil.remove DestinationTargetModel, { it.id == DEST_TARGET_ID }
        IntegrationTestUtil.remove EndpointModel, { it.id == ENDPOINT }
        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == IO_WITH_OUTBOUND_CHANNEL_CONFIG }
    }

    @Test
    def 'cannot delete Integration Object when OutboundChannelConfiguration references it'() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_WITH_OUTBOUND_CHANNEL_CONFIG"
        )
        then:
        def e = thrown AssertionError
        e.message.contains "Please delete the related OutboundChannelConfiguration and try again"
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no OutboundChannelConfiguration referencing it"() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_NO_REFERENCE"
        )

        then:
        noExceptionThrown()
    }
}
