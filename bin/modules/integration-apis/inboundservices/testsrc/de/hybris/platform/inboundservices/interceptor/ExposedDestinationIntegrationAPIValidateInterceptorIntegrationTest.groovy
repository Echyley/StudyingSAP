package de.hybris.platform.inboundservices.interceptor

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.EndpointModel
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test

@IntegrationTest
class ExposedDestinationIntegrationAPIValidateInterceptorIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = "ExposedDestinationIntegrationAPIValidateInterceptor"
    private static final String INBOUND_PRODUCT_IO = "$TEST_NAME}_IO"
    private static final String INBOUND_STOCK_LEVEL_IO = "${TEST_NAME}_InboundStockLevelIO"
    private static final String ENDPOINT_URL = "http://localhost:9002/endpoint"
    private static final String EXPOSED_DEST_URL = "http://does.not.matter"
    private static final String USERNAME = "${TEST_NAME}_User"
    private static final String PASSWORD = "pass"
    private static final String TARGET1 = "${TEST_NAME}_Target1"
    private static final String TARGET2 = "${TEST_NAME}_Target2"
    private static final String CREDENTIALS = "${TEST_NAME}_BasicCredentials"
    private static final String ENDPOINT = "${TEST_NAME}_Endpoint"
    private static final String DESTINATION = "${TEST_NAME}_Destination"
    private static final String DESTINATION2 = "${TEST_NAME}_Destination2"

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject ;code[unique = true]',
                "                                       ;$INBOUND_PRODUCT_IO ",
                "                                       ;$INBOUND_STOCK_LEVEL_IO ",
                'INSERT_UPDATE InboundChannelConfiguration ;integrationObject(code)[unique = true] ;authenticationType(code)',
                "                                          ;$INBOUND_PRODUCT_IO                    ;BASIC",
                "                                          ;$INBOUND_STOCK_LEVEL_IO                ;BASIC",
                'INSERT_UPDATE BasicCredential ;id[unique=true] ;username   ;password',
                "                              ;$CREDENTIALS       ;$USERNAME  ;$PASSWORD ",
                'INSERT_UPDATE DestinationTarget; id[unique=true]   ;destinationChannel(code); template',
                "                               ; $TARGET1          ;DEFAULT                 ; true",
                "                               ; $TARGET2          ;DEFAULT                 ; true",
                'INSERT_UPDATE Endpoint ;id[unique=true] ;version ;specUrl          ;specData ;name ;description',
                "                       ;$ENDPOINT              ;v1      ;$ENDPOINT_URL    ;s1       ;n1   ;des",
                'INSERT_UPDATE ExposedDestination ;id[unique=true] ;url                ;endpoint(id) ;destinationTarget(id) ;active ;credential(id) ;inboundChannelConfiguration(integrationObject(code))',
                "                                 ;$DESTINATION            ;$EXPOSED_DEST_URL  ;$ENDPOINT           ;$TARGET1              ;true   ;$CREDENTIALS      ;$INBOUND_PRODUCT_IO"
        )
    }

    def cleanup() {
        IntegrationTestUtil.removeAll InboundChannelConfigurationModel
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll BasicCredentialModel
        IntegrationTestUtil.removeAll DestinationTargetModel
        IntegrationTestUtil.removeAll EndpointModel
        IntegrationTestUtil.removeAll ExposedDestinationModel
    }

    @Test
    def "Impex import case 1: throw exception"() {
        when: "create an ExposedDestination with same InboundChannelConfiguration exposed in the same TargetDestination"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE ExposedDestination ;id[unique=true]        ;url                  ;endpoint(id)        ;destinationTarget(id) ;active ;credential(id)    ;inboundChannelConfiguration(integrationObject(code))',
                "                                        ;$DESTINATION2          ;$EXPOSED_DEST_URL    ;$ENDPOINT           ;$TARGET1              ;true   ;$CREDENTIALS      ;$INBOUND_PRODUCT_IO"
        )

        then:
        def e = thrown(AssertionError)
        e.message.contains("has already been exposed in this DestinationTarget")
    }

    @Test
    def "Impex import case 2: no exception will be thrown"() {
        when: "create an ExposedDestination with same InboundChannelConfiguration but different TargetDestination"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE ExposedDestination ;id[unique=true]              ;url                  ;endpoint(id)          ;destinationTarget(id)   ;active          ;credential(id) ;inboundChannelConfiguration(integrationObject(code))',
                "                                        ;$DESTINATION2                ;$EXPOSED_DEST_URL    ;$ENDPOINT             ;$TARGET2                ;true            ;$CREDENTIALS   ;$INBOUND_PRODUCT_IO"
        )

        then:
        noExceptionThrown()
    }

    @Test
    def "Impex import case 3: no exception will be thrown"() {
        when: "create an ExposedDestination with different InboundChannelConfiguration but same TargetDestination"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE ExposedDestination ;id[unique=true]     ;url                                 ;endpoint(id)           ;destinationTarget(id)    ;active                 ;credential(id)    ;inboundChannelConfiguration(integrationObject(code))',
                "                                        ;$DESTINATION2       ;$EXPOSED_DEST_URL                   ;$ENDPOINT              ;$TARGET1                 ;true                   ;$CREDENTIALS      ;$INBOUND_STOCK_LEVEL_IO"
        )

        then:
        noExceptionThrown()
    }
}
