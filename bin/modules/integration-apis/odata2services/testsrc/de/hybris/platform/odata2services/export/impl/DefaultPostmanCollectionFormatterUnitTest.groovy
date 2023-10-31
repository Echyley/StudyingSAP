/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.node.ObjectNode
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultPostmanCollectionFormatterUnitTest extends JUnitPlatformSpecification {

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

    def postmanCollectionFormatter = new DefaultPostmanCollectionFormatter()

    @Test
    def "format a Postman collection as a Json string and verify the result"() {
        given:
        def postmanCollection = PostmanCollectionBuilder.postmanCollectionBuilder().withExportEntities(getExportEntities()).build()

        when:
        def postmanCollectionJson = JsonObject.createFrom(postmanCollectionFormatter.format(postmanCollection))

        then:
        with(postmanCollectionJson) {
            getString("info.name") == POSTMAN_COLLECTION_NAME
            getString("info.schema") == POSTMAN_COLLECTION_SCHEMA
            getCollection("item[*]").size() == 1
            getString("item[0].name") == ITEM_NAME
            getString("item[0].request.url.raw") == EXPORT_URL
            getCollection("item[0].request.url.host").contains(HOST_URL_VALUE)
            getCollection("item[0].request.url.path").containsAll("odata2webservices", "ScriptService", "Scripts")
            getString("item[0].request.auth.type") == AUTHENTICATION_TYPE
            getCollection("item[0].request.auth.basic").size() == 2
            getCollection("item[0].request.auth.basic").containsAll([[key: PASSWORD_KEY, value: PASSWORD_VALUE, type: "string"],
                                                                     [key: USERNAME_KEY, value: USERNAME_VALUE, type: "string"]])
            getString("item[0].request.method") == HTTP_METHOD
            getString("item[0].request.body.raw") == getRequestBody()
            getCollection("variable").size() == 3
            getCollection("variable").containsAll([[id: PASSWORD_KEY, key: PASSWORD_KEY, value: null],
                                                   [id: USERNAME_KEY, key: USERNAME_KEY, value: null],
                                                   [id: HOST_URL_KEY, key: HOST_URL_KEY, value: null]])
        }

    }

    @Test
    def "throws an exception when formatting a Postman collection as a Json string"() {
        given:"a stub ObjectMapper that throws a JsonGenerationException exception"
        postmanCollectionFormatter.setObjectMapper(getObjectMapper())

        when:
        def postmanCollection = PostmanCollectionBuilder.postmanCollectionBuilder().withExportEntities(getExportEntities()).build()
        postmanCollectionFormatter.format(postmanCollection)

        then:
        def ex = thrown(PostmanCollectionFormatterException)
        with(ex) {
            ex.message.contains("An error occurs while formatting a Postman collection.")
            ex.getCause() instanceof JsonGenerationException
        }
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

    def getObjectMapper() {
        Stub(ObjectMapper) {
            writerWithDefaultPrettyPrinter() >>
                    Stub(ObjectWriter) {
                        writeValueAsString(_) >> { throw new JsonGenerationException("Error while generating a Json string from a Postman collection.", null, null) }
                    }
            readValue(_ as String, _ as Class<Object>) >> Stub(ObjectNode)
        }
    }

    def getExportEntities() {
        def exportEntity = new ExportEntity()
        exportEntity.setRequestUrl(EXPORT_URL)
        exportEntity.setRequestBodies(Set.of(getRequestBody()))
        return Set.of(exportEntity)
    }

}
