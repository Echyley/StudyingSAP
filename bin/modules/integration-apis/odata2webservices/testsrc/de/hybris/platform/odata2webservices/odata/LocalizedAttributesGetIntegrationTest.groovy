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

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.inboundservices.util.LocalizationRule
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.product.ProductService
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Rule
import org.junit.Test

import javax.annotation.Resource

import static org.apache.commons.lang.StringUtils.EMPTY
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class LocalizedAttributesGetIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "LocalizedAttributesGet"
    private static final String SERVICE_NAME = "${TEST_NAME}_MyProduct"
    private static final String PRODUCTS = "Products"
    private static final String PRODUCT_CODE_ENCODED = "${TEST_NAME}_testProduct"
    private static final String PRODUCT_INTEGRATION_KEY = "Staged|${TEST_NAME}_Default|" + PRODUCT_CODE_ENCODED
    private static final String LANGUAGE = "language"
    private static final String SOME_RESULTS_PATH = "\$['d']['results'][?]"
    private static final String ALL_RESULTS_PATH = "\$['d']['results'][*]"
    private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes"

    @Resource(name = "defaultODataFacade")
    ODataFacade facade
    @Resource
    ModelService modelService
    @Resource
    ODataContextGenerator oDataContextGenerator
    @Resource
    ProductService productService
    @Rule
    LocalizationRule localizationRule = LocalizationRule.initialize()

    Filter frenchProduct = Filter.filter(
            Criteria.where("name").eq("fr name for testProduct")
                    .and("description").eq("fr description for testProduct")
                    .and(LANGUAGE).eq("fr")
    )
    Filter englishProduct = Filter.filter(
            Criteria.where("name").eq("en name for testProduct")
                    .and("description").eq("en description for testProduct")
                    .and(LANGUAGE).eq("en")
    )

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE IntegrationObject; code[unique = true];",
                "; ${SERVICE_NAME}",
                "INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique=true]; code[unique = true]; type(code)",
                "; ${SERVICE_NAME} ; Product            ; Product",
                "; ${SERVICE_NAME} ; Catalog            ; Catalog",
                "; ${SERVICE_NAME} ; CatalogVersion     ; CatalogVersion",
                "; ${SERVICE_NAME} ; Category         ; Category",
                "INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
                "; ${SERVICE_NAME}:Product        ; code                   ; Product:code                            ;",
                "; ${SERVICE_NAME}:Product        ; name                   ; Product:name                            ;",
                "; ${SERVICE_NAME}:Product        ; description            ; Product:description                     ;",
                "; ${SERVICE_NAME}:Product        ; catalogVersion         ; Product:catalogVersion                  ; ${SERVICE_NAME}:CatalogVersion",
                "; ${SERVICE_NAME}:Catalog        ; id                     ; Catalog:id                              ;",
                "; ${SERVICE_NAME}:Category          ; code                    ; Category:code           ;",
                "; ${SERVICE_NAME}:CatalogVersion ; catalog                ; CatalogVersion:catalog                  ; ${SERVICE_NAME}:Catalog",
                "; ${SERVICE_NAME}:CatalogVersion ; version                ; CatalogVersion:version                  ;",
                "; ${SERVICE_NAME}:CatalogVersion ; categorySystemName     ; CatalogVersion:categorySystemName       ;",
                "; ${SERVICE_NAME}:Product        ; supercategories         ; Product:supercategories                 ; ${SERVICE_NAME}:Category",
                "INSERT_UPDATE Catalog;id[unique=true];name[lang=en];defaultCatalog;",
                ";${TEST_NAME}_Default;Default;true",
                "INSERT_UPDATE CatalogVersion; catalog(id)[unique=true]; version[unique=true];active;",
                ";${TEST_NAME}_Default;Staged;true",
                "INSERT_UPDATE Language;isocode[unique=true];name[lang=en]",
                ";fr;French",
                "INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]; description[lang = en]; description[lang = fr]",
                "; ${PRODUCT_CODE_ENCODED} ; ${TEST_NAME}_Default:Staged ; en name for testProduct ; fr name for testProduct ; en description for testProduct ; fr description for testProduct",
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll CategoryModel
        IntegrationTestUtil.remove ProductModel, {it.code == PRODUCT_CODE_ENCODED}
        IntegrationTestUtil.removeSafely LanguageModel, { it.isocode == 'fr' }
    }

    @Test
    def "all values for localizedAttributes returned when requesting item's localizedAttributes navigation segment"() {
        given:
        def context = oDataContext(PRODUCT_INTEGRATION_KEY, LOCALIZED_ATTRIBUTES)

        when:
        def oDataResponse = facade.handleRequest(context)

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.OK)
                .jsonBody()
                .pathHasSize(ALL_RESULTS_PATH, 2)
                .pathContainsMatchingElement(SOME_RESULTS_PATH, frenchProduct)
                .pathContainsMatchingElement(SOME_RESULTS_PATH, englishProduct)
    }

    @Test
    def "localized properties are returned in the platform's default language"() {
        given:
        localizationRule.setSessionLanguage("fr")

        and:
        def context = oDataContext(PRODUCT_INTEGRATION_KEY)

        when:
        def oDataResponse = facade.handleRequest(context)

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.OK)
                .jsonBody()
                .hasPathWithValue("d.name", "fr name for testProduct")
                .hasPathWithValue("d.description", "fr description for testProduct")
    }

    @Test
    def "all languages are provided in the response when \$expand=localizedAttributes"() {
        given:
        def context = oDataContext(PRODUCT_INTEGRATION_KEY, ['\$expand': 'localizedAttributes'])

        when:
        def oDataResponse = facade.handleRequest(context)

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.OK)
                .jsonBody()
                .pathContainsMatchingElement("\$['d']['localizedAttributes']['results'][*]", frenchProduct)
                .pathContainsMatchingElement("\$['d']['localizedAttributes']['results'][*]", englishProduct)
    }

    @Test
    def "localized attribute with null value is not returned in response body"() {
        given:
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]; description[lang = en]; description[lang = fr]",
                                     "; null_fields_prod ; ${TEST_NAME}_Default:Staged ; en name for testProduct ; ; ; fr description for testProduct",
        )

        and:
        def context = oDataContext("Staged|"+TEST_NAME+"_Default|null_fields_prod", LOCALIZED_ATTRIBUTES)

        when:
        def oDataResponse = facade.handleRequest(context)

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.OK)
                .jsonBody()
                .pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
                        .where('name').exists(false)
                        .and('description').eq('fr description for testProduct')))
                .pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
                        .where('name').eq('en name for testProduct')
                        .and("description").exists(false)))

    }

    @Test
    def "localized attribute with empty string will be returned in response body as empty"() {
        given:
        def product = productService.getProductForCode(PRODUCT_CODE_ENCODED)
        // can't set empty string with ImpEx so doing it programmatically
        product.setDescription(EMPTY, Locale.ENGLISH)
        modelService.save(product)

        and:
        def context = oDataContext(PRODUCT_INTEGRATION_KEY, LOCALIZED_ATTRIBUTES)

        when:
        def oDataResponse = facade.handleRequest(context)

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.OK)
                .jsonBody()
                .pathContainsMatchingElement(SOME_RESULTS_PATH, Filter.filter(Criteria
                        .where('name').eq('en name for testProduct')
                        .and('description').eq(EMPTY)))
                .pathContainsMatchingElement(SOME_RESULTS_PATH, frenchProduct)
    }

    @Test
    def "includes deferred localizedAttributes for item with localizedAttributes"() {
        given:
        IntegrationTestUtil.importImpEx(
                "\$catalogVersion = ${TEST_NAME}_Default:Staged",
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                "; ${SERVICE_NAME}:Product            ; name            ; Product:name                    ;",
                'INSERT_UPDATE Product; code[unique = true]; name; catalogVersion(catalog(id), version)',
                '; pr-1 ; enProductName  ; $catalogVersion',
        )
        def context = oDataContext(null)

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = JsonObject.createFrom response.getEntityAsStream()
        json.getCollection("d.results").size() == 2
        json.exists "d.results[0].localizedAttributes.__deferred"
        json.exists "d.results[1].localizedAttributes.__deferred"
    }

    @Test
    def "Expands nested localizedAttributes"() {
        given:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                "; ${SERVICE_NAME}:Product        ; name        ; Product:name            ;",
                "; ${SERVICE_NAME}:Category        ; name        ; Category:name                   ;",
                "\$catalogVersion = ${TEST_NAME}_Default:Staged",
                'INSERT_UPDATE Category; code[unique = true]; name[lang=en]; catalogVersion(catalog(id), version)',
                '; test ; enCategoryName ; $catalogVersion',
                'INSERT_UPDATE Product; code[unique = true]; name[lang=en]; name[lang=fr]; catalogVersion(catalog(id), version); supercategories(code)',
                "; ${PRODUCT_CODE_ENCODED}" + '; enProductName ; frProductName ; $catalogVersion; test ',
        )
        def context = oDataContext(null, ['$expand': 'localizedAttributes,supercategories/localizedAttributes'])

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        response.getStatus() == HttpStatusCodes.OK
        def json = JsonObject.createFrom response.getEntityAsStream()
        json.getCollection("d.results").size() == 1
        json.getCollection("d.results[0].localizedAttributes.results").size() == 2
        json.getCollection("d.results[0].supercategories.results").size() == 1
        json.getCollection("d.results[0].supercategories.results[0].localizedAttributes.results").size() == 1
        json.getString("d.results[0].supercategories.results[0].localizedAttributes.results[0].language") == "en"
        json.getString("d.results[0].supercategories.results[0].localizedAttributes.results[0].name") == "enCategoryName"
    }

    ODataContext oDataContext(final String integrationKey) {
        oDataContext(integrationKey, "", [:], null)
    }

    ODataContext oDataContext(final String integrationKey, final String navigationSegment) {
        oDataContext(integrationKey, navigationSegment, [:], null)
    }

    ODataContext oDataContext(final String integrationKey, Map params) {
        oDataContext(integrationKey, "", params, null)
    }

    ODataContext oDataContext(String integrationKey, String navigationSegment, Map params, Locale locale) {
        def request = ODataRequestBuilder.oDataGetRequest()
                .withAccepts(APPLICATION_JSON_VALUE)
                .withAcceptLanguage(locale)
                .withParameters(params)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(PRODUCTS)
                        .withEntityKeys(integrationKey)
                        .withNavigationSegment(navigationSegment))
                .build()

        oDataContextGenerator.generate request
    }
}
