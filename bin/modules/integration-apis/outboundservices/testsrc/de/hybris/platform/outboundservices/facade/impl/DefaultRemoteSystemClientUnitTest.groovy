/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

@UnitTest
class DefaultRemoteSystemClientUnitTest extends JUnitPlatformSpecification {
    private static final def DESTINATION_URL = 'http://does.not.matter'
    private static final def SOME_ENTITY = new HttpEntity('request body - irrelevant')
    private static final String EXTRA_PATH = "/extraPath"

    def restTemplateFactory = Stub IntegrationRestTemplateFactory
    def client = new DefaultRemoteSystemClient(restTemplateFactory)

    @Test
    def 'rest template factory is required for DefaultRemoteSystemClient'() {
        when:
        new DefaultRemoteSystemClient(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'IntegrationRestTemplateFactory cannot be null'
    }

    @Test
    def 'uses RestOperations created by the factory to perform POST with response type #responseType'() {
        given: 'RestOperations respond with some result to the POST call'
        def response = new ResponseEntity(HttpStatus.CREATED)
        def restOps = Stub(RestOperations) {
            postForEntity(DESTINATION_URL, SOME_ENTITY, responseType) >> response
        }
        and: 'the REST template factory creates the RestOperations for a destination'
        def destination = destination(DESTINATION_URL)
        restTemplateFactory.create(destination) >> restOps

        expect: 'response that was received from the RestOperations'
        client.post(destination, SOME_ENTITY, responseType) == response

        where:
        responseType << [Map.class, String.class]
    }

    @Test
    def 'posting with additional path appends it to the url in the request'() {
        given:
        def restOps = Mock(RestOperations)
        def destination = destination(DESTINATION_URL)
        restTemplateFactory.create(destination) >> restOps

        when:
        client.post(destination, SOME_ENTITY, String.class, EXTRA_PATH)

        then:
        1 * restOps.postForEntity(DESTINATION_URL + EXTRA_PATH, SOME_ENTITY, String.class)
    }

    private ConsumedDestinationModel destination(String url) {
        Stub(ConsumedDestinationModel) {
            getUrl() >> url
        }
    }
}
