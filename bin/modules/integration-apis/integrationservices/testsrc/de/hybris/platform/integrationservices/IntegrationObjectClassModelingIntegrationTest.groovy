/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import org.junit.Test
import spock.lang.PendingFeature

@IntegrationTest
class IntegrationObjectClassModelingIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = "IntegrationObjectClassModeling"
    private static final String IO = "${TEST_NAME}_IO"

    def cleanupSpec() {
        IntegrationTestUtil.remove IntegrationObjectModel, { it -> it.code == IO }
    }

    @Test
    void 'no exception is thrown when IntegrationObject with IntegrationObjectClass is modeled correctly'() {
        when:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $IO",
                'INSERT_UPDATE IntegrationObjectClass; integrationObject(code)[unique = true]; code[unique = true]; type(code)                                     ; root[default = false];',
                "                                    ; $IO                                   ; OrderModel         ; de.hybris.platform.core.model.order.OrderModel ; true                 ;",
                '$integrationClass = integrationObjectClass(integrationObject(code), code)',
                '$returnClass = returnIntegrationObjectClass(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectClassAttribute; $integrationClass[unique = true]; attributeName[unique = true] ; readMethod    ; $returnClass',
                "                                             ; $IO:OrderModel                  ; statusInfo                   ; getStatusInfo ; ",
                "                                             ; $IO:OrderModel                  ; code                         ; getCode       ; ")

        then:
        noExceptionThrown()
    }

    @Test
    @PendingFeature(reason = 'https://jira.tools.sap/browse/CXMC-763')
    void 'an exception is thrown when making a collection attribute unique'() {
        when:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $IO",
                'INSERT_UPDATE IntegrationObjectClass; integrationObject(code)[unique = true]; code[unique = true]; type(code)                                          ;root[default = false]',
                "                                    ; $IO                                   ; OrderEntry         ; de.hybris.platform.core.model.order.OrderEntryModel ;true                 ",
                '$integrationClass = integrationObjectClass(integrationObject(code), code)',
                '$returnClass = returnIntegrationObjectClass(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectClassAttribute; $integrationClass[unique = true]; attributeName[unique = true] ; unique[default = false]; readMethod',
                "                                             ; $IO:OrderEntry                  ; entryNumber                  ; true                   ;           ",
                "                                             ; $IO:OrderEntry                  ; entryGroupNumbers            ; true                   ;           ")
        then:
        def e = thrown InterceptorException
        with(e.message) {
            contains("$IO")
            contains("OrderEntry")
            contains("entryGroupNumbers")
        }
    }

    @Test
    void 'an exception is thrown when IntegrationObjectClass is modeled with a primitive'() {
        when:
        def primitiveTypeCode = "java.lang.Integer"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $IO                ",
                'INSERT_UPDATE IntegrationObjectClass; integrationObject(code)[unique = true]; code[unique = true]; type(code)        ; root[default = false]',
                "                                    ; $IO                                   ; Unit               ; $primitiveTypeCode;                      ")

        then:
        def e = thrown AssertionError
        e.message.contains("IntegrationObjectClasses cannot be a primitive type.")
    }

    @Test
    void 'an exception is thrown when IntegrationObject root is an atomic type'() {
        when:
        def atomicTypeCode = "de.hybris.platform.util.DiscountValue"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $IO                       ",
                'INSERT_UPDATE IntegrationObjectClass; integrationObject(code)[unique = true]; code[unique = true]; type(code)     ; root[default = false]',
                "                                    ; $IO                                   ; Unit               ; $atomicTypeCode; true                 ")

        then:
        def e = thrown AssertionError
        e.message.contains("atomic type [$atomicTypeCode] cannot have root set to true")
    }
}
