/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.decorator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.EventType
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.service.impl.DefaultCloudEventHeadersService
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import spock.lang.Issue
import spock.lang.Shared

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME

@UnitTest
@Issue("https://cxjira.sap.com/browse/IAPI-5118")
class DefaultCloudEventHeadersRequestDecoratorUnitTest extends JUnitPlatformSpecification {
    private static final def INTEGRATION_OBJECT_CODE = "IntegrationObjectCode"
    private static final def CLOUD_EVENT_SAP_PASSPORT = "abcd-1234-efgh-5678"
    private static final def CLOUD_EVENT_ID = "ce-id"
    private static final def CLOUD_EVENT_ID_VALUE = "1234-1234-efgh-5678"
    private static final def MAP_KEY = "key"
    private static final def MAP_VALUE = "value"
    private static final def INTEGRATION_KEY_NAME = "integrationKey"
    private static final def INTEGRATION_KEY_VALUE = "${MAP_KEY}|${MAP_VALUE}"
    private static final def PAYLOAD = [(MAP_KEY): MAP_VALUE, (INTEGRATION_KEY_NAME): INTEGRATION_KEY_VALUE]
    private static final def EXPECT_CLOUD_EVENT_HEADER_SIZE = 2

    @Shared
    def CLOUD_EVENT_OPERATION = Stub EventType

    def httpHeaders = headersWithSapPassport()
    def cloudEventHeaders = headersWithCloudEventHeader()

    def decoratorExecution = Stub(DecoratorExecution) {
        createHttpEntity(_, _, _) >> { args -> new HttpEntity<>(args[1] as Map, args[0] as HttpHeaders) }
    }

    def decoratorContext = Stub(DecoratorContext) {
        getIntegrationObjectCode() >> INTEGRATION_OBJECT_CODE
        getEventType() >> CLOUD_EVENT_OPERATION
    }

    def cloudEventHeadersService = Stub(DefaultCloudEventHeadersService) {
        generateCloudEventHeaders(INTEGRATION_OBJECT_CODE, INTEGRATION_KEY_VALUE, CLOUD_EVENT_OPERATION, CLOUD_EVENT_SAP_PASSPORT) >> cloudEventHeaders
    }

    def decorator = new DefaultCloudEventHeadersRequestDecorator(cloudEventHeadersService)

    def setup() {
        httpHeaders = headersWithSapPassport()
        cloudEventHeaders = headersWithCloudEventHeader()
    }

    @Test
    def 'decorator isApplicable returns #applicable when OutboundSource is #source'() {
        given:
        decoratorContext.getSource() >> source

        expect:
        applicable == decorator.isApplicable(decoratorContext)

        where:
        source                         | applicable
        OutboundSource.WEBHOOKSERVICES | true
        OutboundSource.OUTBOUNDSYNC    | false
    }

    @Test
    def 'CloudEvent headers created when outbound source is webhook service'() {
        when:
        def result = decorator.decorate(httpHeaders, PAYLOAD, decoratorContext, decoratorExecution)

        then:
        result.getHeaders().size() == EXPECT_CLOUD_EVENT_HEADER_SIZE
        result.getHeaders().getFirst(CLOUD_EVENT_ID) == CLOUD_EVENT_ID_VALUE
        result.getHeaders().getFirst(SAP_PASSPORT_HEADER_NAME) == CLOUD_EVENT_SAP_PASSPORT
    }

    @Test
    def "null CloudEventDataService fails precondition check"() {
        when:
        new DefaultCloudEventHeadersRequestDecorator(null)

        then:
        thrown(IllegalArgumentException)
    }

    private static HttpHeaders headersWithSapPassport() {
        new HttpHeaders([(SAP_PASSPORT_HEADER_NAME): CLOUD_EVENT_SAP_PASSPORT])
    }

    private static HttpHeaders headersWithCloudEventHeader() {
        new HttpHeaders([(CLOUD_EVENT_ID): CLOUD_EVENT_ID_VALUE])
    }
}
