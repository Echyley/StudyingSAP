/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationObjectsContext
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectClassAttributeReadMethodValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CODE = 'AnyIO'
    private static final def DEFAULT_ATTRIBUTE_NAME = "attributeName"

    private static final def PRIMITIVE_TYPE_GETTER = "getInt"
    private static final def COMPOSED_TYPE_GETTER = "getComposedType"
    private static final def UNLOCALIZED_TYPE_GETTER = "getIndexedProperty"
    private static final def LOCALIZED_TYPE_GETTER = "getByLocale"
    private static final def MULTIPLE_PARAMETER_GETTER = "getByMultipleLocales"
    private static final def NOT_PUBLIC_METHOD = "somePrivateMethod"
    private static final def VOID_TYPE_METHOD = "doVoid"
    private static final def NON_EXISTENT_METHOD = "someNonExistentMethod"

    private static final def TEST_CLASS = TestPojo
    private static final def ORDER_MODEL = OrderModel
    private static final def IO_CONTEXT = IntegrationObjectsContext

    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
    private static final def NON_MATCHING_CLASS = new IntegrationObjectClassModel(type: IO_CONTEXT, integrationObject: IO_MODEL)
    private static final def POJO_TEST_CLASS = new IntegrationObjectClassModel(type: TEST_CLASS, integrationObject: IO_MODEL)
    private static final def MATCHING_CLASS = new IntegrationObjectClassModel(type: ORDER_MODEL, integrationObject: IO_MODEL)

    def interceptor = new IntegrationObjectClassAttributeReadMethodValidateInterceptor()

    @Test
    def 'Does not validate when read method is not provided'() {
        given:
        def ioClassAttribute = attribute()

        when:
        interceptor.onValidate ioClassAttribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    def 'no exception is thrown when #condition'() {
        given:
        def ioClassAttribute = attribute(["readMethod" : readMethod,
                                          "integrationObjectClass" : POJO_TEST_CLASS,
                                          "returnIntegrationObjectClass" : returnIntObjClass])

        when:
        interceptor.onValidate ioClassAttribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        readMethod            | returnIntObjClass | condition
        LOCALIZED_TYPE_GETTER | null              | 'read method has a single localized parameter'
        PRIMITIVE_TYPE_GETTER | null              | 'read method has no parameters and non-void return type'
        PRIMITIVE_TYPE_GETTER | null              | 'primitive read method and attribute existed, null return class'
        COMPOSED_TYPE_GETTER  | MATCHING_CLASS    | 'same composed read method and return class type, attribute existed'
    }

    @Test
    def 'exception is thrown when #condition'() {
        given:
        def attribute = attribute(["readMethod"                  : readMethod,
                                   "attributeName"               : DEFAULT_ATTRIBUTE_NAME,
                                   "integrationObjectClass"      : POJO_TEST_CLASS,
                                   "returnIntegrationObjectClass": returnIntObjClass])

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains("for class [$POJO_TEST_CLASS.type.name] of IntegrationObject [$POJO_TEST_CLASS.integrationObject.code] $errMsg")

        where:
        readMethod                | returnIntObjClass  | errMsg                                                 | condition
        NON_EXISTENT_METHOD       | null               | 'does not exist'                                       | 'read method does not exist'
        MULTIPLE_PARAMETER_GETTER | null               | 'has non-empty parameters'                             | 'read method has multiple parameters'
        UNLOCALIZED_TYPE_GETTER   | null               | 'has non-empty parameters'                             | 'read method has an unlocalized parameter'
        NOT_PUBLIC_METHOD         | null               | 'does not exist'                                       | 'read method is not public'
        VOID_TYPE_METHOD          | null               | 'is VOID'                                              | 'read method is void type'
        PRIMITIVE_TYPE_GETTER     | NON_MATCHING_CLASS | "does not match the returnIntegrationObjectClass type" | 'primitive read method and attribute existed, return class is not null'
        COMPOSED_TYPE_GETTER      | NON_MATCHING_CLASS | "does not match the returnIntegrationObjectClass type" | 'different composed type read method and return class'
    }

    def attribute(final Map<String, Object> params = [:]) {
        Stub(IntegrationObjectClassAttributeModel) {
            getAttributeName() >> params["attributeName"]
            getReadMethod() >> params["readMethod"]
            getIntegrationObjectClass() >> params["integrationObjectClass"]
            getReturnIntegrationObjectClass() >> params["returnIntegrationObjectClass"]
        }
    }
}
