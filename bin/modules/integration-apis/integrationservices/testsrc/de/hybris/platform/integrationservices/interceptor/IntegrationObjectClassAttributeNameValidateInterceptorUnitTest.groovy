/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderEntryModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationObjectsContext
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectClassAttributeNameValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CODE = 'AnyIO'
    private static final def PRIMITIVE_TYPE_ATTRIBUTE = "timestamp"
    private static final def ATTRIBUTE_NO_PUBLIC_GETTER = "code"
    private static final def COMPOSED_TYPE_ATTRIBUTE = "order"
    private static final def LIST_COMPOSED_TYPE_ATTRIBUTE = "entries"
    private static final def NO_GETTER_ATTRIBUTE = "eventScope"

    private static final def EVENT_CLASS = SubmitOrderEvent
    private static final def ORDER_MODEL = OrderModel
    private static final def IO_CONTEXT = IntegrationObjectsContext
    private static final def ORDER_ENTRY_MODEL = OrderEntryModel

    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
    private static final def CLASS = new IntegrationObjectClassModel(type: EVENT_CLASS, integrationObject: IO_MODEL)
    private static final def CLASS_2 = new IntegrationObjectClassModel(type: IO_CONTEXT, integrationObject: IO_MODEL)
    private static final def RETURN_CLASS_MATCH_TYPE = new IntegrationObjectClassModel(type: ORDER_MODEL, integrationObject: IO_MODEL)
    private static final def CLASS_3 = new IntegrationObjectClassModel(type: ORDER_MODEL, integrationObject: IO_MODEL)
    private static final def RETURN_CLASS_MATCH_TYPE2 = new IntegrationObjectClassModel(type: ORDER_ENTRY_MODEL, integrationObject: IO_MODEL)

    def interceptor = new IntegrationObjectClassAttributeNameValidateInterceptor()

    @Test
    void 'does not throw an exception when read method is provided'() {
        given:
        def ioClassAttribute = attribute(["readMethod": "getAttr"])

        when:
        interceptor.onValidate ioClassAttribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    void 'no exception is thrown when #condition'() {
        given:
        def ioClassAttribute = attribute(["readMethod"                  : null,
                                          "attributeName"               : attrName,
                                          "integrationObjectClass"      : intObjClass,
                                          "returnIntegrationObjectClass": returnIntObjClass])

        when:
        interceptor.onValidate ioClassAttribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        attrName                     | intObjClass | returnIntObjClass        | condition
        COMPOSED_TYPE_ATTRIBUTE      | CLASS       | RETURN_CLASS_MATCH_TYPE  | 'same composed attribute and return class type, null read method'
        LIST_COMPOSED_TYPE_ATTRIBUTE | CLASS_3     | RETURN_CLASS_MATCH_TYPE2 | 'inheritance types for return class and attribute, null read method'
    }

    @Test
    void "no exception is thrown for an attribute of a Java primitive type: #attrName"() {
        given:
        def ioClass = new IntegrationObjectClassModel(type: TestPojo, integrationObject: IO_MODEL)
        def ioAttribute = attribute([attributeName: attrName, integrationObjectClass: ioClass])

        when:
        interceptor.onValidate ioAttribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        attrName << ['boolean', 'byte', 'char', 'double', 'float', 'int', 'long', 'short']
    }

    @Test
    void 'exception is thrown when #condition'() {
        given:
        def attribute = attribute(["readMethod"                  : null,
                                   "attributeName"               : attrName,
                                   "returnIntegrationObjectClass": returnIntObjClass])

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains("for class [$CLASS.type.name] of IntegrationObject [$CLASS.integrationObject.code] $errMsg")

        where:
        attrName                   | returnIntObjClass | errMsg                                                 | condition
        NO_GETTER_ATTRIBUTE        | null              | 'does not exist'                                       | 'getter method for attribute does not exist'
        ATTRIBUTE_NO_PUBLIC_GETTER | null              | 'does not exist'                                       | 'default getter method is not public'
        PRIMITIVE_TYPE_ATTRIBUTE   | CLASS_2           | "does not match the returnIntegrationObjectClass type" | 'primitive attribute existed, return class is not null'
        COMPOSED_TYPE_ATTRIBUTE    | CLASS_2           | "does not match the returnIntegrationObjectClass type" | 'different composed type attribute and return class, read method is null'
    }

    def attribute(final Map<String, Object> params = [:]) {
        Stub(IntegrationObjectClassAttributeModel) {
            getAttributeName() >> params["attributeName"]
            getReadMethod() >> params["readMethod"]
            getIntegrationObjectClass() >> (params["integrationObjectClass"] ?: CLASS)
            getReturnIntegrationObjectClass() >> params["returnIntegrationObjectClass"]
        }
    }
}
