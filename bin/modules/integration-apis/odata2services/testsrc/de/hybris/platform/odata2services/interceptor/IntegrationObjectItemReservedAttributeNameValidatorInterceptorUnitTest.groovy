/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.interceptor

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

class IntegrationObjectItemReservedAttributeNameValidatorInterceptorUnitTest extends JUnitPlatformSpecification {

    def interceptor = new IntegrationObjectItemReservedAttributeNameValidatorInterceptor()

    @Test
    @Unroll
    def 'exception is thrown when attribute name is #attrName'() {
        given:
        def attribute = attribute attrName

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains "[$attrName] attribute name is reserved. Please rename this attribute."

        where:
        attrName << ['localizedAttributes', 'integrationKey']
    }

    @Test
    @Unroll
    def 'exception is not throw when attribute name is not reserved'(){
        given:
        def attribute = attribute "code"

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    def attribute(String attrName) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> attrName
        }
    }
}
