package de.hybris.platform.inboundservices.strategies.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.apiregistryservices.services.impl.DefaultDestinationService
import de.hybris.platform.apiregistryservices.strategies.DestinationTargetCloningValidationCheckStrategy
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test
import spock.lang.Issue

import javax.annotation.Resource

@IntegrationTest
@Issue('https://cxjira.sap.com/browse/GRIFFIN-4231')
class ICCDestinationTargetValidationCheckIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = "ICCDestinationTargetValidationCheck"
    private static final def IO = "${TEST_NAME}_IO"
    private static final def URL = "http://localhost:9002/test"
    private static final def USERNAME = "${TEST_NAME}_User"
    private static final def PASSWORD = "pass"
    private static final def EXPOSEDDESTINATION_ID = "${TEST_NAME}_ExposedDestination"
    private static final def TARGET_ID = "${TEST_NAME}_Target"
    private static final def BASIC_CRDENTIAL_ID = "${TEST_NAME}_BasicCredential"
    private static final def ENDPOINT_ID = "${TEST_NAME}_Endpoint"
    @Resource
    private DefaultDestinationService<AbstractDestinationModel> destinationService

    @Resource
    List<DestinationTargetCloningValidationCheckStrategy> destinationTargetCloningCheckStrategyList

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                                      ; $IO ",
                'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
                "                                         ; $IO                        ; BASIC",
                'INSERT_UPDATE BasicCredential;id[unique=true];username;password',
                "								;$BASIC_CRDENTIAL_ID; $USERNAME ; $PASSWORD ",
                'INSERT_UPDATE DestinationTarget;id[unique=true];destinationChannel(code)[default=DEFAULT];template',
                "                               ;$TARGET_ID;;true",
                'INSERT_UPDATE Endpoint;id[unique=true];version;specUrl;specData;name;description',
                "                       ;$ENDPOINT_ID;v1;$URL;e1;n1;des",
                'INSERT_UPDATE ExposedDestination;id[unique=true];url;endpoint(id);additionalProperties;destinationTarget(id);active;credential(id);inboundChannelConfiguration(integrationObject(code))',
                "                                ;$EXPOSEDDESTINATION_ID;$URL;$ENDPOINT_ID;;$TARGET_ID;true;$BASIC_CRDENTIAL_ID;$IO"
        )
    }

    def cleanup() {
        IntegrationTestUtil.remove BasicCredentialModel, { bc -> (bc.id == BASIC_CRDENTIAL_ID) }
        IntegrationTestUtil.remove DestinationTargetModel, { dt -> (dt.id == TARGET_ID) }
        IntegrationTestUtil.remove EndpointModel, { ed -> (ed.id == ENDPOINT_ID) }
        IntegrationTestUtil.remove ExposedDestinationModel, { ed -> (ed.id == EXPOSEDDESTINATION_ID) }
        IntegrationTestUtil.remove InboundChannelConfigurationModel, { icc -> (icc?.integrationObject?.code == IO) }
        IntegrationTestUtil.remove IntegrationObjectModel, { io -> (io.code == IO) }
    }

    @Test
    def "Expect false if destination is Exposed Destination with ICC"() {
        given:
        def destination = destinationService.getDestinationByIdAndByDestinationTargetId(EXPOSEDDESTINATION_ID, TARGET_ID)
        def strategy = destinationTargetCloningCheckStrategyList.find {
            it instanceof ICCDestinationTargetCloningValidationCheckStrategy
        }

        expect:
        !strategy.isValidExposedDestination(destination)
    }
}
