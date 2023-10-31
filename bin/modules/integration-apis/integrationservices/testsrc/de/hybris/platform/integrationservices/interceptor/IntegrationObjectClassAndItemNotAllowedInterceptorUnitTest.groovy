/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectClassAndItemNotAllowedInterceptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CODE = "io"
    private static final def ITEM_CODE = "item"

    private static final def ITEM = new IntegrationObjectItemModel(code: ITEM_CODE)

    def validator = new IntegrationObjectClassAndItemNotAllowedInterceptor()

    @Test
    def "no exception when assigning POJO type to integration object without Model type"() {
        given: "the integration object #condition"
        def clazz = Stub(IntegrationObjectClassModel) {
            getIntegrationObject() >> Stub(IntegrationObjectModel) {
                getItems() >> items
            }
        }

        when:
        validator.onValidate clazz, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        items << [null, []]
    }

    @Test
    def 'throws exception when assigning POJO type to integration object with Model type'() {
        given:
        def clazz = Stub(IntegrationObjectClassModel) {
            getIntegrationObject() >> Stub(IntegrationObjectModel) {
                getCode() >> IO_CODE
                getItems() >> [ITEM]
            }
        }

        when:
        validator.onValidate clazz, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains("Integration object [$IO_CODE] has integration object item(s) [$ITEM_CODE] assigned")
    }
}
