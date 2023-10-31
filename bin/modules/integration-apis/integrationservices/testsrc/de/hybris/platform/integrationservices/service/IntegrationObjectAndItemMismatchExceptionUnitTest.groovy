/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectAndItemMismatchExceptionUnitTest extends JUnitPlatformSpecification {
    @Test
    def "provided context can be read back from the exception"() {
        given:
        def itemModel = Stub(ItemModel)
        def io = Stub(IntegrationObjectDescriptor)

        expect:
        def e = new IntegrationObjectAndItemMismatchException(itemModel, io)
        e.payloadObject == itemModel
        e.integrationObject == io
    }

    @Test
    def "POJO: provided context can be read back from the exception"() {
        given:
        def pojo = Stub(Object)
        def io = Stub(IntegrationObjectDescriptor)

        expect:
        def e = new IntegrationObjectAndItemMismatchException(pojo, io)
        e.payloadObject == pojo
        e.integrationObject == io
    }

    @Test
    def "message includes provided context"() {
        given:
        def itemModel = Stub(ItemModel)
        def io = Stub(IntegrationObjectDescriptor)

        expect:
        def e = new IntegrationObjectAndItemMismatchException(itemModel, io)
        e.message.contains itemModel.toString()
        e.message.contains io.toString()
    }

    @Test
    def "POJO: message includes provided context"() {
        given:
        def pojo = new Object()
        def io = Stub(IntegrationObjectDescriptor)

        expect:
        def e = new IntegrationObjectAndItemMismatchException(pojo, io)
        e.message.contains pojo.toString()
        e.message.contains io.toString()
    }
}
