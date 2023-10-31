/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.inboundservices.util.InboundMonitoringRule
import de.hybris.platform.inboundservices.util.InboundRequestPersistenceContext
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2services.config.ODataServicesConfiguration
import de.hybris.platform.odata2services.odata.content.ODataAtomProductBuilder
import de.hybris.platform.odata2services.odata.content.ODataJsonProductBuilder
import de.hybris.platform.odata2services.util.Odata2ServicesEssentialData
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.user.UserService
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME
import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.batchBuilder
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetBuilder.changeSetBuilder
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML_VALUE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataFacadeInboundMonitoringIntegrationTest extends ServicelayerTransactionalSpockSpecification {
    private static final def TEST_NAME = 'ODataFacadeInboundMonitoring'
    private static final def SERVICE_NAME = "${TEST_NAME}_IO"
    private static final def CATALOG = "${TEST_NAME}_Catalog"
    private static final def VERSION = "${TEST_NAME}_Version"
    private static final def PRODUCT = "${TEST_NAME}_Product"
    private static final def ENTITY_SET = 'Products'
    private static final String USER_ID = "${TEST_NAME}_testadmin"
    private static final String SAP_PASSPORT = 'my-sap-passport'

    @Shared
    @ClassRule
    Odata2ServicesEssentialData essentialData = Odata2ServicesEssentialData.odata2ServicesEssentialData()
    @Shared
    @ClassRule
    IntegrationObjectModelBuilder integrationObject = integrationObject().withCode(SERVICE_NAME)
            .withItem(integrationObjectItem().withCode('Catalog')
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
            .withItem(integrationObjectItem().withCode('CatalogVersion')
                    .withAttribute(integrationObjectItemAttribute().withName('version'))
                    .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog')))
            .withItem(integrationObjectItem().withCode('Product')
                    .withAttribute(integrationObjectItemAttribute('code'))
                    .withAttribute(integrationObjectItemAttribute('name'))
                    .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion')))
    @Rule
    public InboundRequestPersistenceContext requestPersistenceContext = InboundRequestPersistenceContext.create()
    @Rule
    public InboundMonitoringRule monitoring = InboundMonitoringRule.enabled()

    @Resource(name = 'oDataServicesConfiguration')
    private ODataServicesConfiguration configuration
    @Resource(name = 'oDataWebMonitoringFacade')
    private ODataFacade facade
    @Resource
    private ConfigurationService configurationService
    @Resource
    private UserService userService
    @Resource
    private FlexibleSearchService flexibleSearchService

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Catalog; id[unique=true]',
                "                     ; $CATALOG",
                'INSERT_UPDATE CatalogVersion; catalog(id)[unique=true]; version[unique=true]',
                "                            ; $CATALOG                ; $VERSION",

                'INSERT_UPDATE Language; isocode[unique=true]; name[lang=de]; name[lang=en]',
                '                      ; de                  ; Deutsch      ; German',

                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)',
                "                      ; $USER_ID          ; integrationadmingroup"
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.remove UserModel, { it.uid == USER_ID }
        IntegrationTestUtil.remove LanguageModel, { it.isocode == 'de' }
        IntegrationTestUtil.remove CatalogModel, { it.id == CATALOG }
        IntegrationTestUtil.remove ProductModel, { it.code == PRODUCT }
    }

    @Test
    def "inbound request contains response error when request content is invalid"() {
        when:
        def response = facade.handleRequest post('<invalid_content />')

        then:
        response.status == HttpStatusCodes.BAD_REQUEST

        and:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            getStatus() == IntegrationRequestStatus.ERROR
            getPayload() == null
            getErrors().size() == 1
            errors[0].code == 'odata_error'
            getUser().uid == USER_ID
            getSapPassport() == null
        }
    }

    @Test
    @Unroll
    def "inbound request contains #contentType request payload when success retention is enabled"() {
        given:
        logSuccessRequestPayload(true)

        and:
        def request = post content, contentType
        request.requestHeaders[SAP_PASSPORT_HEADER_NAME] = [SAP_PASSPORT]

        when:
        def response = facade.handleRequest request

        then:
        response.status == HttpStatusCodes.CREATED

        and:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            getStatus() == IntegrationRequestStatus.SUCCESS
            getMediaContent(getPayload()) == content
            getPayload().getMime().equalsIgnoreCase mimeType
            getErrors().isEmpty()
            getUser().uid == USER_ID
            getSapPassport() == SAP_PASSPORT
        }

        where:
        contentType                | content                          | mimeType
        APPLICATION_JSON_VALUE     | json().withCode(PRODUCT).build() | 'application/octet-stream'
        APPLICATION_ATOM_XML_VALUE | xml().withCode(PRODUCT).build()  | 'text/xml'
    }

    @Test
    def "inbound request does not contain request payload when success retention is disabled"() {
        given:
        logSuccessRequestPayload(false)

        when:
        def response = facade.handleRequest post(json().withCode(PRODUCT))

        then:
        response.status == HttpStatusCodes.CREATED

        and:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            getStatus() == IntegrationRequestStatus.SUCCESS
            getPayload() == null
            getErrors().isEmpty()
            getUser().uid == USER_ID
            getSapPassport() == null
        }
    }

    @Test
    def "inbound request is not logged when monitoring is off"() {
        given:
        monitoring.disabled()

        when:
        facade.handleRequest post('{ "code": "InvalidProduct" }')

        then:
        requestPersistenceContext.getAllMedia().isEmpty()
        requestPersistenceContext.searchAllInboundRequest().isEmpty()
        requestPersistenceContext.searchAllInboundRequestErrors().isEmpty()
    }

    @Test
    def "inbound request contains multiple batch changesets when success retention is enabled"() {
        given:
        logSuccessRequestPayload(true)

        and:
        def englishProduct = json().withName('a product')
        def germanProduct = json().withName('ein Produkt')
        def content = batchBuilder()
                .withChangeSet(changeSetBuilder()
                        .withUri('Products')
                        .withPart(Locale.ENGLISH, englishProduct)
                        .withPart(Locale.GERMAN, germanProduct))
                .build()

        when:
        facade.handleRequest batchODataPostRequest(content)

        then:
        def request = getInboundRequest()
        with(request) {
            getStatus() == IntegrationRequestStatus.SUCCESS
            def payload = getMediaContent getPayload()
            payload.contains 'a product'
            payload.contains 'ein Produkt'
            getErrors().isEmpty()
        }
    }

    @Test
    def "inbound request does not contain multiple batch changesets when success retention is disabled"() {
        given:
        logSuccessRequestPayload(false)

        and:
        def englishProduct = json().withName('a product')
        def germanProduct = json().withName('ein Produkt')
        def content = batchBuilder()
                .withChangeSet(changeSetBuilder()
                        .withUri('Products')
                        .withPart(Locale.ENGLISH, englishProduct)
                        .withPart(Locale.GERMAN, germanProduct))
                .build()

        when:
        facade.handleRequest batchODataPostRequest(content)

        then:
        def request = getInboundRequest()
        with(request) {
            getStatus() == IntegrationRequestStatus.SUCCESS
            getPayload() == null
            getErrors().isEmpty()
        }
    }

    @Test
    def "inbound request contains multiple batch changesets and errors when success and error retentions are enabled"() {
        given:
        logSuccessRequestPayload(true)
        logErrorPayload(true)

        and:
        def content = batchBuilder()
                .withChangeSet(changeSetBuilder()
                        .withUri("Products")
                        .withPart(Locale.GERMAN, json().withCode("Prod-1").withName("ein gutes Produkt"))
                        .withPart(Locale.ENGLISH, json().withCode("Prod-1").withName("invalid product").withCatalog(null)))
                .withChangeSet(changeSetBuilder()
                        .withUri("Products")
                        .withPart(Locale.ENGLISH, json().withCode("Prod@2").withName("a product")))
                .build()

        and:
        def request = batchODataPostRequest(content)
        request.getRequestHeaders().put SAP_PASSPORT_HEADER_NAME, [SAP_PASSPORT]

        when:
        facade.handleRequest request

        then:
        def requests = requestPersistenceContext.searchAllInboundRequest()
        requests.size() == 2

        and: 'error inbound request'
        def errorRequest = requests.find { it.status == IntegrationRequestStatus.ERROR }
        with(errorRequest) {
            errors
            def payload = getMediaContent getPayload()
            with(payload) {
                contains 'Prod-1'
                contains 'invalid product'
                contains 'ein gutes Produkt'
                !contains('Prod@2')
                !contains('a product')
            }
            user.uid == USER_ID
            sapPassport == SAP_PASSPORT
        }

        and: 'success inbound request'
        def successRequest = requests.find { it.status == IntegrationRequestStatus.SUCCESS }
        with(successRequest) {
            integrationKey == "$VERSION|$CATALOG|Prod@2"
            !errors
            def payload = getMediaContent getPayload()
            with(payload) {
                contains 'Prod@2'
                contains 'a product'
                !contains('Prod-1')
                !contains('ein gutes Produkt')
                !contains('invalid product')
            }
            user.uid == USER_ID
            sapPassport == SAP_PASSPORT
        }
    }

    @Test
    def "inbound request does not contain multiple batch changesets and errors when success and error retentions are disabled"() {
        given:
        logSuccessRequestPayload(false)
        logErrorPayload(false)

        and:
        def content = batchBuilder()
                .withChangeSet(changeSetBuilder()
                        .withUri("Products")
                        .withPart(Locale.GERMAN, json().withCode("Prod-1").withName("ein gutes Produkt"))
                        .withPart(Locale.ENGLISH, json().withCode("Prod-1").withName("invalid product").withCatalog(null)))
                .withChangeSet(changeSetBuilder()
                        .withUri("Products")
                        .withPart(Locale.ENGLISH, json().withCode("Prod-2").withName("a product")))
                .build()

        when:
        facade.handleRequest batchODataPostRequest(content)

        then:
        def requests = requestPersistenceContext.searchAllInboundRequest()
        requests.size() == 2

        and: 'error inbound request'
        def errorRequest = requests.find { it.status == IntegrationRequestStatus.ERROR }
        with(errorRequest) {
            !getErrors().isEmpty()
            getPayload() == null
            getUser().uid == USER_ID
            getSapPassport() == null
        }

        and: 'success inbound request'
        def successRequest = requests.find { it.status == IntegrationRequestStatus.SUCCESS }
        with(successRequest) {
            getIntegrationKey() == "$VERSION|$CATALOG|Prod-2"
            getErrors().isEmpty()
            getPayload() == null
            getUser().uid == USER_ID
            getSapPassport() == null
        }
    }

    @Test
    def "inbound request logs errors when number of batches exceeds the changeset limit"() {
        given:
        configuration.setBatchLimit(4)

        and:
        def content = batchBuilder()
                .withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, json().withCode(PRODUCT)))
                .withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, json().withCode(PRODUCT)))
                .withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, json().withCode(PRODUCT)))
                .withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, json().withCode(PRODUCT)))
                .withChangeSet(changeSetBuilder().withUri("Products").withPart(Locale.ENGLISH, json().withCode(PRODUCT)))
                .build()

        when:
        facade.handleRequest batchODataPostRequest(content)

        then:
        def requests = requestPersistenceContext.searchAllInboundRequest()
        requests.size() == 1

        and:
        def request = requests.find { it.status == IntegrationRequestStatus.ERROR }
        with(request) {
            !getErrors().isEmpty()
            getPayload() == null
            getUser().uid == USER_ID
            getSapPassport() == null
        }
    }

    @Test
    def "inbound request has the correct http method and response status when PATCH request is made"() {
        given: 'a product exists'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Product; code[unique = true]; name    ; catalogVersion(version, catalog(id))',
                "                     ; abc                ; original; $VERSION:$CATALOG"
        )

        when:
        facade.handleRequest patch("$VERSION|$CATALOG|abc", json().withCode('abc').withName('new'))

        then:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            status == IntegrationRequestStatus.SUCCESS
            httpMethod == HttpMethod.PATCH
        }
    }

    @Test
    def "inbound request has the correct http method and response status when DELETE request is made"() {
        given: 'a product exists'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Product; code[unique = true]; name    ; catalogVersion(version, catalog(id))',
                "                     ; abc                ; original; $VERSION:$CATALOG"
        )

        when:
        facade.handleRequest delete("$VERSION|$CATALOG|abc")

        then:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            status == IntegrationRequestStatus.SUCCESS
            httpMethod == HttpMethod.DELETE
        }
    }

    @Test
    def "inbound request has the correct http method and response status when POST request is made"() {
        given: 'a product exists'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Product; code[unique = true]; name    ; catalogVersion(version, catalog(id))',
                "                     ; abc                ; original; $VERSION:$CATALOG"
        )

        when:
        facade.handleRequest post(json().withCode('abc'))

        then:
        def inboundRequest = getInboundRequest()
        with(inboundRequest) {
            status == IntegrationRequestStatus.SUCCESS
            httpMethod == HttpMethod.POST
        }
    }

    def getInboundRequest() {
        def inboundRequests = requestPersistenceContext.searchAllInboundRequest()
        assert inboundRequests.size() == 1
        inboundRequests.first()
    }

    def batchODataPostRequest(String content) {
        setUserInSession()
        def request = ODataFacadeTestUtils.batchODataPostRequest(SERVICE_NAME, content)
        ODataFacadeTestUtils.createContext request
    }

    def getMediaContent(final IntegrationApiMediaModel payload) {
        requestPersistenceContext.getMediaContentAsString(payload)
    }

    void logSuccessRequestPayload(boolean logPayload) {
        configurationService.getConfiguration().setProperty("inboundservices.monitoring.success.payload.retention", String.valueOf(logPayload))
    }

    void logErrorPayload(boolean logPayload) {
        configurationService.getConfiguration().setProperty("inboundservices.monitoring.error.payload.retention", String.valueOf(logPayload))
    }

    ODataContext patch(String key, ODataJsonProductBuilder body) {
        setUserInSession()
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPatchRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(ENTITY_SET)
                        .withEntityKeys(key))
                .withContentType(APPLICATION_JSON)
                .withBody(body.build())
    }

    ODataContext delete(String key) {
        setUserInSession()
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataDeleteRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(ENTITY_SET)
                        .withEntityKeys(key))
                .withAccepts(APPLICATION_JSON)
    }

    ODataContext post(ODataJsonProductBuilder body, String contentType = APPLICATION_JSON) {
        post body.build(), contentType
    }

    ODataContext post(String body, String contentType = APPLICATION_JSON) {
        setUserInSession()
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPostRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(ENTITY_SET))
                .withContentType(contentType)
                .withBody(body)
    }

    UserModel testUser() {
        UserModel userModel = new UserModel()
        userModel.setUid(USER_ID)
        userModel
    }

    def setUserInSession() {
        UserModel testUser = flexibleSearchService.getModelByExample(testUser())
        userService.setCurrentUser(testUser)
    }

    ODataJsonProductBuilder json() {
        ODataJsonProductBuilder.product().withCatalog(CATALOG).withCatalogVersion(VERSION)
    }

    ODataAtomProductBuilder xml() {
        ODataAtomProductBuilder.product().withCatalog(CATALOG).withCatalogVersion(VERSION)
    }
}