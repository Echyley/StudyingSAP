/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.impl.PersistenceFailedException
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectClassAndItemNotAllowedInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectClassAttributeNameValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectClassAttributeReadMethodValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectClassRootValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectClassTypeValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectItemAndClassNotAllowedInterceptor
import de.hybris.platform.integrationservices.interceptor.IntegrationObjectTypeMixValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.RootClassExistsValidateInterceptor
import de.hybris.platform.integrationservices.interceptor.SingleRootClassValidateInterceptor
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.validator.SingleRootItemValidator
import de.hybris.platform.jalo.JaloInvalidParameterException
import de.hybris.platform.servicelayer.exceptions.ModelSavingException
import de.hybris.platform.servicelayer.exceptions.SystemException
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test

@UnitTest
class PersistenceFailedExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    private static final def CAUSE_MESSAGE = 'nested exception message'
    private static final def ENTITY_TYPE_NAME = 'EntityTypeName'
    private static final def INTEGRATION_KEY = 'key|value'
    private static final String INVALID_ATTRIBUTE_VALUE = 'invalid_attribute_value'
    private static final String INVALID_ATTRIBUTE_NAME = 'invalid_attribute_name'
    private static final String INVALID_READ_METHOD = 'invalid_read_method'
    private static final String INVALID_ROOT_POJO = 'invalid_root_pojo'
    private static final String INVALID_POJO_ITEM_MODELING = 'invalid_pojo_item_modeling'
    private static final String INTERNAL_ERROR = 'internal_error'
    private static final String INVALID_CLASS_TYPE = 'invalid_class_type'

    def populator = new PersistenceFailedExceptionContextPopulator()

    @Test
    void "response has BAD_REQUEST status with cause exception message when cause is #interceptor.class.simpleName"() {
        given:
        def cause = new ModelSavingException('testMessage', interceptor)
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(context(), cause))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            errorCode == INVALID_ATTRIBUTE_VALUE
            message == "An error occurred while attempting to save entries for entityType: $ENTITY_TYPE_NAME, with error message $CAUSE_MESSAGE"
            locale == Locale.ENGLISH
            innerError == INTEGRATION_KEY
        }

        where:
        interceptor << [new InterceptorException(CAUSE_MESSAGE), new JaloInvalidParameterException(CAUSE_MESSAGE, 0)]
    }

    @Test
    void "response is a BAD_REQUEST with the expected error code and error message when cause is #interceptor.class.simpleName"() {
        given:
        def interceptorExceptionMsg = 'message set for interceptor exception'
        def interceptorException = new InterceptorException(interceptorExceptionMsg, interceptor)
        def cause = new ModelSavingException('testMessage', interceptorException)
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(context(), cause))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            errorCode == expectedErrorCode
            message.contains(interceptorExceptionMsg)
            locale == Locale.ENGLISH
            innerError == INTEGRATION_KEY
        }

        where:
        interceptor                                                          | expectedErrorCode
        new IntegrationObjectItemAndClassNotAllowedInterceptor()             | INVALID_POJO_ITEM_MODELING
        new IntegrationObjectClassAndItemNotAllowedInterceptor()             | INVALID_POJO_ITEM_MODELING
        new IntegrationObjectTypeMixValidateInterceptor()                    | INVALID_POJO_ITEM_MODELING
        new RootClassExistsValidateInterceptor()                             | INVALID_ROOT_POJO
        new SingleRootClassValidateInterceptor()                             | INVALID_ROOT_POJO
        new IntegrationObjectClassRootValidateInterceptor(Stub(TypeService)) | INVALID_ROOT_POJO
        new IntegrationObjectClassAttributeReadMethodValidateInterceptor()   | INVALID_READ_METHOD
        new IntegrationObjectClassAttributeNameValidateInterceptor()         | INVALID_ATTRIBUTE_NAME
        new SingleRootItemValidator()                                        | INVALID_ATTRIBUTE_VALUE
        new IntegrationObjectClassTypeValidateInterceptor()                  | INVALID_CLASS_TYPE
    }

    @Test
    void "response has INTERNAL_ERROR status with cause exception message when the cause is of type SystemException"() {
        given:
        def cause = new SystemException(CAUSE_MESSAGE)
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(context(), cause))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
            errorCode == INTERNAL_ERROR
            message == "An error occurred while attempting to save entries for entityType: $ENTITY_TYPE_NAME, with error message $CAUSE_MESSAGE"
            locale == Locale.ENGLISH
            innerError == INTEGRATION_KEY
        }
    }

    @Test
    void 'response message does not contain cause message when the cause is not one of: SystemException, InterceptorException, JaloInvalidParameterException'() {
        given:
        def rootCause = new IllegalArgumentException('root cause message')
        def cause = new RuntimeException('cause message', rootCause)
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(context(), cause))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
            errorCode == INTERNAL_ERROR
            message == "An error occurred while attempting to save entries for entityType: $ENTITY_TYPE_NAME"
            locale == Locale.ENGLISH
            innerError == INTEGRATION_KEY
        }
    }

    @Test
    void 'handles absence of cause in PersistenceFailedException'() {
        given:
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(context(), null))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
            errorCode == INTERNAL_ERROR
            message == "An error occurred while attempting to save entries for entityType: $ENTITY_TYPE_NAME"
            locale == Locale.ENGLISH
            innerError == INTEGRATION_KEY
        }
    }

    @Test
    void 'handles absence of persistence context in PersistenceFailedException'() {
        given:
        def cause = new Exception(CAUSE_MESSAGE)
        def errorContext = new ODataErrorContext(exception: new PersistenceFailedException(null, cause))

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
            errorCode == INTERNAL_ERROR
            message == 'An error occurred while attempting to save entries for entityType: null'
            locale == Locale.ENGLISH
            !innerError
        }
    }

    @Test
    void 'does not populate the error context for exceptions other than PersistenceFailedException'() {
        given:
        def errorContext = new ODataErrorContext(exception: new RuntimeException())

        when:
        populator.populate errorContext

        then:
        with(errorContext) {
            !httpStatus
            !errorCode
            !message
            !locale
            !innerError
        }
    }

    @Test
    void 'handles PersistenceFailedException'() {
        expect:
        populator.exceptionClass == PersistenceFailedException
    }

    PersistenceContext context() {
        Stub(PersistenceContext) {
            getIntegrationItem() >> Stub(IntegrationItem) {
                getItemType() >> Stub(TypeDescriptor) {
                    getItemCode() >> ENTITY_TYPE_NAME
                }
                getIntegrationKey() >> INTEGRATION_KEY
            }
        }
    }
}
