/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel
import de.hybris.platform.catalog.model.classification.ClassificationClassModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataEnumTypeIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "ODataEnumType"
    private static final String IO = "${TEST_NAME}_ClassAttributeAssignmentIO"
    private static final def TEST_SYSTEM = "${TEST_NAME}_ERP_CLASSIFICATION_3000"
    private static final def TEST_SYSTEM_VERSION = "ERP_IMPORT"
    private static final def TEST_CLASSIFICATION_SYSTEM_VERSION = "$TEST_SYSTEM:$TEST_SYSTEM_VERSION"

    @Resource
    private FlexibleSearchService flexibleSearchService
    @Resource(name = "oDataWebMonitoringFacade")
    private ODataFacade facade

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE IntegrationObject; code[unique = true]",
                "; $IO",
                "INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
                "; $IO ; ClassAttributeAssignment        ; ClassAttributeAssignment",
                "; $IO ; ClassificationClass             ; ClassificationClass",
                "; $IO ; CatalogVersion                  ; CatalogVersion",
                "; $IO ; Catalog                         ; Catalog",
                "; $IO ; ClassificationSystem            ; ClassificationSystem",
                "; $IO ; ClassificationSystemVersion     ; ClassificationSystemVersion",
                "; $IO ; ClassificationAttributeTypeEnum ; ClassificationAttributeTypeEnum",
                "; $IO ; ClassificationAttributeUnit     ; ClassificationAttributeUnit",
                "; $IO ; ClassificationAttribute         ; ClassificationAttribute",

                '$item = integrationObjectItem(integrationObject(code), code)',
                '$descriptor = attributeDescriptor(enclosingType(code), qualifier)',
                '$refType=returnIntegrationObjectItem(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]; attributeName[unique = true]; $descriptor; $refType; unique[default = false]',
                "; $IO:ClassAttributeAssignment        ; classificationClass     ; ClassAttributeAssignment:classificationClass     ; $IO:ClassificationClass                                  ; true",
                "; $IO:ClassAttributeAssignment        ; classificationAttribute ; ClassAttributeAssignment:classificationAttribute ; $IO:ClassificationAttribute                              ; true",
                "; $IO:ClassAttributeAssignment        ; attributeType           ; ClassAttributeAssignment:attributeType           ; $IO:ClassificationAttributeTypeEnum                      ; true",
                "; $IO:ClassAttributeAssignment        ; multiValued             ; ClassAttributeAssignment:multiValued             ;                                                          ;",
                "; $IO:ClassAttributeAssignment        ; unit                    ; ClassAttributeAssignment:unit                    ; $IO:ClassificationAttributeUnit                          ; true",
                "; $IO:ClassAttributeAssignment        ; formatDefinition        ; ClassAttributeAssignment:formatDefinition        ;                                                          ;",
                "; $IO:ClassAttributeAssignment        ; range                   ; ClassAttributeAssignment:range                   ;                                                          ;",
                "; $IO:ClassificationClass             ; code                    ; ClassificationClass:code                         ;                                                          ; true",
                "; $IO:ClassificationClass             ; catalogVersion          ; ClassificationClass:catalogVersion               ; $IO:ClassificationSystemVersion                          ; true",
                "; $IO:CatalogVersion                  ; catalog                 ; CatalogVersion:catalog                           ; $IO:Catalog                                              ; true",
                "; $IO:CatalogVersion                  ; version                 ; CatalogVersion:version                           ;                                                          ; true",
                "; $IO:CatalogVersion                  ; active                  ; CatalogVersion:active                            ;                                                          ;",
                "; $IO:Catalog                         ; id                      ; Catalog:id                                       ;                                                          ; true",
                "; $IO:ClassificationAttribute         ; code                    ; ClassificationAttribute:code                     ;                                                          ; true",
                "; $IO:ClassificationAttribute         ; systemVersion           ; ClassificationAttribute:systemVersion            ; $IO:ClassificationSystemVersion                          ; true",
                "; $IO:ClassificationSystemVersion     ; version                 ; ClassificationSystemVersion:version              ;                                                          ; true",
                "; $IO:ClassificationSystemVersion     ; catalog                 ; ClassificationSystemVersion:catalog              ; $IO:Catalog                                              ; true",
                "; $IO:ClassificationSystem            ; id                      ; ClassificationSystem:id                          ;                                                          ; true",
                "; $IO:ClassificationAttributeTypeEnum ; code                    ; ClassificationAttributeTypeEnum:code             ;                                                          ; true",
                "; $IO:ClassificationAttributeUnit     ; code                    ; ClassificationAttributeUnit:code                 ;                                                          ; true",
                "; $IO:ClassificationAttributeUnit     ; unitType                ; ClassificationAttributeUnit:unitType             ;                                                          ; true",
                "; $IO:ClassificationAttributeUnit     ; systemVersion           ; ClassificationAttributeUnit:systemVersion        ; $IO:ClassificationSystemVersion                          ; true"
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll ClassAttributeAssignmentModel
        IntegrationTestUtil.removeAll ClassificationSystemModel
        IntegrationTestUtil.removeAll ClassificationSystemVersionModel
        IntegrationTestUtil.removeAll ClassificationClassModel
        IntegrationTestUtil.removeAll ClassificationAttributeModel
        IntegrationTestUtil.removeAll ClassificationAttributeValueModel
        IntegrationTestUtil.removeAll ClassificationAttributeUnitModel
    }

    @Test
    def "can successfully create a new ClassAttributeTypeEnum"() {
        given:
        def classificationClassCode = "${TEST_NAME}_ClassCode"
        def classificationAttributeCode = "${TEST_NAME}_AttrCode"
        def unitTypeVal = "SAP-POWER"
        def unitCode = "${TEST_NAME}_Unit"
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE ClassificationSystem; id[unique = true]; name[lang = en];",
                "                                  ; $TEST_SYSTEM      ; $TEST_SYSTEM",
                "INSERT_UPDATE ClassificationSystemVersion; catalog(id)[unique = true]; version[unique = true]; active;",
                "                                         ; $TEST_SYSTEM               ; $TEST_SYSTEM_VERSION    ; true",
                "INSERT_UPDATE ClassificationClass; code[unique = true]     ; name[lang = en]; catalogVersion(catalog(id), version)",
                "                                 ; $classificationClassCode; Dragon Car     ; $TEST_CLASSIFICATION_SYSTEM_VERSION",
                "INSERT_UPDATE ClassificationAttribute; code[unique = true]         ; name[lang = en]; systemVersion(catalog(id), version)",
                "                                     ; $classificationAttributeCode; Dark Blue      ; $TEST_CLASSIFICATION_SYSTEM_VERSION",
                "INSERT_UPDATE ClassificationAttributeValue; code[unique = true]         ; name[lang = en]; systemVersion(catalog(id), version)",
                "                                          ; $classificationAttributeCode; Dark Blue      ; $TEST_CLASSIFICATION_SYSTEM_VERSION",
                "INSERT_UPDATE ClassificationAttributeUnit; code[unique = true]; unitType[unique = true]; systemVersion(catalog(id), version); symbol",
                "                                         ; $unitCode          ; $unitTypeVal           ; $TEST_CLASSIFICATION_SYSTEM_VERSION   ; aSymbol",
        )
        final String content = json()
                .withField("range", false)
                .withField("multiValued", false)
                .withField("classificationClass", json()
                        .withField("code", classificationClassCode)
                        .withField("catalogVersion", testSystemVersionJson()))
                .withField("classificationAttribute", json()
                        .withField("code", classificationAttributeCode)
                        .withField("systemVersion", testSystemVersionJson()))
                .withField("attributeType", json()
                        .withField("code", "string"))
                .withField("unit", json()
                        .withField("code", unitCode)
                        .withField("unitType", unitTypeVal)
                        .withField("systemVersion", testSystemVersionJson()))
                .build()

        when:
        def request = ODataFacadeTestUtils.oDataPostRequest(IO, "ClassAttributeAssignments", content, APPLICATION_JSON_VALUE)
        def postResponse = facade.handleRequest(createContext(request))
        postResponse.getStatus() == HttpStatusCodes.CREATED

        then:
        def classAssignment = IntegrationTestUtil.findAny(ClassAttributeAssignmentModel, { it.attributeType == ClassificationAttributeTypeEnum.STRING }).orElse(null)
        with(classAssignment) {
            classificationClass.code == classificationClassCode
            attributeType == ClassificationAttributeTypeEnum.STRING
            with(classificationAttribute) {
                code == classificationAttributeCode
            }
            with(unit) {
                code == unitCode
                unitType == unitTypeVal
            }
        }
    }

    private def testSystemVersionJson() {
        json()
                .withField("version", TEST_SYSTEM_VERSION)
                .withField("catalog", json().withId(TEST_SYSTEM))
    }
}
