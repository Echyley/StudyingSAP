/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.util.LogLevel
import de.hybris.platform.integrationservices.util.LoggerProbe
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test

@UnitTest
class IntegrationObjectItemAttributeValidateInterceptorUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE ='AnyIO'
    private static final def TYPE_CODE = "AnyTypeCode"
    private static final def ATTR_NAME = "AnyAttrName"
    private static final def ATTRIBUTE = new IntegrationObjectItemAttributeModel()

    @Rule
    LoggerProbe log = LoggerProbe.create IntegrationObjectItemAttributeValidateInterceptor

    def descriptorFactory = Stub DescriptorFactory

    def interceptor = new IntegrationObjectItemAttributeValidateInterceptor(descriptorFactory)

    @Test
    def "warning is logged when attribute is abstract and autoCreate"() {
        given:
        descriptorFactory.createTypeAttributeDescriptor(ATTRIBUTE) >> stubAttributeDescriptor([autoCreate: true, abstract: true])

        when:
        interceptor.onValidate ATTRIBUTE, Stub(InterceptorContext)

        then:
        !log.allLoggedRecords.isEmpty()
        log.allLoggedRecords[0].level == LogLevel.WARN
        with (log.allLoggedRecords[0].message) {
            contains IO_CODE
            contains TYPE_CODE
            contains ATTR_NAME
            contains 'abstract'
        }
    }

    @Test
    def "exception is thrown when #msg"() {
        given:
        descriptorFactory.createTypeAttributeDescriptor(ATTRIBUTE) >> stubAttributeDescriptor(params)

        when:
        interceptor.onValidate ATTRIBUTE, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains "Collection, Map or Localized attribute [$TYPE_CODE.$ATTR_NAME] cannot be unique."

        where:
        params                                             | msg
        [keyAttribute: true, collection: true, map: false] | 'attribute is unique and collection'
        [keyAttribute: true, collection: true, map: false] | 'attribute is unique and map'
    }

    @Test
    def "no exception is thrown when #msg"() {
        given:
        descriptorFactory.createTypeAttributeDescriptor(ATTRIBUTE) >> stubAttributeDescriptor(params)

        when:
        interceptor.onValidate ATTRIBUTE, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        params                                               | msg
        [keyAttribute: true, collection: false, map: false]  | 'attribute is unique but is neither map nor collection'
        [keyAttribute: false, collection: true, map: false]  | 'attribute is not unique but is collection'
        [keyAttribute: false, collection: false, map: true]  | 'attribute is not unique but is map'
    }

    private stubAttributeDescriptor(Map params) {
        def typeDescriptor = Stub(TypeDescriptor) {
            getTypeCode() >> TYPE_CODE
            getIntegrationObjectCode() >> IO_CODE
        }

        def attributeTypeDescriptor = Stub (TypeDescriptor) {
            isAbstract() >> (params['abstract'] ?: false)
        }

        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> ATTR_NAME
            isAutoCreate() >> (params['autoCreate'] ?: false)
            isKeyAttribute() >> (params['keyAttribute'] ?: false)
            isCollection() >> (params['collection'] ?: false)
            isMap() >> (params['map'] ?: false)
            getTypeDescriptor() >> typeDescriptor
            getAttributeType() >> attributeTypeDescriptor
        }
    }
}
