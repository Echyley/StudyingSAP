/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.integrationservices.IntegrationObjectBuilder
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory
import de.hybris.platform.outboundservices.decorator.RequestDecoratorService
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class DefaultDeleteRequestSenderUnitTest extends JUnitPlatformSpecification {
    private static final def DESTINATION_URL = 'http://external.sys/entities'
    private static final def IO = 'ProductIO'
    private static final def DELETE_URL = "$DESTINATION_URL('{key}')"
    private static final def INTEGRATION_KEY_VALUE = 'integration-key-value'

    private static final Map<String, String> DELETE_URL_PARAMS = [key: INTEGRATION_KEY_VALUE]
    private static final HttpEntity HTTP_ENTITY = new HttpEntity(null, null)

    def parameters = SyncParameters.syncParametersBuilder()
            .withIntegrationKey(INTEGRATION_KEY_VALUE)
            .withDestination(new ConsumedDestinationModel(url: DESTINATION_URL))
            .withIntegrationObject(IntegrationObjectBuilder.integrationObject().withCode(IO).build())
            .build()

    @Shared
    def decoratorService = Stub(RequestDecoratorService)
    @Shared
    def restTemplateFactory = Stub(IntegrationRestTemplateFactory)


    @Test
    @Unroll
    def 'cannot be instantiated with null #condition'() {
        when:
        new DefaultDeleteRequestSender(factory, decService)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$condition cannot be null"

        where:
        condition                        | factory             | decService
        'IntegrationRestTemplateFactory' | null                | decoratorService
        'RequestDecoratorService'        | restTemplateFactory | null
    }

    @Test
    def 'send delegates to the REST template'() {
        given:
        def decoratorService = Stub(RequestDecoratorService) {
            createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY
        }
        def restTemplate = Mock(RestTemplate)
        def factory = Stub(IntegrationRestTemplateFactory) {
            create(parameters.destination) >> restTemplate
        }

        def requestSender = new DefaultDeleteRequestSender(factory, decoratorService)

        when:
        requestSender.send parameters

        then: 'the rest template was called with the item integration key'
        1 * restTemplate.exchange(DELETE_URL, HttpMethod.DELETE, HTTP_ENTITY, Object, DELETE_URL_PARAMS)
    }
}
