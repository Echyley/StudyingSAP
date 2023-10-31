/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PostmanCollectionBuilderUnitTest extends JUnitPlatformSpecification {

    private static final String POSTMAN_COLLECTION_NAME = "ImportConfiguration"
    private static final String POSTMAN_COLLECTION_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    private static final String ITEM_NAME = "Scripts_scriptCode"
    private static final String EXPORT_URL = "{{hostUrl}}/odata2webservices/ScriptService/Scripts"
    private static final String AUTHENTICATION_TYPE = "basic"
    private static final String HTTP_METHOD = "POST"
    private static final String HOST_URL_KEY = "hostUrl"
    private static final String PASSWORD_KEY = "password"
    private static final String USERNAME_KEY = "username"
    private static final String PASSWORD_VALUE = "{{password}}"
    private static final String USERNAME_VALUE = "{{username}}"
    private static final String HOST_URL_VALUE = "{{hostUrl}}"

    def postmanCollectionBuilder = PostmanCollectionBuilder.postmanCollectionBuilder()

    @Test
    def "generate a Postman collection and verify the result"() {
        when:
        def postmanCollection = postmanCollectionBuilder.withExportEntities(getExportEntities(getRequestBody())).build()

        then:
        with(postmanCollection) {
            def item = items.get(0)
            items.size() == 1
            info.name == POSTMAN_COLLECTION_NAME
            info.schema == POSTMAN_COLLECTION_SCHEMA
            item.name == ITEM_NAME
            item.request.url.raw == EXPORT_URL
            item.request.url.hosts.contains(HOST_URL_VALUE)
            item.request.url.paths.containsAll("odata2webservices", "ScriptService", "Scripts")
            item.request.auth.type == AUTHENTICATION_TYPE
            item.request.auth.credentials.size() == 2
            item.request.auth.credentials.get(0).key == PASSWORD_KEY
            item.request.auth.credentials.get(0).value == PASSWORD_VALUE
            item.request.auth.credentials.get(1).key == USERNAME_KEY
            item.request.auth.credentials.get(1).value == USERNAME_VALUE
            item.request.method == HTTP_METHOD
            item.request.body.raw == getRequestBody()
            variables.size() == 3
            variables.get(0).key == PASSWORD_KEY
            variables.get(1).key == USERNAME_KEY
            variables.get(2).key == HOST_URL_KEY
            auth.type == AUTHENTICATION_TYPE
            auth.credentials.size() == 2
            auth.credentials.get(0).key == PASSWORD_KEY
            auth.credentials.get(0).value == PASSWORD_VALUE
            auth.credentials.get(1).key == USERNAME_KEY
            auth.credentials.get(1).value == USERNAME_VALUE
        }
    }

    @Test
    @Unroll
    def "verify the generated name [#itemName] of the Postman collection item"() {
        when:
        def postmanCollection = postmanCollectionBuilder.withExportEntities(exportEntities).build()

        then:
        with(postmanCollection) {
            items.size() == 1
            items.get(0).getName() == itemName
        }

        where:
        exportEntities                                           | itemName
        getExportEntities(getRequestBodyWithoutIntegrationKey()) | "Scripts"
        getExportEntities(getRequestBody())                      | "Scripts_scriptCode"
    }

    def getExportEntities(def body) {
        def exportEntity = new ExportEntity()
        exportEntity.setRequestUrl(EXPORT_URL)
        exportEntity.setRequestBodies(Set.of(body))
        return Set.of(exportEntity)
    }

    def getRequestBody() {
        '''\
{
    "code": "scriptCode",
    "autoDisabling": false,
    "scriptType": {
        "code": "GROOVY"
    },
    "disabled": false,
    "content": " script content",
    "integrationKey": "scriptCode",
    "localizedAttributes": []
}'''
    }

    def getRequestBodyWithoutIntegrationKey() {
        '''\
{
    "code": "scriptCode",
    "autoDisabling": false,
    "scriptType": {
        "code": "GROOVY"
    },
    "disabled": false,
    "content": " script content",
    "localizedAttributes": []
}'''
    }

}
