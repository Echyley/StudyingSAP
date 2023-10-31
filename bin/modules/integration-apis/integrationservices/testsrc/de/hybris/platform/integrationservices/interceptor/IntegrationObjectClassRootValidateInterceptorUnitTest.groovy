/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.AtomicTypeModel
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectClassRootValidateInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CLASS_CODE = 'classCode'
    private static final def IO_CODE = 'AnyIO'
    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)

    def typeService = Stub(TypeService)
    def interceptor = new IntegrationObjectClassRootValidateInterceptor(typeService)

    @Test
    void 'no exception is thrown when root is not an atomic type'() {
        given:
        def classType = TestPojo
        typeService.getAtomicTypeForJavaClass(classType) >> { throw Stub(UnknownIdentifierException) }
        def classModel = rootIOClass(classType)

        when:
        interceptor.onValidate classModel, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    void 'no exception is thrown when non root is an atomic type'() {
        given:
        def classType = Class
        typeService.getAtomicTypeForJavaClass(classType) >> { throw Stub(UnknownIdentifierException) }
        def classModel = ioClass(classType)

        when:
        interceptor.onValidate classModel, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    void 'exception is thrown when root is an atomic type'() {
        given:
        def atomicClassType = Class
        typeService.getAtomicTypeForJavaClass(atomicClassType) >> Stub(AtomicTypeModel)
        def classModel = rootIOClass(atomicClassType)

        when:
        interceptor.onValidate classModel, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains classModel.code
        e.message.contains classModel.integrationObject.code
        e.message.contains("atomic type [$atomicClassType.name] cannot have root set to true")
    }

    def rootIOClass(final Class type) {
        Stub(IntegrationObjectClassModel) {
            getRoot() >> true
            getCode() >> IO_CLASS_CODE
            getType() >> type
            getIntegrationObject() >> IO_MODEL
        }
    }

    def ioClass(final Class type) {
        Stub(IntegrationObjectClassModel) {
            getRoot() >> false
            getCode() >> IO_CLASS_CODE
            getType() >> type
            getIntegrationObject() >> IO_MODEL
        }
    }
}
