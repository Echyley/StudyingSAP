/*
 * [y] hybris Platform
 *  
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataServiceDocumentIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'ODataServiceDocument'
    private static final String TEST_IO = "${TEST_NAME}_IO"
    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = 'defaultODataFacade')
    private ODataFacade facade

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                ";${TEST_IO}",
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                ";${TEST_IO} ; Catalog         ; Catalog",
                ";${TEST_IO} ; CatalogVersion  ; CatalogVersion",
                ";${TEST_IO} ; Category        ; Category",

                '$item = integrationObjectItem(integrationObject(code), code)',
                '$descriptor = attributeDescriptor(enclosingType(code), qualifier)',
                '$refType=returnIntegrationObjectItem(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]; attributeName[unique = true]; $descriptor; $refType; unique[default = false]',
                ";${TEST_IO}:Catalog        ; id              ; Catalog:id              ;",
                ";${TEST_IO}:Category       ; code            ; Category:code           ;",
                ";${TEST_IO}:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; ${TEST_IO}:Catalog",
                ";${TEST_IO}:CatalogVersion ; version         ; CatalogVersion:version  ;")
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    @Test
    def "exposes integration object items for existing integration object"() {
        when:
        ODataResponse response = facade.handleRequest(oDataContext(TEST_IO))

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = JsonObject.createFrom response.getEntityAsStream()
        json.getCollection('d.EntitySets').size() == 3
        json.getCollectionOfObjects('d.EntitySets[*]').containsAll('CatalogVersions', 'Catalogs', 'Categories')
    }

    @Test
    def "returns empty collection for integration object with no items"() {
        given:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                ";${TEST_IO}_OtherIntegrationObj")

        when:
        ODataResponse response = facade.handleRequest(oDataContext(TEST_IO.concat("_OtherIntegrationObj")))

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = JsonObject.createFrom response.getEntityAsStream()
        json.getCollection("d.EntitySets").size() == 0
    }

    ODataContext oDataContext(final String integrationObj) {
        def request = ODataRequestBuilder.oDataGetRequest()
                .withAccepts(APPLICATION_JSON_VALUE)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(integrationObj))
                .build()

        contextGenerator.generate request
    }
}
