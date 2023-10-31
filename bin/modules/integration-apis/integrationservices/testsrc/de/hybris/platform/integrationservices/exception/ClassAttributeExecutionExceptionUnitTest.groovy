/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ClassAttributeExecutionExceptionUnitTest extends JUnitPlatformSpecification {

    @Test
    def "message is #expectedMessage when #condition"() {
        given:
        def exception = new ClassAttributeExecutionException(attribute)

        expect:
        exception.getMessage().contains(expectedMessage)

        where:
        condition                    | attribute                  | expectedMessage
        'read method does not exist' | attributeWithReadMethod()  | 'ReadMethod getMyAttribute from attribute noReadMethodAttribute does not exist.'
        'attribute does not exist'   | nonExistingAttributeName() | 'Attribute nonExistingAttribute for class java.lang.String does not exist'

    }

    IntegrationObjectClassAttributeModel attributeWithReadMethod() {
        Stub(IntegrationObjectClassAttributeModel) {
            getReadMethod() >> 'getMyAttribute'
            getAttributeName() >> 'noReadMethodAttribute'
        }
    }

    IntegrationObjectClassAttributeModel nonExistingAttributeName() {
        Stub(IntegrationObjectClassAttributeModel) {
            getIntegrationObjectClass() >> Stub(IntegrationObjectClassModel) {
                getType() >> String.class
            }
            getAttributeName() >> 'nonExistingAttribute'
        }
    }
}
