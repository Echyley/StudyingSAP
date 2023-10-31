/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel
import de.hybris.platform.inboundservices.model.InboundRequestModel
import de.hybris.platform.inboundservices.util.InboundServicesEssentialData
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.ClassRule
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext

@IntegrationTest
class InboundIntegrationMonitoringIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'InboundIntegrationMonitoring'
    private static final String REQUEST1 = "${TEST_NAME}_InboundRequest1"
    private static final String REQUEST2 = "${TEST_NAME}_InboundRequest2"
    private static final String MISSING_REQUIRED_FIELD = "${TEST_NAME}_missing_required_field"

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = InboundServicesEssentialData.inboundServicesEssentialData()

    @Resource(name = "oDataWebMonitoringFacade")
    private ODataFacade facade

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT InboundRequest; &reqID    ; status(code); type    ; integrationKey; user(uid)',
                "                     ; $REQUEST1 ; ERROR       ; Category; test-error    ; admin",
                "                     ; $REQUEST2 ; SUCCESS     ; Catalog ; test-success  ; admin",
                'INSERT InboundRequestError; code                    ; message              ; inboundRequest(&reqID); owner(&reqID)',
                "                          ; $MISSING_REQUIRED_FIELD ; Some field is missing; $REQUEST1 ; $REQUEST2 "
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll InboundRequestModel
    }

    @Test
    def "can GET /InboundIntegrationMonitoring/#feed"() {
        when:
        def response = facade.handleRequest request(feed)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = asJson response
        def content = json.getCollectionOfObjects path
        content.size() == result.size()
        content.containsAll result

        where:
        feed                         | path                | result
        ''                           | 'd.EntitySets'      | ['InboundRequestErrors', 'IntegrationRequestStatuses', 'InboundRequests', 'InboundUsers', 'Localized___InboundUsers', 'HttpMethods']
        'IntegrationRequestStatuses' | 'd.results[*].code' | ['SUCCESS', 'ERROR']
        'InboundRequests'            | 'd.results[*].type' | ['Category', 'Catalog']
        'InboundRequestErrors'       | 'd.results[*].code' | [MISSING_REQUIRED_FIELD]
        'HttpMethods'                | 'd.results[*].code' | ['POST', 'DELETE', 'PATCH']
    }

    @Test
    def "can GET /InboundIntegrationMonitoring/IntegrationRequestStatuses('key')"() {
        when:
        def response = facade.handleRequest request('IntegrationRequestStatuses', 'SUCCESS')

        then:
        response.getStatus() == HttpStatusCodes.OK
        asJson(response).getString('d.code') == 'SUCCESS'
    }

    @Test
    def "can GET /InboundIntegrationMonitoring/InboundRequests('key')"() {
        setup:
        String key = IntegrationTestUtil.findAny(InboundRequestModel.class, { 'Catalog' == it.getProperty('type') })
                .map({ integrationKeyFor(it) })
                .orElse('')

        when:
        def response = facade.handleRequest request('InboundRequests', key)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def entity = asJson(response)
        entity.getString('d.type') == 'Catalog'
        entity.exists('d.status.__deferred')
        entity.getString('d.integrationKey').contains 'test-success'
        entity.exists('d.user.__deferred')
    }

    @Test
    def "can get user name and uid from InboundRequest"() {
        setup:
        String key = IntegrationTestUtil.findAny(InboundRequestModel.class, { 'Catalog' == it.getProperty('type') })
                .map({ integrationKeyFor(it) })
                .orElse('')

        when:
        def response = facade.handleRequest request('InboundRequests', key, ['$expand': 'user'])

        then:
        response.getStatus() == HttpStatusCodes.OK
        def entity = asJson(response)
        entity.getString('d.user.uid') == ('admin')
        entity.getString('d.user.name') == ('Administrator')
    }

    @Test
    def "can GET /InboundIntegrationMonitoring/InboundRequestErrors('key')"() {
        setup:
        String key = IntegrationTestUtil.findAny(InboundRequestErrorModel.class, { it -> true })
                .map({ integrationKeyFor(it) })
                .orElse('')

        when:
        def response = facade.handleRequest request('InboundRequestErrors', key)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def entity = asJson(response)
        entity.getString('d.code') == MISSING_REQUIRED_FIELD
        entity.getString('d.message') == 'Some field is missing'
    }

    private static ODataContext request(String entitySet) {
        createContext ODataRequestBuilder.oDataGetRequest()
                .withAccepts('application/json')
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(TEST_NAME)
                        .withEntitySet(entitySet))
    }

    private static ODataContext request(String entitySet, String key) {
        createContext ODataRequestBuilder.oDataGetRequest()
                .withAccepts('application/json')
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(TEST_NAME)
                        .withEntitySet(entitySet)
                        .withEntityKeys(key))
    }

    private static ODataContext request(String entitySet, String key, Map params) {
        createContext ODataRequestBuilder.oDataGetRequest()
                .withAccepts('application/json')
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(TEST_NAME)
                        .withEntitySet(entitySet)
                        .withEntityKeys(key)
                        .withNavigationSegment())
                .withParameters(params)
    }

    private static JsonObject asJson(ODataResponse response) {
        JsonObject.createFrom response.getEntityAsStream()
    }

    private static String integrationKeyFor(final InboundRequestModel request) {
        "${request.creationtime.time}|null|${request.getProperty('integrationKey')}"
    }

    private String integrationKeyFor(final InboundRequestErrorModel error) {
        final InboundRequestModel request = error.getProperty('inboundRequest')
        "${error.creationtime.time}|${integrationKeyFor(request)}"
    }
}
