/*
 *  Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.ClassificationBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.ODataSchema
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.odata2webservices.util.Odata2WebServicesEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.ClassRule
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.CatalogIntegrationObjectItemPayloadBuilder.catalogIntegrationObjectItem
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.CatalogVersionIntegrationObjectItemPayloadBuilder.catalogVersionIntegrationObjectItem
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.CategoryIntegrationObjectItemPayloadBuilder.categoryIntegrationObjectItem
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.ClassificationAttributeAssignmentPayloadBuilder.classificationAttributeAssignment
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.IntegrationObjectItemClassificationAttributePayloadBuilder.classificationAttribute
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.IntegrationObjectPayloadBuilder.integrationObject
import static de.hybris.platform.odata2webservices.odata.builders.meta.payload.ProductIntegrationObjectItemPayloadBuilder.productIntegrationObjectItem
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class IntegrationServiceGetIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = "IntegrationServiceGet"
    private static final String IOI_CODE = "${TEST_NAME}_MyProduct"
    private static final String CONTEXT_IO = "${TEST_NAME}_IO"
    private static final Set CONTEXT_IO_ITEMS = ['Product', 'Catalog', 'CatalogVersion']
    private static final String SERVICE_NAME = 'IntegrationService'
    private static final String CLASS_SYSTEM = "${TEST_NAME}_Electronics"
    private static final String CLASS_VERSION = "${TEST_NAME}_Phones"
    private static final String CLASSIFICATION_CLASS = "${TEST_NAME}_phoneSpecs"
    private static final String CLASS_SYSTEM_VERSION = "$CLASS_SYSTEM:$CLASS_VERSION"
    private static final String PRODUCT_WITH_CLASS_ATTRIBUTES_IO = "${TEST_NAME}_ProductWithClassAttributesIO"

    @Shared
    @ClassRule
    Odata2WebServicesEssentialData essentialData = Odata2WebServicesEssentialData.odata2WebservicesEssentialData().withDependencies()
    @Shared
    @ClassRule
    ClassificationBuilder classificationBuilder = ClassificationBuilder.classification()
            .withSystem(CLASS_SYSTEM)
            .withVersion(CLASS_VERSION)
            .withClassificationClass(CLASSIFICATION_CLASS)
            .withAttribute(ClassificationBuilder.attribute().withName('brand').mandatory().number())
            .withAttribute(ClassificationBuilder.attribute().withName('model').mandatory().number())

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade
    @Resource(name = "oDataSchemaGenerator")
    private SchemaGenerator generator

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $CONTEXT_IO",
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                "                                   ; $CONTEXT_IO                           ; Product            ; Product",
                "                                   ; $CONTEXT_IO                           ; CatalogVersion     ; CatalogVersion",
                "                                   ; $CONTEXT_IO                           ; Catalog            ; Catalog",
                '$item = integrationObjectItem(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]      ; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code)',
                "                                            ; $CONTEXT_IO:Catalog       ; id                          ; Catalog:id",
                "                                            ; $CONTEXT_IO:Product       ; code                        ; Product:code",
                "                                            ; $CONTEXT_IO:Product       ; catalogVersion              ; Product:catalogVersion                             ; $CONTEXT_IO:CatalogVersion",
                "                                            ; $CONTEXT_IO:CatalogVersion; version                     ; CatalogVersion:version",
                "                                            ; $CONTEXT_IO:CatalogVersion; catalog                     ; CatalogVersion:catalog                             ; $CONTEXT_IO:Catalog"
        )
    }

    def cleanup() {
        IntegrationTestUtil.remove(IntegrationObjectModel) { it.code == CONTEXT_IO }
    }

    @Test
    def "GET IntegrationObjects returns all integration objects"() {
        given:
        def context = oDataGetContext("IntegrationObjects")

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects("d.results").size() == essentialData.allIntegrationObjectCodes.size() + 1
        json.getCollectionOfObjects('d.results[*].code').containsAll essentialData.allIntegrationObjectCodes
        json.getCollectionOfObjects('d.results[*].code').contains CONTEXT_IO
    }

    @Test
    def "GET IntegrationObjectItems returns all integration object items"() {
        given:
        def params = ['$top': '1000']
        def context = oDataGetContext("IntegrationObjectItems", params)

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects("d.results[*].code").toSet().size() == essentialData.allIntegrationObjectItemCodes.size() + CONTEXT_IO_ITEMS.size()
        json.getCollectionOfObjects('d.results[*].code').containsAll essentialData.allIntegrationObjectItemCodes
        json.getCollectionOfObjects('d.results[*].code').containsAll CONTEXT_IO_ITEMS
    }

    @Test
    @Unroll
    def "GET IntegrationObjectItemAttributes returns all attributes for type #itemType"() {
        given: "A large page size to make sure the first page will include all attributes that we expect"
        def params = ['$filter': "code eq " + "'$itemType'", '$expand': 'attributes']
        def context = oDataGetContext("IntegrationObjectItems", params)

        when:
        ODataResponse response = facade.handleRequest(context)

        then: 'verify the total number of attributes and that each type has the expected ones'
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects('d.results[*].attributes.results[*]').size() == expectedAttributes.size()
        json.getCollectionOfObjects('d.results[*].attributes.results[*].attributeName').containsAll(expectedAttributes)

        where:
        itemType                                       | expectedAttributes
        'Product'                                      | ['code', 'catalogVersion']
        'CatalogVersion'                               | ['version', 'catalog']
        'Catalog'                                      | ['id']
        'IntegrationObject'                            | ['code', 'integrationType', 'items', 'classes']
        'IntegrationType'                              | ['code']
        'ItemTypeMatchEnum'                            | ['code']
        'IntegrationObjectClass'                       | ['code', 'type', 'integrationObject', 'root', 'attributes']
        'IntegrationObjectClassAttribute'              | ['attributeName', 'returnIntegrationObjectClass', 'unique', 'readMethod', 'integrationObjectClass']
        'IntegrationObjectItem'                        | ['code', 'type', 'itemTypeMatch', 'integrationObject', 'root', 'attributes', 'classificationAttributes', 'virtualAttributes']
        'IntegrationObjectItemAttribute'               | ['attributeName', 'returnIntegrationObjectItem', 'attributeDescriptor', 'unique', 'autoCreate', 'integrationObjectItem']
        'IntegrationObjectItemClassificationAttribute' | ['attributeName', 'classAttributeAssignment', 'integrationObjectItem', 'returnIntegrationObjectItem']
        'ClassificationSystem'                         | ['id']
        'ClassificationSystemVersion'                  | ['catalog', 'version']
        'ClassAttributeAssignment'                     | ['classificationClass', 'classificationAttribute']
        'ClassificationClass'                          | ['code', 'catalogVersion']
        'ClassificationAttribute'                      | ['code', 'systemVersion']
        'AttributeDescriptor'                          | ['qualifier', 'enclosingType', 'qualifier', 'enclosingType', 'attributeType', 'unique', 'partOf', 'optional', 'localized', 'generate']
        'ComposedType'                                 | ['code', 'code']
        'InboundChannelConfiguration'                  | ['integrationObject', 'authenticationType']
        'AuthenticationType'                           | ['code']
        'IntegrationObjectVirtualAttributeDescriptor'  | ['code', 'logicLocation', 'type']
        'IntegrationObjectItemVirtualAttribute'        | ['integrationObjectItem', 'attributeName', 'retrievalDescriptor']
        'Type'                                         | ['code', 'code']
    }

    @Test
    def "GET IntegrationObjectItemClassificationAttributes returns classification attributes for Product"() {
        given:
        classificationAttributesForProduct()
        and:
        def context = oDataGetContext("IntegrationObjectItemClassificationAttributes")

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects('d.results[*].attributeName').containsAll('brand', 'model')
    }

    @Test
    def "POST IntegrationObjects, IntegrationObjectItem and IntegrationObjectItemAttribute creates new service"() {
        given:
        def ioCode = "CategoryOne"
        categoryIntegrationObjectIsCreated(ioCode)

        when:
        def schema = ODataSchema.from(generator.generateSchema(getIntegrationObjectItemModelDefinitions()))

        then:
        with(schema)
                {
                    getEntityTypeNames().containsAll("Category", TestConstants.LOCALIZED_ENTITY_PREFIX + "Category")
                    getEntityType("Category").getPropertyNames().containsAll("code", "name")
                    getEntityType("Category").getAnnotatableProperty("code").getAnnotationNames() == ["Nullable", "s:IsUnique"]
                    getEntityType("Category").getAnnotatableProperty("name").getAnnotationNames() == ["s:IsLanguageDependent", "Nullable"]
                    getEntityType("Category").getNavigationPropertyNames() == ["localizedAttributes"]
                }

        cleanup:
        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == ioCode }
    }

    @Test
    def "POST an IO with Classification Attributes for type Product creates schema containing those Classification Attributes"() {
        given: 'product IO with brand classification attribute'
        def body = integrationObject(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                .withItems(
                        productIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .withIntegrationObjectItemCode(IOI_CODE)
                                .withClassificationAttributes(contextClassAttribute('brand')).build(),
                        contextCatalogVersionIntegrationObjectItem(),
                        contextCatalogIntegrationObjectItem()).build()
        and: 'product is POSTed to the integration api'
        productIntegrationObjectIsCreated(body)

        when:
        "schema is generate for $IOI_CODE"
        def schema = ODataSchema.from(generator.generateSchema(IntegrationTestUtil.findAll(IntegrationObjectItemModel, { it.code == IOI_CODE })))

        then: 'schema contains regular and classification attributes'
        with(schema)
                {
                    getEntityTypeNames().containsAll(IOI_CODE)
                    getEntityType(IOI_CODE).getPropertyNames().containsAll("code", "brand")
                    getEntityType(IOI_CODE).getNavigationPropertyNames().containsAll("catalogVersion")
                }
    }

    @Test
    def "PATCH an IO with Classification Attributes for type Product updates schema with new Classification Attributes"() {
        given: 'product IOI with brand classification attribute'
        def body = integrationObject(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                .withItems(
                        productIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .withIntegrationObjectItemCode(IOI_CODE)
                                .withClassificationAttributes(contextClassAttribute('brand'))
                                .build(),
                        catalogVersionIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .build(),
                        catalogIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .build())
                .build()
        and: 'product is POSTed to the integration api'
        productIntegrationObjectIsCreated(body)
        when: 'product is PATCHed to the integration api with brand and model classification attributes'
        def patchedBody = integrationObject(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                .withItems(
                        productIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .withIntegrationObjectItemCode(IOI_CODE)
                                .withClassificationAttributes(
                                        contextClassAttribute('brand'), contextClassAttribute('model'))
                                .build(),
                        catalogVersionIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .build(),
                        catalogIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                                .build())
                .build()

        productIntegrationObjectIsPatched(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, patchedBody)

        and: 'schema is generated for MyProduct'
        def schema = ODataSchema.from(generator.generateSchema(IntegrationTestUtil.findAll(IntegrationObjectItemModel,
                { it.code == IOI_CODE })))

        then: 'schema contains regular and PATCHed classification attributes'
        with(schema)
                {
                    getEntityTypeNames().containsAll(IOI_CODE)
                    getEntityType(IOI_CODE).getPropertyNames().containsAll("code", "brand", "model")
                }
    }

    @Test
    def "POST multiple IntegrationObjects including duplicated Attribute name/descriptor creates different Integration Objects"() {
        given:
        def ioCode1 = "CategoryOne"
        def ioCode2 = "CategoryTwo"
        categoryIntegrationObjectIsCreated(ioCode1)
        categoryIntegrationObjectIsCreated(ioCode2)

        when:
        ODataResponse response = facade.handleRequest(oDataGetContext("IntegrationObjects"))

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects('d.results[*].code').containsAll('CategoryOne', 'CategoryTwo')

        cleanup:
        IntegrationTestUtil.remove IntegrationObjectModel, { it.code == ioCode1 || it.code == ioCode2 }
    }

    def classificationAttributesForProduct() {
        IntegrationTestUtil.importImpEx(
                '$item = integrationObjectItem(integrationObject(code), code)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$classificationAssignment = classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                'INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment',
                "                                                          ; $CONTEXT_IO:Product ; brand                       ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS:$CLASS_SYSTEM_VERSION:brand",
                "                                                          ; $CONTEXT_IO:Product ; model                       ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS:$CLASS_SYSTEM_VERSION:model"
        )
    }

    def categoryIntegrationObjectIsCreated(String integrationObjectCode) {
        def categoryOneIntegrationObjectContext = oDataPostContext("IntegrationObjects",
                integrationObject(integrationObjectCode)
                        .withItems(
                                categoryIntegrationObjectItem(integrationObjectCode)
                                        .build()).build())
        facade.handleRequest(categoryOneIntegrationObjectContext)
    }

    def productIntegrationObjectIsCreated(String productBody) {
        def productIntegrationObjectContext = oDataPostContext("IntegrationObjects", productBody)
        facade.handleRequest(productIntegrationObjectContext)
    }

    def productIntegrationObjectIsPatched(String integrationObjectCode, String patchedBody) {
        facade.handleRequest patch(SERVICE_NAME, "IntegrationObjects", integrationObjectCode, patchedBody)
    }

    static ODataContext patch(String serviceName, String entitySet, String key, String body) {
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPatchRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(serviceName)
                        .withEntitySet(entitySet)
                        .withEntityKeys(key))
                .withContentType('application/json')
                .withAccepts('application/json')
                .withBody(body)
    }

    ODataContext oDataGetContext(String entitySetName) {
        oDataGetContext(entitySetName, [:])
    }

    ODataContext oDataGetContext(String entitySetName, Map params) {
        def request = ODataRequestBuilder.oDataGetRequest()
                .withAccepts(APPLICATION_JSON_VALUE)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(entitySetName))
                .withParameters(params)
                .build()

        contextGenerator.generate request
    }

    ODataContext oDataPostContext(String entitySetName, String content) {
        def request = ODataFacadeTestUtils
                .oDataPostRequest(SERVICE_NAME, entitySetName, content, APPLICATION_JSON_VALUE)

        contextGenerator.generate request
    }

    def contextCatalogIntegrationObjectItem() {
        catalogIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO).build()
    }

    def contextCatalogVersionIntegrationObjectItem() {
        catalogVersionIntegrationObjectItem(PRODUCT_WITH_CLASS_ATTRIBUTES_IO).build()
    }

    def contextClassAttribute(String attributeName) {
        classificationAttribute()
                .withIntegrationObjectCode(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
                .withIntegrationObjectItemCode(IOI_CODE)
                .withType('Product')
                .withAttributeName(attributeName)
                .withClassAttributeAssignment(contextClassAttributeAssignment(attributeName))
                .build()
    }

    def contextClassAttributeAssignment(String attributeName) {
        classificationAttributeAssignment()
                .withAttributeName(attributeName)
                .withClassificationClass(CLASSIFICATION_CLASS)
                .withClassificationSystem(CLASS_SYSTEM)
                .withClassificationVersion(CLASS_VERSION)
                .build()
    }

    def extractEntitiesFrom(ODataResponse response) {
        extractBodyWithExpectedStatus(response, HttpStatusCodes.OK)
    }

    def extractBodyWithExpectedStatus(ODataResponse response, HttpStatusCodes expStatus) {
        assert response.getStatus() == expStatus
        JsonObject.createFrom response.getEntity() as InputStream
    }

    def getIntegrationObjectItemModelDefinitions() {
        IntegrationTestUtil.findAll(IntegrationObjectItemModel.class) as Collection<IntegrationObjectItemModel>
    }
}
