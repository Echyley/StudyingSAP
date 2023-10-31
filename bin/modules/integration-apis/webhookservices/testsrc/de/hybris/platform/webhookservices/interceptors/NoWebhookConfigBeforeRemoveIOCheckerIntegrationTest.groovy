package de.hybris.platform.webhookservices.interceptors

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import org.junit.Test

@IntegrationTest
class NoWebhookConfigBeforeRemoveIOCheckerIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = "NoWebhookConfigBeforeRemoveIOCheckerIntegrationTest"
    private static final def IO_WITH_WEBHOOK_CONFIG = "${TEST_NAME}_IO_WH"
    private static final def DEST_TARGET_ID = "${TEST_NAME}_DT"
    private static final def BASIC_ID = "${TEST_NAME}_CRED"
    private static final def DEST_TARGET_ID2 = "${TEST_NAME}_WH_DT"
    private static final def ENDPOINT = "${TEST_NAME}_EP"
    private static final def EVENT_TYPE = "de.hybris.platform.webhookservices.event.ItemSavedEvent";
    private static final def VERSION = 'unknown'
    private static final def IO_NO_REFERENCE = "${TEST_NAME}_IO_NO_REF"

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject       ; code[unique = true]',
                "                                      ; $IO_WITH_WEBHOOK_CONFIG ",
                "                                      ; $IO_NO_REFERENCE ",

                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                "                      ; $ENDPOINT           ; $VERSION               ; local-hybris ; https://localhost:9002",
                "INSERT_UPDATE DestinationTarget; id[unique = true]; destinationChannel(code); registrationStatus(code)",
                "                               ; $DEST_TARGET_ID  ; WEBHOOKSERVICES         ; REGISTERED",
                "INSERT_UPDATE EventConfiguration; eventClass[unique = true]                               ; exportName                     ; exportFlag; extensionName   ; destinationTarget(id)[unique = true]; version[unique = true]; priority(code)",
                "                                ; $EVENT_TYPE                                             ; webhookservices.ItemSavedEvent ; true      ; webhookservices ; $DEST_TARGET_ID                     ; 1                     ; CRITICAL",
                'INSERT_UPDATE BasicCredential; id[unique = true]; username; password',
                "                             ; $BASIC_ID        ; will    ; blah",
                'INSERT_UPDATE ConsumedDestination; id[unique = true] ; url              ; endpoint(id, version)         ; credential(id); destinationTarget(id)',
                "                                 ; $DEST_TARGET_ID2  ; https://localUrl ; $ENDPOINT:$VERSION            ; $BASIC_ID     ; $DEST_TARGET_ID",
                "INSERT_UPDATE WebhookConfiguration; integrationObject(code)[unique = true]                 ; destination(id)[unique = true]",
                "                                  ; $IO_WITH_WEBHOOK_CONFIG                                ; $DEST_TARGET_ID2"
        )
    }

    def cleanup() {
        IntegrationTestUtil.remove WebhookConfigurationModel, { it.integrationObject.code == IO_WITH_WEBHOOK_CONFIG }
        IntegrationTestUtil.remove ConsumedDestinationModel, { it.id == DEST_TARGET_ID2 }
        IntegrationTestUtil.remove BasicCredentialModel, { it.id == BASIC_ID }
        IntegrationTestUtil.remove EventConfigurationModel, {
            it.eventClass == EVENT_TYPE && it.destinationTarget.id == DEST_TARGET_ID && it.version == 1
        }
        IntegrationTestUtil.remove DestinationTargetModel, { it.id == DEST_TARGET_ID }
        IntegrationTestUtil.remove EndpointModel, { it.id == ENDPOINT }
        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == IO_WITH_WEBHOOK_CONFIG }
    }

    @Test
    def 'cannot delete Integration Object when WebhookConfiguration references it'() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_WITH_WEBHOOK_CONFIG"
        )
        then:
        def e = thrown AssertionError
        e.message.contains "Please delete the related WebhookConfiguration and try again"
    }

    @Test
    def "no exception is thrown when deleting an Integration Object that has no WebhookConfiguration referencing it"() {
        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE IntegrationObject; code[unique=true]',
                "                        ; $IO_NO_REFERENCE"
        )

        then:
        noExceptionThrown()
    }
}
