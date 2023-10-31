/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.facade.SyncParametersBuilder
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultDecoratorContextFactoryUnitTest extends JUnitPlatformSpecification {
    private static final def OUTBOUND_SOURCE = OutboundSource.WEBHOOKSERVICES
    private static final def ITEM = new ItemModel()
    private static final def DESTINATION_ID = 'testDestinationId'
    private static final def DESTINATION = new ConsumedDestinationModel(id: DESTINATION_ID)
    private static final def IO_CODE = 'TestIOCode'
    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_CODE)
    private static final def INTEGRATION_KEY = 'integration|key|value'
    private static final def EVENT_TYPE = DefaultEventType.CREATED
    private static final String DEST_NOT_FOUND_ERROR_MSG = "Provided destination '$DESTINATION_ID' was not found."

    @Shared
    def IO_DESCRIPTOR = Stub(IntegrationObjectDescriptor) {
        getCode() >> IO_CODE
    }

    def descriptorFactory = Stub(DescriptorFactory) {
        createIntegrationObjectDescriptor(IO_MODEL) >> IO_DESCRIPTOR
    }

    def deprecatedFactory =
            new DefaultDecoratorContextFactory(
                    Stub(IntegrationObjectService),
                    Stub(FlexibleSearchService),
                    descriptorFactory)
    def factory = new DefaultDecoratorContextFactory(descriptorFactory)

    @Test
    def "deprecated factory throws IllegalArgumentException when a null DescriptorFactory is provided"() {
        when:
        new DefaultDecoratorContextFactory(Stub(IntegrationObjectService), Stub(FlexibleSearchService), null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "DescriptorFactory must be provided."
    }

    @Test
    def "factory throws IllegalArgumentException when a null DescriptorFactory is provided"() {
        when:
        new DefaultDecoratorContextFactory(null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "DescriptorFactory must be provided."
    }

    @Test
    def "deprecated factory creates context with specified parameters"() {
        given:
        def params = parametersBuilder()
                .withPayloadObject(ITEM)
                .withIntegrationObject(IO_MODEL)
                .withDestination(DESTINATION)
                .withSource(OUTBOUND_SOURCE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withEventType(EVENT_TYPE)
                .build()

        when:
        def decoratorContext = deprecatedFactory.createContext params

        then:
        with(decoratorContext) {
            destinationModel == DESTINATION
            integrationObject == IO_DESCRIPTOR
            payloadObject == ITEM
            source == OUTBOUND_SOURCE
            integrationKey == INTEGRATION_KEY
            eventType == EVENT_TYPE
        }
    }

    @Test
    def "creates context with specified parameters"() {
        given:
        def params = parametersBuilder()
                .withPayloadObject(ITEM)
                .withIntegrationObject(IO_MODEL)
                .withDestination(DESTINATION)
                .withSource(OUTBOUND_SOURCE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withEventType(EVENT_TYPE)
                .build()

        when:
        def decoratorContext = factory.createContext params

        then:
        with(decoratorContext) {
            destinationModel == DESTINATION
            integrationObject == IO_DESCRIPTOR
            payloadObject == ITEM
            source == OUTBOUND_SOURCE
            integrationKey == INTEGRATION_KEY
            eventType == EVENT_TYPE
        }
    }

    @Test
    def "deprecated factory creates context with #expectedHttpMethod when changed item model is #condition"() {
        when:
        def decoratorContext = deprecatedFactory.createContext outboundParams.build()

        then:
        decoratorContext.httpMethod == expectedHttpMethod

        where:
        expectedHttpMethod | condition     | outboundParams
        HttpMethod.DELETE  | "not present" | defaultBuilder().withPayloadObject(null)
        HttpMethod.POST    | "present"     | defaultBuilder().withPayloadObject(ITEM)
    }

    @Test
    def "creates context with #expectedHttpMethod when changed item model is #condition"() {
        when:
        def decoratorContext = factory.createContext outboundParams.build()

        then:
        decoratorContext.httpMethod == expectedHttpMethod

        where:
        expectedHttpMethod | condition     | outboundParams
        HttpMethod.DELETE  | "not present" | defaultBuilder().withPayloadObject(null)
        HttpMethod.POST    | "present"     | defaultBuilder().withPayloadObject(ITEM)
    }

    @Test
    def "deprecated factory creates context with no errors when destinationModel and integrationObjectDescriptor are provided in SyncParameters"() {
        given:
        final SyncParameters paramsWithModels = defaultBuilder().build()

        when:
        def decoratorContext = deprecatedFactory.createContext(paramsWithModels)

        then:
        with(decoratorContext) {
            errors == []
            !hasErrors()
        }
    }

    @Test
    def "creates context with no errors when destinationModel and integrationObjectDescriptor are provided in SyncParameters"() {
        given:
        final SyncParameters paramsWithModels = defaultBuilder().build()

        when:
        def decoratorContext = factory.createContext(paramsWithModels)

        then:
        with(decoratorContext) {
            errors == []
            !hasErrors()
        }
    }

    @Test
    def "deprecated factory creates context with error when provided destination is ConsumedDestinationNotFoundModel"() {
        given:
        def nonExistingDestination = new ConsumedDestinationNotFoundModel(DESTINATION_ID)

        and:
        def paramsWithErrorModel = defaultBuilder().withDestination(nonExistingDestination).build()

        when:
        def decoratorContext = deprecatedFactory.createContext(paramsWithErrorModel)

        then:
        with(decoratorContext) {
            destinationModel.is nonExistingDestination
            errors == [DEST_NOT_FOUND_ERROR_MSG]
            hasErrors()
        }
    }

    @Test
    def "creates context with error when provided destination is ConsumedDestinationNotFoundModel"() {
        given:
        def nonExistingDestination = new ConsumedDestinationNotFoundModel(DESTINATION_ID)

        and:
        def paramsWithErrorModel = defaultBuilder().withDestination(nonExistingDestination).build()

        when:
        def decoratorContext = factory.createContext(paramsWithErrorModel)

        then:
        with(decoratorContext) {
            destinationModel.is nonExistingDestination
            errors == [DEST_NOT_FOUND_ERROR_MSG]
            hasErrors()
        }
    }

    private static SyncParametersBuilder parametersBuilder() {
        SyncParameters.syncParametersBuilder()
    }

    private defaultBuilder() {
        parametersBuilder()
                .withPayloadObject(ITEM)
                .withIntegrationObject(IO_MODEL)
                .withDestination(DESTINATION)
                .withSource(OUTBOUND_SOURCE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withEventType(EVENT_TYPE)
    }
}
