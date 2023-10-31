/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.ClassRule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectClassModelBuilder.integrationObjectClass
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil.findIntegrationObjectByCode
import static de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil.findIntegrationObjectClassByCodeAndIntegrationObject
import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.handleRequest
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class IntegrationServiceClassPersistenceIntegrationTest extends ServicelayerSpockSpecification {

    private static final def TEST_NAME = "IntegrationServiceClassPersistence"
    private static final def SERVICE_NAME = "IntegrationService"
    private static final def CLASS_ENTITY_SET = "IntegrationObjectClasses"
    private static final def OBJECT_CODE = "${TEST_NAME}_IO"
    private static final def ERROR_MESSAGE = "error.message.value"
    private static final def DEFAULT_ATTRIBUTE_NAME = "does-not-matter"
    private static final def INVALID_ATTRIBUTE_NAME = 'invalid_attribute_name'
    private static final def INVALID_READ_METHOD = 'invalid_read_method'
    private static final def INVALID_ROOT_POJO = 'invalid_root_pojo'
    private static final def INVALID_POJO_ITEM_MODELING = 'invalid_pojo_item_modeling'
    private static final String INVALID_CLASS_TYPE = "invalid_class_type";

    @Shared
    @ClassRule
    IntegrationServicesEssentialData essentialData = IntegrationServicesEssentialData.integrationServicesEssentialData()
    @AutoCleanup('cleanup')
    def integrationObject = integrationObject().cleanAlways().withCode(OBJECT_CODE)

    @Resource(name = 'defaultODataFacade')
    private ODataFacade facade

    @Test
    void "Successfully create class as root when no root class exists on the same IO"() {
        given: 'an IntegrationObject with a non-root IOClass Product exists'
        integrationObject
                .withClass(integrationObjectClass('Product').withType(ProductModel))
                .build()

        and: 'a request is generated to add a Category root IOClass to the IntegrationObject'
        def request = postRequest(CLASS_ENTITY_SET, rootClass('Category', CategoryModel.getName()))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        response.status == HttpStatusCodes.CREATED

        and: 'Category IOClass now exists'
        findIOClassByCode('Category').isPresent()
    }

    @Test
    void "Fails to create class when attribute does not exist within the IntegrationObjectClass"() {
        given: 'the payload references a not existing attribute'
        final def attributeName = 'not-existing-attribute'
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes',
                        [rootClass('Unit', UnitModel.getName())
                                 .withFieldValues('attributes',
                                         json().withField('attributeName', attributeName))]))

        when:
        def response = handleRequest facade, request

        then: 'the error is reported'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_ATTRIBUTE_NAME)
                .hasPathWithValueContaining(ERROR_MESSAGE, attributeName)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'does not exist')

        and: 'the integration object is not created'
        findIOClassByCode(OBJECT_CODE).isEmpty()
    }

    @Test
    void "Fails to create class when class type is a primitive type"() {
        given: 'the payload sets the IntegrationObjectClass type to a primitive type'
        integrationObject.build()
        def integerPrimitiveTypeCode = 'java.lang.Integer'
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes',
                        [ioClass('Unit', integerPrimitiveTypeCode)]))

        when:
        def response = handleRequest facade, request

        then: 'the error is reported'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_CLASS_TYPE)
                .hasPathWithValueContaining(ERROR_MESSAGE, integerPrimitiveTypeCode)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'IntegrationObjectClasses cannot be a primitive type')

        and: 'the integration object is not created'
        findIOClassByCode(OBJECT_CODE).isEmpty()
    }

    @Test
    void "Fails to create class when class root is an atomic type"() {
        given: 'the payload sets the IntegrationObjectClass type to an atomic type'
        integrationObject.build()
        def atomicTypeCode = 'de.hybris.platform.util.StandardDateRange'
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes',
                        [rootClass('Unit', atomicTypeCode)]))

        when:
        def response = handleRequest facade, request

        then: 'the error is reported'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_ROOT_POJO)
                .hasPathWithValueContaining(ERROR_MESSAGE, atomicTypeCode)
                .hasPathWithValueContaining(ERROR_MESSAGE, "atomic type [$atomicTypeCode] cannot have root set to true")

        and: 'the integration object is not created'
        findIOClassByCode(OBJECT_CODE).isEmpty()
    }

    @Test
    void "Fails to create class as root when a root class already exists on the same IO"() {
        given: 'an IntegrationObject with a root IOClass Product exists'
        integrationObject
                .withClass(integrationObjectClass('Product').withType(ProductModel).root())
                .build()

        and: 'a request is generated to add a Category root IOClass to the IntegrationObject'
        def request = postRequest(CLASS_ENTITY_SET, rootClass('Category', CategoryModel.getName()))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_ROOT_POJO)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'root')

        and: 'Category IOClass was not created'
        findIOClassByCode('Category').isEmpty()
    }

    @Test
    void "Fails to create IO it it includes items and classes in the payload"() {
        given:
        def ioCode = "New" + OBJECT_CODE
        def request =
                postRequest('IntegrationObjects',
                        json().withCode(ioCode)
                                .withField('items', [rootItem('Product')])
                                .withField('classes', [rootClass('Catalog', CatalogModel.getName())]))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_POJO_ITEM_MODELING)
                .hasPathWithValueContaining(ERROR_MESSAGE, "Mixing")
        and: 'The IntegrationObject does not exist'
        !findIntegrationObjectByCode(ioCode)
    }

    @Test
    void "Fails to create class when a item already exists on the same IO"() {
        given:
        integrationObject.withItem(integrationObjectItem('Product')).build()

        and:
        final ODataRequest request = postRequest(CLASS_ENTITY_SET, ioClass('Category', CategoryModel.getName()))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_POJO_ITEM_MODELING)
                .hasPathWithValueContaining(ERROR_MESSAGE, "Mixing")
        and: 'Category IOClass was not created'
        findIOClassByCode('Category')
                .isEmpty()
    }

    @Test
    void "Successfully changes an existing root class to not root"() {
        given:
        def io = integrationObject.withClass(integrationObjectClass('Product').withType(ProductModel)
                .root())
                .build()

        when:
        def response = handleRequest facade, postRequest(CLASS_ENTITY_SET, ioClass('Product', ProductModel.getName()))

        then: 'the expected response content is returned'
        response.status == HttpStatusCodes.CREATED
        and: 'Product IOClass is no longer the root'
        !findIntegrationObjectClassByCodeAndIntegrationObject('Product', io).root
    }

    @Test
    void "Fails to create class when the type provided does not match the type in the type system"() {
        when:
        def invalidClassType = 'class.path.that.does.not.exist.for.CategoryModel'
        final ODataResponse response = handleRequest facade,
                postRequest(CLASS_ENTITY_SET, rootClass('Category', invalidClassType))

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, 'invalid_pojo_class')
                .hasPathWithValue(ERROR_MESSAGE, "The IntegrationObjectClass [${invalidClassType}] does not exist in the specified classpath.")

        and: 'Category IOClass was not created'
        findIOClassByCode('Category').isEmpty()
    }

    @Test
    void "Successfully send same root class twice"() {
        given: 'an IntegrationObject with a root IOClass Product exists'
        integrationObject
                .withClass(integrationObjectClass('Product').withType(ProductModel).root())
                .build()

        and: 'a request is generated to add a Product again as root IOClass to the IntegrationObject'
        def request = postRequest(CLASS_ENTITY_SET, rootClass('Product', ProductModel.getName()))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        response.status == HttpStatusCodes.CREATED

        and: 'Product IOClass is still the root'
        findIOClassByCode('Product').orElse(null)?.root
    }

    @Test
    void "Change existing item to root when no root item exists on the same IO"() {
        given:
        final String itemCode = 'Product'
        integrationObject.withClass(integrationObjectClass(itemCode).withType(ProductModel)).build()

        when:
        def response = handleRequest facade, postRequest(CLASS_ENTITY_SET, rootClass(itemCode, ProductModel.getName()))

        then: 'the expected response content is returned'
        response.status == HttpStatusCodes.CREATED
        and: 'Product IOClass has been updated to root'
        findIOClassByCode(itemCode).orElse(null)?.root
    }

    @Test
    void "Creates IntegrationObject with multiple classes where only 1 class as root"() {
        given: 'a request is generated to create an IntegrationObject with a root IOClass: Product and a non-root IOClass: Catalog'
        def request =
                postRequest('IntegrationObjects',
                        json().withCode(OBJECT_CODE)
                                .withField('classes', [rootClass('Product', ProductModel.getName()),
                                                       ioClass('Catalog', CatalogModel.getName())]))

        when:
        def response = handleRequest facade, request

        then: 'the expected response content is returned'
        response.status == HttpStatusCodes.CREATED

        and: 'The IntegrationObject has been created with the expected classes'
        def objectModel = findIntegrationObjectByCode(OBJECT_CODE)
        objectModel.classes.size() == 2
    }

    @Test
    void "Fails to create IntegrationObject with no root classes"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [ioClass('Product', ProductModel.getName())]))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_ROOT_POJO)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'root')
                .hasPathWithValueContaining(ERROR_MESSAGE, OBJECT_CODE)
        and: 'The IntegrationObject does not exist'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "Fails to create IntegrationObject with multiple root classes"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Product', ProductModel.getName()),
                                       rootClass('Catalog', CatalogModel.getName())]))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_ROOT_POJO)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'root')
                .hasPathWithValueContaining(ERROR_MESSAGE, OBJECT_CODE)
        and: 'The IntegrationObject does not exist'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "Fails to create Integration Object when providing a class attribute to a reference to a class that does not exist"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Product', ProductModel.getName())
                                               .withField('attributes', [
                                                       json().withField('attributeName', 'catalogVersion')
                                                               .withField('unique', false)
                                                               .withField('readMethod', 'getCatalogVersion')
                                                               .withField('returnIntegrationObjectClass',
                                                                       ioClass('CatalogVersion', CatalogVersionModel.getName()))])]))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, "missing_nav_property")
                .hasPathWithValueContaining(ERROR_MESSAGE, 'does not exist')
        and: 'The IntegrationObject does not exist'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "Fails to create an IntegrationObjectClass when providing a class attribute to a reference to a class that is a different type"() {
        given:
        def jsonBody = rootClass('Product', ProductModel.getName())
                .withField('attributes', [
                        json().withField('attributeName', 'catalogVersion')
                                .withField('unique', false)
                                .withField('readMethod', 'getCatalogVersion')
                                .withField('returnIntegrationObjectClass',
                                        ioClass('Product', ProductModel.getName()))])

        when:
        def response = handleRequest facade, postRequest(CLASS_ENTITY_SET, jsonBody)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_READ_METHOD)
                .hasPathWithValueContaining(ERROR_MESSAGE, 'readMethod')
                .hasPathWithValueContaining(ERROR_MESSAGE, 'does not match the returnIntegrationObjectClass type.')
        and: 'Product IOClass was not created'
        findIOClassByCode('Product').isEmpty()
    }

    @Test
    void "Fails to create IntegrationObject if an attribute has read method that does not exist"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Product', ProductModel.getName())
                                               .withField('attributes', [attribute(attributeName: 'unit', readMethod: 'invalidMethodName')])]))

        when:
        def response = handleRequest(facade, request)

        then: 'the expected response content is returned'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, INVALID_READ_METHOD)
                .hasPathWithValueContaining(ERROR_MESSAGE, "readMethod")
                .hasPathWithValueContaining(ERROR_MESSAGE, "does not exist")
        and: 'The IntegrationObject does not exist'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "Fails to create an IntegrationObjectClass if an attribute has a readMethod of type void"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Test', TestPojo.name)
                                               .withField('attributes', [attribute(readMethod: 'doVoid')])]))

        when:
        def response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, 'invalid_read_method')
                .hasPathWithValueContaining(ERROR_MESSAGE, "readMethod")
                .hasPathWithValueContaining(ERROR_MESSAGE, "is VOID")

        and: 'The IO is not created'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "Fails to create an IntegrationObjectClass if an attribute has a readMethod with non-empty unlocalized parameters"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Test', TestPojo.name)
                                               .withField('attributes', [attribute(readMethod: 'getIndexedProperty')])]))

        when:
        def response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue(ERROR_CODE, 'invalid_read_method')
                .hasPathWithValueContaining(ERROR_MESSAGE, "readMethod")
                .hasPathWithValueContaining(ERROR_MESSAGE, "parameters")

        and: 'The IO is not created'
        !findIntegrationObjectByCode(OBJECT_CODE)
    }

    @Test
    void "IntegrationObject gets created when readMethod has localized parameters"() {
        given:
        def request = postRequest('IntegrationObjects', json()
                .withCode(OBJECT_CODE)
                .withField('classes', [rootClass('Test', TestPojo.name)
                                               .withField('attributes', [attribute(readMethod: 'getByLocale')])]))

        when:
        def response = handleRequest(facade, request)

        then: 'An IO with TestPojo was created'
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)
    }

    private static JsonBuilder attribute(final Map<String, Object> values) {
        json().withField('attributeName', values.get('attributeName', DEFAULT_ATTRIBUTE_NAME))
                .withField('unique', values.get('unique', true))
                .withField('readMethod', values.get('readMethod', ''))
    }

    private static ODataRequest postRequest(final String entitySet, final JsonBuilder requestBody) {
        postRequestBuilder(SERVICE_NAME, entitySet, APPLICATION_JSON_VALUE)
                .withAcceptLanguage(Locale.ENGLISH)
                .withBody(requestBody)
                .build()
    }

    private static JsonBuilder ioClass(String itemCode, String typeCode) {
        json()
                .withCode(itemCode)
                .withField('type', typeCode)
                .withField('root', false)
                .withField('integrationObject', json().withCode(OBJECT_CODE))
    }

    private static JsonBuilder rootItem(String itemCode, String iocode = OBJECT_CODE) {
        json()
                .withCode(itemCode)
                .withField("type", json().withCode(itemCode))
                .withField('root', true)
                .withField('integrationObject', json().withCode(iocode))
    }

    private static JsonBuilder rootClass(String itemCode, String typeCode, String iocode = OBJECT_CODE) {
        json()
                .withCode(itemCode)
                .withField('type', typeCode)
                .withField('root', true)
                .withField('integrationObject', json().withCode(iocode))
    }

    private Optional<IntegrationObjectClassModel> findIOClassByCode(final String classCode) {
        IntegrationTestUtil
                .findAny(IntegrationObjectClassModel.class,
                        integrationObjectClass -> integrationObjectClass.getCode().equals(classCode))
    }
}
