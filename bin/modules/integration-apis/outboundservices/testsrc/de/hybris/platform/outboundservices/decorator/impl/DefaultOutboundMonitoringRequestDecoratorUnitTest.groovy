/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.model.OutboundRequestModel
import de.hybris.platform.outboundservices.monitoring.OutboundMonitoringException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import spock.lang.Unroll

@UnitTest
class DefaultOutboundMonitoringRequestDecoratorUnitTest extends JUnitPlatformSpecification {
    private static final String MY_INTEGRATION_OBJECT = 'MyIntegrationObject'
    private static final String SAP_PASSPORT_HEADER = 'SAP-PASSPORT'
    private static final String SAP_PASSPORT_VALUE = 'MY-SAP-PASSPORT'
    private static final String INTEGRATION_KEY = 'integrationKey'
    private static final String INTEGRATION_KEY_VALUE = 'MY-INTEGRATION-KEY'
    private static final String DESTINATION_URL = 'THE-DESTINATION-URL'
    private static final String ERROR_MESSAGE = 'my error message'
    private static final String OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME = 'X-OutboundMonitoring-MessageId'
    private static final OutboundSource OUTBOUND_SOURCE = OutboundSource.OUTBOUNDSYNC
    private static final HttpMethod HTTP_METHOD = HttpMethod.POST

    def persistedRequest
    def modelService = Stub(ModelService) {
        create(OutboundRequestModel) >> new OutboundRequestModel()
        save(_) >> { args -> persistedRequest = args[0] }
    }
    def exceptionErrorParser = Stub(MonitoredRequestErrorParser) {
        parseErrorFrom(MonitoredRequestErrorModel, _, _) >> Stub(MonitoredRequestErrorModel) {
            getMessage() >> ERROR_MESSAGE
        }
    }
    def outboundServicesConfiguration = Stub(OutboundServicesConfiguration)
    def monitoringDecorator = new DefaultOutboundMonitoringRequestDecorator(modelService: modelService, exceptionErrorParser: exceptionErrorParser, outboundServicesConfiguration: outboundServicesConfiguration)

    @Test
    def 'monitoring decorator is applicable by default'(){
        expect:
        monitoringDecorator.isApplicable(Stub(DecoratorContext))
    }

    @Test
    @Unroll
    def 'decorator is #isMonitoringEnabled when monitoring is #condition'() {
        when:
        outboundServicesConfiguration.isMonitoringEnabled() >> isMonitoringEnabled

        then:
        monitoringDecorator.isEnabled() == isMonitoringEnabled

        where:
        isMonitoringEnabled | condition
        true                | 'enabled'
        false               | 'disabled'
    }

    @Test
    def 'creates and saves outbound request'() {
        given: 'headers with SAP-PASSPORT header value set'
        def httpHeaders = headersWithSapPassport()
        and: 'a payload with integration key value present'
        def payload = [(INTEGRATION_KEY): INTEGRATION_KEY_VALUE]

        when:
        monitoringDecorator.decorate httpHeaders, payload, decoratorContext(), decoratorExecution()

        then: 'saved outbound request is populated'
        with(persistedRequest) {
            destination == DESTINATION_URL
            sapPassport == SAP_PASSPORT_VALUE
            integrationKey == INTEGRATION_KEY_VALUE
            status == IntegrationRequestStatus.ERROR
            source == OUTBOUND_SOURCE
            httpMethod == HTTP_METHOD
            type == MY_INTEGRATION_OBJECT
            messageId
            !error
        }
    }

    @Test
    @Unroll
    def 'when integration key #isPresent in the context, it is #isExtracted from the payload'() {
        when:
        monitoringDecorator.decorate headersWithSapPassport(), [(INTEGRATION_KEY): INTEGRATION_KEY_VALUE], context, decoratorExecution()

        then:
        with(persistedRequest) {
            integrationKey == expectedIntegrationKey
        }

        where:
        isPresent        | isExtracted     | context                         | expectedIntegrationKey
        'is present'     | 'not extracted' | decoratorContext('a|key|value') | 'a|key|value'
        'is not present' | 'extracted'     | decoratorContext()              | INTEGRATION_KEY_VALUE
    }

    @Test
    def 'injects message ID into every request'() {
        given:
        def headers = headersWithSapPassport()

        when:
        monitoringDecorator.decorate headers, [:], decoratorContext(), decoratorExecution()

        then: 'message ID is injected'
        headers[OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME] == [persistedRequest.messageId]
    }

    @Test
    @Unroll
    def "exception is thrown when request headers #headers do not contain SAP PASSPORT"() {
        given: 'payload with integrationKey value'
        def payload = [(INTEGRATION_KEY): 'some key']

        when:
        monitoringDecorator.decorate headers, payload, decoratorContext(), decoratorExecution()

        then: 'exception is thrown'
        def e = thrown OutboundMonitoringException
        e.message == 'No SAP-PASSPORT header present in request.'
        and: 'saved outbound request is populated despite the exception'
        with(persistedRequest) {
            destination == DESTINATION_URL
            integrationKey == payload[INTEGRATION_KEY]
            error == ERROR_MESSAGE
            status == IntegrationRequestStatus.ERROR
            source == OUTBOUND_SOURCE
            type == MY_INTEGRATION_OBJECT
            !sapPassport
        }

        where:
        headers << [new HttpHeaders(), new HttpHeaders([(SAP_PASSPORT_HEADER): null]), new HttpHeaders([(SAP_PASSPORT_HEADER): ''])]
    }

    @Test
    def 'exception not thrown when integration key is not present in the payload or the context'() {
        given: 'payload does not have integration key'
        def payload = [:]

        when:
        monitoringDecorator.decorate headersWithSapPassport(), payload, decoratorContext(), decoratorExecution()

        then:
        !persistedRequest.integrationKey
    }

    @Test
    def 'when integration key has value in the payload, its value is decoded'() {
        given: 'payload contains encoded integration key value'
        def payload = [(INTEGRATION_KEY): 'Field1|Complex%7CField2']

        when:
        monitoringDecorator.decorate headersWithSapPassport(), payload, decoratorContext(), decoratorExecution()

        then:
        persistedRequest.integrationKey == 'Field1|Complex|Field2'
    }

    @Test
    def 'persists outbound request even when exception is thrown by DecoratorExecution'() {
        given: 'DecoratorExecution throws exception'
        def exception = new RuntimeException()
        def execution = Stub(DecoratorExecution) {
            createHttpEntity(_, _, _) >> { throw exception }
        }

        when:
        monitoringDecorator.decorate headersWithSapPassport(), [:], decoratorContext(), execution

        then: 'the exception is rethrown'
        def e = thrown Exception
        e.is exception
        and: 'inbound request is still persisted'
        with(persistedRequest) {
            status == IntegrationRequestStatus.ERROR
            error == ERROR_MESSAGE
        }
    }

    private DecoratorContext decoratorContext(String integrationKey = null) {
        Stub(DecoratorContext) {
            getDestinationModel() >> Stub(ConsumedDestinationModel) {
                getUrl() >> DESTINATION_URL
            }
            getSource() >> OUTBOUND_SOURCE
            getHttpMethod() >> HTTP_METHOD
            getIntegrationObjectCode() >> MY_INTEGRATION_OBJECT
            getIntegrationKey() >> integrationKey
        }
    }

    private DecoratorExecution decoratorExecution() {
        Stub(DecoratorExecution) {
            createHttpEntity(_, _, _) >> { args -> new HttpEntity<>(args[1] as Map, args[0] as HttpHeaders) }
        }
    }

    private static HttpHeaders headersWithSapPassport() {
        new HttpHeaders([(SAP_PASSPORT_HEADER): SAP_PASSPORT_VALUE])
    }
}
