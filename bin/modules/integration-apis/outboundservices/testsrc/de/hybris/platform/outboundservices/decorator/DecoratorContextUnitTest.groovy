/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DecoratorContextUnitTest extends JUnitPlatformSpecification {
    def ITEM = Stub(ItemModel)
    def IO_CODE = 'TestObject'

    @Test
    def 'derives integration object code'() {
        when:
        def context = contextBuilder().withIntegrationObject(integrationObject(IO_CODE)).build()

        then:
        context.integrationObjectCode == IO_CODE
    }

    @Test
    def 'derives context integration object item when a matching item exists in the integration object'() {
        given: 'integration object contains an item matching the item model type'
        def itemCode = 'SomeItem'
        def item = Stub(TypeDescriptor) {
            getItemCode() >> itemCode
        }
        def io = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM) >> Optional.of(item)
            getTypeDescriptor(ITEM) >> Optional.of(item)
        }
        when:
        def context = contextBuilder()
                .withPayloadObject(ITEM)
                .withIntegrationObject(io)
                .build()

        then:
        context.integrationObjectItem == Optional.of(item)
    }

    @Test
    def 'does not derive a context integration object item when a matching item not found in the integration object'() {
        given: 'integration object has no matching item for the item model type'
        def io = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM) >> Optional.empty()
        }
        when:
        def context = contextBuilder()
                .withPayloadObject(ITEM)
                .withIntegrationObject(io)
                .build()

        then:
        context.integrationObjectItem.empty
    }

    @Test
    def "IllegalArgumentException is thrown with message \"#message\" when HttpMethod is #method, payload is #payload, and the integrationKey is #key"() {
        given:
        def itemCode = 'SomeItem'

        def item = Stub(TypeDescriptor) {
            getItemCode() >> itemCode
        }

        def io = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM) >> Optional.of(item)
        }

        when:
        contextBuilder()
                .withIntegrationObject(io)
                .withIntegrationKey(key)
                .withPayloadObject(payload)
                .withHttpMethod(method)
                .build()

        then:
        def e = thrown IllegalArgumentException
        message == e.message

        where:
        method            | payload      | key        | message
        HttpMethod.POST   | null         | "some key" | "A payload must be provided for the HttpMethod POST"
        HttpMethod.DELETE | new Object() | null       | "An integration key must be provided for the HttpMethod DELETE."
        HttpMethod.DELETE | new Object() | ""         | "An integration key must be provided for the HttpMethod DELETE."
    }

    @Test
    def 'getting error list does not leak reference'() {
        given:
        def errors = ['some errors']
        def context = contextBuilder()
                .withIntegrationObject(integrationObject())
                .withErrors(errors).build()
        and:
        def contextErrors = context.errors

        when:
        contextErrors.clear()

        then:
        !context.errors.empty
    }

    DecoratorContextBuilder contextBuilder() {
        DecoratorContext.decoratorContextBuilder()
                .withPayloadObject(ITEM)
                .withDestinationModel(new ConsumedDestinationModel())
                .withOutboundSource(OutboundSource.OUTBOUNDSYNC)
    }

    IntegrationObjectDescriptor integrationObject(String ioCode = IO_CODE) {
        Stub(IntegrationObjectDescriptor) {
            getCode() >> ioCode
        }
    }
}
