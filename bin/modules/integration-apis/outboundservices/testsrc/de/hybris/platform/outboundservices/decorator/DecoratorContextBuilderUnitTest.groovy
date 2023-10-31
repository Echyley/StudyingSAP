/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Shared

import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder

@UnitTest
class DecoratorContextBuilderUnitTest extends JUnitPlatformSpecification {

    private static final OUTBOUND_SOURCE = OutboundSource.WEBHOOKSERVICES
    private static final ITEM = new ItemModel()
    private static final DESTINATION = new ConsumedDestinationModel()

    @Shared
    def INTEGRATION_OBJECT = Stub IntegrationObjectDescriptor

    @Test
    def "test build when all fields are provided"() {
        when:
        def providedErrors = ["error 1", "error 2"]
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withOutboundSource(OUTBOUND_SOURCE)
                .withErrors(providedErrors)
                .build()

        then:
        with(context) {
            payloadObject == payloadObject
            destinationModel == DESTINATION
            integrationObject == INTEGRATION_OBJECT
            source == OUTBOUND_SOURCE
            errors == providedErrors
            hasErrors()
        }
    }

    @Test
    def "error references are not leaked"() {
        when:
        def providedErrors = ["error 1", "error 2"]
        def builder = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withErrors(providedErrors)

        and: 'provided errors ar modified'
        providedErrors.clear()
        def context = builder.build()

        then: 'the product is not affected'
        context.errors == ["error 1", "error 2"]
    }

    @Test
    def "test build when errors are not provided"() {
        when:
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .build()

        then:
        with(context) {
            errors == []
            !hasErrors()
        }
    }

    @Test
    def "IllegalArgumentException is thrown when request is built with null #condition"() {
        when:
        decoratorContextBuilder()
                .withDestinationModel(destination)
                .withIntegrationObject(io)
                .withPayloadObject(item)
                .withOutboundSource(OUTBOUND_SOURCE)
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == "$condition cannot be null"

        where:
        condition                     | item | destination | io
        'ConsumedDestinationModel'    | ITEM | null        | INTEGRATION_OBJECT
        'IntegrationObjectDescriptor' | ITEM | DESTINATION | null
    }

    @Test
    def "build DecoratorContext with #expectedHttpMethod when httpMethod is provided as #providedHttpMethod"() {
        given:
        def key = "some key"
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withIntegrationKey(key)
                .withPayloadObject(ITEM)
                .withOutboundSource(OUTBOUND_SOURCE)
                .withHttpMethod(providedHttpMethod)
                .build()

        expect:
        context.httpMethod == expectedHttpMethod

        where:
        expectedHttpMethod | providedHttpMethod
        HttpMethod.POST    | null
        HttpMethod.POST    | HttpMethod.POST
        HttpMethod.DELETE  | HttpMethod.DELETE
    }

    @Test
    def "build DecoratorContext with #expectedIntegrationKey when integrationKey is provided as #providedIntegrationKey"() {
        given:
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withOutboundSource(OUTBOUND_SOURCE)
                .withIntegrationKey(providedIntegrationKey)
                .build()

        expect:
        context.integrationKey == expectedIntegrationKey

        where:
        expectedIntegrationKey | providedIntegrationKey
        null                   | null
        ''                     | ''
        'actual|value'         | 'actual|value'
    }

    @Test
    def "test build with no source defaults to unknown source"() {
        given:
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .build()

        expect:
        context.source == OutboundSource.UNKNOWN
    }

    @Test
    def 'default to UNKNOWN source when null source was provided'() {
        when:
        def context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withOutboundSource(null)
                .build()

        then:
        context.source == OutboundSource.UNKNOWN
    }

    @Test
    def 'builds valid context with empty errors list when #cond errors were provided'() {
        when:
        def context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withErrors(providedErrors)
                .build()

        then:
        with(context) {
            errors == []
            !hasErrors()
        }

        where:
        cond    | providedErrors
        "null"  | null
        "empty" | []
    }

    @Test
    def "2 different decoratorContext instances can be created using the same decorator context builder"() {
        given:
        def sharedBuilder = decoratorContextBuilder()
        when:
        def context1 = sharedBuilder
                .withDestinationModel(new ConsumedDestinationModel())
                .withIntegrationObject(Stub(IntegrationObjectDescriptor))
                .withOutboundSource(OutboundSource.WEBHOOKSERVICES)
                .withPayloadObject(new ItemModel())
                .build()
        def context2 = sharedBuilder
                .withDestinationModel(new ConsumedDestinationModel())
                .withIntegrationObject(Stub(IntegrationObjectDescriptor))
                .withOutboundSource(OutboundSource.OUTBOUNDSYNC)
                .withPayloadObject(new ItemModel())
                .build()

        then:
        !context1.is(context2)
        context1.itemModel != context2.itemModel
        context1.destinationModel != context2.destinationModel
        context1.integrationObject != context2.integrationObject
        context1.source != context2.source
    }

    @Test
    @Issue("https://cxjira.sap.com/browse/IAPI-5212")
    def "test build without event type results in 'Unknown'"() {
        given:
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .build()

        expect:
        context.eventType == DefaultEventType.UNKNOWN
    }


    @Test
    @Issue("https://cxjira.sap.com/browse/IAPI-5212")
    def "test build with event type"() {
        given:
        final DecoratorContext context = decoratorContextBuilder()
                .withDestinationModel(DESTINATION)
                .withIntegrationObject(INTEGRATION_OBJECT)
                .withPayloadObject(ITEM)
                .withEventType(DefaultEventType.CREATED)
                .build()

        expect:
        context.eventType == DefaultEventType.CREATED
    }
}
