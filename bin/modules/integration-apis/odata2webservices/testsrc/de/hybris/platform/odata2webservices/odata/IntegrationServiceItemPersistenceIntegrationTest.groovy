/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.ClassRule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectClassModelBuilder.integrationObjectClass
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject
import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.handleRequest
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class IntegrationServiceItemPersistenceIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "IntegrationServicePersistence"
    private static final SERVICE_NAME = "IntegrationService"
    private static final ITEMS_ENTITY_SET = "IntegrationObjectItems"
    private static final OBJECT_CODE = "${TEST_NAME}_IO"
    private static final String ERROR_MESSAGE = "error.message.value";
    private static final def INVALID_POJO_ITEM_MODELING = 'invalid_pojo_item_modeling'

    @Shared
    @ClassRule
    IntegrationServicesEssentialData essentialData = IntegrationServicesEssentialData.integrationServicesEssentialData()
    @AutoCleanup('cleanup')
    def integrationObject = integrationObject().cleanAlways().withCode(OBJECT_CODE)

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade

    @Test
    def "Fails to create item when a class already exists on the same IO"() {
        given:
        def io = integrationObject.withClass(integrationObjectClass("Product").withType(ProductModel).root())
                .build()
        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem('Category'))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_POJO_ITEM_MODELING)
                .hasPathWithValueContaining(ERROR_MESSAGE, "Mixing")
        and: 'Category IOClass was not created'
        !findIntegrationObjectItemByCodeAndIntegrationObject('Category', io)?.root
    }

    @Test
    def "Successfully create item as root when no root item exists on the same IO"() {
        given:
        def io = integrationObject.withItem(integrationObjectItem().withCode("Product"))
                .build()
        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem('Category'))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then:
        response.status == HttpStatusCodes.CREATED
        findIntegrationObjectItemByCodeAndIntegrationObject('Category', io)?.root
    }

    @Test
    def "Fails to create item as root when a root item already exists on the same IO"() {
        given:
        def io = integrationObject.withItem(integrationObjectItem().withCode("Product")
                .root())
                .build()

        when:
        final ODataResponse response = handleRequest facade, postRequest(ITEMS_ENTITY_SET, rootItem('Category'))

        then: "the expected error code and message segment is returned"
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, "invalid_attribute_value")
                .hasPathWithValueContaining(ERROR_MESSAGE, "root item")
        and: "Category IntegrationObjectItem does not exist"
        !findIntegrationObjectItemByCodeAndIntegrationObject("Category", io)
    }

    @Test
    def "Change an existing root item to not root"() {
        given:
        final String itemCode = 'Product'
        def io = integrationObject.withItem(integrationObjectItem().withCode(itemCode).root())
                .build()

        when:
        def response = handleRequest facade, postRequest(ITEMS_ENTITY_SET, item(itemCode))

        then:
        response.status == HttpStatusCodes.CREATED
        !findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, io).root
    }

    @Test
    def "Successfully send same root item twice"() {
        given:
        final String itemCode = 'Category'

        def io = integrationObject.withItem(integrationObjectItem().withCode(itemCode).root())
                .build()

        when:
        def response = handleRequest facade, postRequest(ITEMS_ENTITY_SET, rootItem(itemCode))

        then:
        response.status == HttpStatusCodes.CREATED
        findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, io)?.root
    }

    @Test
    def "Change existing item to root when no root item exists on the same IO"() {
        given:
        final String itemCode = "Category"
        def io = integrationObject.withItem(integrationObjectItem().withCode(itemCode))
                .build()

        when:
        def response = handleRequest facade, postRequest(ITEMS_ENTITY_SET, rootItem(itemCode))

        then:
        response.status == HttpStatusCodes.CREATED
        findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, io)?.root
    }

    @Test
    def "Creates IntegrationObject with multiple items where only 1 item as root"() {
        given:
        def request = postRequest("IntegrationObjects", json()
                .withCode(OBJECT_CODE)
                .withField('items', [rootItem('Product'), item('Catalog')]))

        when:
        def response = handleRequest facade, request

        then:
        response.status == HttpStatusCodes.CREATED
        def objectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(OBJECT_CODE)
        objectModel.items.size() == 2
    }

    @Test
    def "Fails to create IntegrationObject with multiple root items"() {
        given:
        def request = postRequest("IntegrationObjects", json()
                .withCode(OBJECT_CODE)
                .withField('items', [rootItem('Product'), rootItem('Catalog')]))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, 'invalid_attribute_value')
                .hasPathWithValueContaining(ERROR_MESSAGE, 'root')
        and: 'The IntegrationObject does not exist'
        !IntegrationObjectTestUtil.findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4076')
    def 'Creates attributes from the second occurrence of the item in payload'() {
        given: 'the IO payload contains Customer item twice and the second occurrence has attribute(s)'
        def payload = json()
                .withCode(OBJECT_CODE)
                .withFieldValues("items",
                        item('Customer'),
                        item('Customer').withFieldValues('attributes', uniqueAttribute('Customer', 'uid')))

        when: "the payload is sent to $SERVICE_NAME"
        def response = handleRequest facade, postRequest('IntegrationObjects', payload)

        then: 'request is successful'
        response.status == HttpStatusCodes.CREATED
        and: 'the created IO contains attribute(s) of the second item occurrence'
        IntegrationTestUtil.findAny(IntegrationObjectItemAttributeModel, {
            it.attributeName == 'uid' && it.integrationObjectItem.integrationObject.code == OBJECT_CODE
        }).present
    }

    private static ODataRequest postRequest(final String entitySet, final JsonBuilder requestBody) {
        postRequestBuilder(SERVICE_NAME, entitySet, APPLICATION_JSON_VALUE)
                .withAcceptLanguage(Locale.ENGLISH)
                .withBody(requestBody)
                .build()
    }

    private static JsonBuilder item(String itemCode) {
        json()
                .withCode(itemCode)
                .withField("type", json().withCode(itemCode))
                .withField("root", false)
                .withField("integrationObject", json().withCode(OBJECT_CODE))
    }

    private static JsonBuilder rootItem(String itemCode) {
        json()
                .withCode(itemCode)
                .withField("type", json().withCode(itemCode))
                .withField("root", true)
                .withField("integrationObject", json().withCode(OBJECT_CODE))
    }

    private static JsonBuilder uniqueAttribute(String type, String name) {
        attribute(type, name).withField('unique', true)
    }

    private static JsonBuilder attribute(String type, String name) {
        json()
                .withField('attributeName', name)
                .withField('attributeDescriptor', json()
                        .withField('qualifier', name)
                        .withField('enclosingType', json().withCode(type)))
    }
}
