/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.core.model.c2l.CurrencyModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.europe1.model.PriceRowModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.search.ItemTypeMatch
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.XmlObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.content.ODataAtomProductBuilder
import de.hybris.platform.odata2services.odata.content.ODataJsonProductBuilder
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
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_ATOM_XML_ENTRY_UTF8
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataIntegrationKeyValueIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "ODataIntegrationKeyValue"
    private static final String SERVICE_NAME = "IntegrationService"
    private static final String TEST_PRICE_ROW = "${TEST_NAME}_TestPriceRow"
    private static final String PRODUCT_IO = "${TEST_NAME}_IntegrationService"
    private static final String CATALOG = "${TEST_NAME}_Catalog"
    private static final String VERSION = "${TEST_NAME}_Version"

    @Shared
    @ClassRule
    Odata2WebServicesEssentialData essentialData = Odata2WebServicesEssentialData.odata2WebservicesEssentialData().withDependencies()
    @AutoCleanup('cleanup')
    def priceRowIo = integrationObject().withCode(TEST_PRICE_ROW)
            .withItem(integrationObjectItem().withCode('PriceRow').root().withTypeMatch(ItemTypeMatch.ALL_SUB_AND_SUPER_TYPES)
                    .withAttribute(integrationObjectItemAttribute().withName('creationtime').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('price').unique()))
    @AutoCleanup('cleanup')
    IntegrationObjectModelBuilder integrationObject = integrationObject().withCode(PRODUCT_IO)
            .withItem(integrationObjectItem().withCode('Catalog')
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
            .withItem(integrationObjectItem().withCode('CatalogVersion')
                    .withAttribute(integrationObjectItemAttribute().withName('version'))
                    .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog').autoCreate()))
            .withItem(integrationObjectItem().withCode('Product')
                    .withAttribute(integrationObjectItemAttribute('code'))
                    .withAttribute(integrationObjectItemAttribute('name'))
                    .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion').autoCreate()))

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade
    @Resource(name = "oDataSchemaGenerator")
    private SchemaGenerator generator

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-3263')
    def "GET IntegrationObjectItemAttributes include the expected integrationKeys"() {
        given: "A large page size to make sure the first page will include all attributes that we expect"
        def params = ['\$top': '1000']
        def context = oDataGetContext("IntegrationObjectItemAttributes", params)

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects('d.results[*].integrationKey').containsAll(
                'integrationObject|InboundChannelConfiguration|'+SERVICE_NAME,
                'authenticationType|InboundChannelConfiguration|'+SERVICE_NAME,
                'code|AuthenticationType|'+SERVICE_NAME,
                'code|IntegrationObject|'+SERVICE_NAME,
                'items|IntegrationObject|'+SERVICE_NAME,
                'code|IntegrationObjectItem|'+SERVICE_NAME,
                'type|IntegrationObjectItem|'+SERVICE_NAME,
                'itemTypeMatch|IntegrationObjectItem|'+SERVICE_NAME,
                'integrationObject|IntegrationObjectItem|'+SERVICE_NAME,
                'root|IntegrationObjectItem|'+SERVICE_NAME,
                'attributes|IntegrationObjectItem|'+SERVICE_NAME,
                'classificationAttributes|IntegrationObjectItem|'+SERVICE_NAME,
                'virtualAttributes|IntegrationObjectItem|'+SERVICE_NAME,
                'attributeName|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'returnIntegrationObjectItem|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'attributeDescriptor|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'unique|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'autoCreate|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'integrationObjectItem|IntegrationObjectItemAttribute|'+SERVICE_NAME,
                'attributeName|IntegrationObjectItemClassificationAttribute|'+SERVICE_NAME,
                'classAttributeAssignment|IntegrationObjectItemClassificationAttribute|'+SERVICE_NAME,
                'integrationObjectItem|IntegrationObjectItemClassificationAttribute|'+SERVICE_NAME,
                'returnIntegrationObjectItem|IntegrationObjectItemClassificationAttribute|'+SERVICE_NAME,
                'attributeName|IntegrationObjectItemVirtualAttribute|'+SERVICE_NAME,
                'integrationObjectItem|IntegrationObjectItemVirtualAttribute|'+SERVICE_NAME,
                'retrievalDescriptor|IntegrationObjectItemVirtualAttribute|'+SERVICE_NAME,
                'code|IntegrationObjectVirtualAttributeDescriptor|'+SERVICE_NAME,
                'logicLocation|IntegrationObjectVirtualAttributeDescriptor|'+SERVICE_NAME,
                'type|IntegrationObjectVirtualAttributeDescriptor|'+SERVICE_NAME,
                'id|ClassificationSystem|'+SERVICE_NAME,
                'catalog|ClassificationSystemVersion|'+SERVICE_NAME,
                'version|ClassificationSystemVersion|'+SERVICE_NAME,
                'classificationClass|ClassAttributeAssignment|'+SERVICE_NAME,
                'classificationAttribute|ClassAttributeAssignment|'+SERVICE_NAME,
                'code|ClassificationClass|'+SERVICE_NAME,
                'catalogVersion|ClassificationClass|'+SERVICE_NAME,
                'code|ClassificationAttribute|'+SERVICE_NAME,
                'systemVersion|ClassificationAttribute|'+SERVICE_NAME,
                'qualifier|AttributeDescriptor|'+SERVICE_NAME,
                'enclosingType|AttributeDescriptor|'+SERVICE_NAME,
                'code|ComposedType|'+SERVICE_NAME,
                'code|Type|'+SERVICE_NAME,
                'code|ItemTypeMatchEnum|'+SERVICE_NAME
        )
    }

    @Test
    def "Attribute of Calendar type could be transformed to String and used as a part of integrationKey."() {
        given:
        priceRowIo.build()
        and:
        importImpEx(
                "INSERT_UPDATE Unit;code[unique=true];unitType[unique=true]",
                "                  ;piecesTest           ;EA",

                "INSERT_UPDATE Currency;isocode[unique=true]",
                "                      ;USD",

                "INSERT_UPDATE PriceRow;price[unique=true];currency(isocode)[unique=true];unit(code)[default=piecesTest]",
                "                      ;1555             ;USD")

        def context = oDataContextForPriceRows(['$filter': "price eq 1555"])

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        def millisecondsOfCreationtime = json.getString("d.results[0].creationtime") =~ /Date\((.*)\)/
        def millisecondsOfIntegrationKey = json.getString("d.results[0].integrationKey") =~ /(.*)\|/
        millisecondsOfCreationtime[0][1] == millisecondsOfIntegrationKey[0][1]

        cleanup:
        IntegrationTestUtil.removeSafely UnitModel, { it.code == 'piecesTest' }
        IntegrationTestUtil.removeSafely PriceRowModel, { it.price == 1555 }
        IntegrationTestUtil.removeSafely CurrencyModel, { it.isocode == 'USD' }
        IntegrationTestUtil.remove(IntegrationObjectModel) { it.code == TEST_PRICE_ROW }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-5606')
    def "response contains integrationKey when request contentType is application/atom+xml;type=entry;charset=utf-8"() {
        given:
        integrationObject.build()

        def productCode = "${PRODUCT_IO}_prod1"

        def content = xml().withCode(productCode).build()
        def request = post "Products", content, APPLICATION_ATOM_XML_ENTRY_UTF8

        when:
        def response = facade.handleRequest request

        then:
        response.status == HttpStatusCodes.CREATED

        and:
        response.getHeader("Content-Type") == "application/atom+xml;charset=utf-8;type=entry"
        def xmlContent = XmlObject.createFrom(response.entityAsStream)
        xmlContent.get("entry//content//properties//integrationKey") == "$VERSION|$CATALOG|$productCode"

        cleanup:
        IntegrationTestUtil.remove ProductModel, { it.code == "$productCode" }
        IntegrationTestUtil.remove CatalogModel, { it.id == "$CATALOG" }
    }

    ODataJsonProductBuilder json() {
        ODataJsonProductBuilder.product().withCatalog(CATALOG).withCatalogVersion(VERSION)
    }

    ODataAtomProductBuilder xml() {
        ODataAtomProductBuilder.product().withCatalog(CATALOG).withCatalogVersion(VERSION)
    }

    ODataContext post(String entitySetName, String body, String contentType = APPLICATION_JSON) {
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPostRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(PRODUCT_IO)
                        .withEntitySet(entitySetName))
                .withContentType(contentType)
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

    def extractEntitiesFrom(ODataResponse response) {
        extractBodyWithExpectedStatus(response, HttpStatusCodes.OK)
    }

    def extractBodyWithExpectedStatus(ODataResponse response, HttpStatusCodes expStatus) {
        assert response.getStatus() == expStatus
        JsonObject.createFrom response.getEntity() as InputStream
    }

    ODataContext oDataContextForPriceRows(Map params) {
        def request = requestBuilderForPriceRow(params, 'PriceRows').build()
        contextGenerator.generate request
    }

    def requestBuilderForPriceRow(Map params, String entitySetName) {
        ODataRequestBuilder.oDataGetRequest()
                .withAccepts(APPLICATION_JSON_VALUE)
                .withParameters(params)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(TEST_PRICE_ROW)
                        .withEntitySet(entitySetName))
    }
}
