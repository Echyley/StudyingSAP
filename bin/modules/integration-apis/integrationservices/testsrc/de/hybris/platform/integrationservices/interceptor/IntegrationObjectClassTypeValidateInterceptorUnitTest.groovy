/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectClassTypeValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CLASS_CODE = 'classCode'
    private static final def IO_CODE = 'AnyIO'
    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)

    def interceptor = new IntegrationObjectClassTypeValidateInterceptor()

    @Test
    void 'no exception is thrown when type is not a primitive type'() {
        given:
        def classType = TestPojo
        def classModel = ioClass(classType)

        when:
        interceptor.onValidate classModel, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    void 'exception is thrown when type is primitive type #primitiveType'() {
        given:
        def classModel = ioClass(primitiveType)

        when:
        interceptor.onValidate classModel, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains("[$IO_CLASS_CODE] for IntegrationObject [$IO_CODE]")
        e.message.contains(primitiveType.toString())
        e.message.contains("cannot be a primitive type")

        where:
        primitiveType << [BigDecimal, BigInteger, Boolean, Byte, Character, Date, Double, Float, Integer, Long, Short, String, Calendar, Object, Class]
    }

    def ioClass(final Class type) {
        Stub(IntegrationObjectClassModel) {
            getCode() >> IO_CLASS_CODE
            getType() >> type
            getIntegrationObject() >> IO_MODEL
        }
    }
}
