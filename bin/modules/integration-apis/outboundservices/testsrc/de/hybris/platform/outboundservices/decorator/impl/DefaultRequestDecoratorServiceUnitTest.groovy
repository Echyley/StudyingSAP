/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorContextFactory
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import spock.lang.Shared

@UnitTest
class DefaultRequestDecoratorServiceUnitTest extends JUnitPlatformSpecification {
    private static final HEADERS = new HttpHeaders()
    private static final PAYLOAD = [:]
    private static final String ENDPOINT_URL = "http://my.consumed.destination/some/path"
    private static final String DESTINATION_ID = 'destination'
    private static final String ITEM_TYPE = 'MyType'
    private static final String IO_CODE = 'integrationObjectCode'
    private static final IntegrationObjectModel IO = new IntegrationObjectModel(code: IO_CODE)
    private static final DESTINATION = new ConsumedDestinationModel(id: DESTINATION_ID, url: ENDPOINT_URL)
    @Shared
    def ITEM = Stub(ItemModel) {
        getItemtype() >> ITEM_TYPE
    }

    def enabledDecorator1 = mockDecorator()

    def enabledDecorator2 = mockDecorator()

    def contextFactory = Stub(DecoratorContextFactory)

    def decoratorService = new DefaultRequestDecoratorService(contextFactory)

    def setup() {
        decoratorService.decorators = [enabledDecorator1, enabledDecorator2]
    }

    @Test
    def 'only enabled and applicable decorators are invoked'() {
        given:
        def context = decoratorContext()
        contextFactory.createContext(_ as SyncParameters) >> context

        when:
        decoratorService.createHttpEntity syncParameters()

        then:
        1 * enabledDecorator1.isEnabled() >> true
        1 * enabledDecorator2.isEnabled() >> false

        1 * enabledDecorator1.decorate([:], [:], context, _) >> { List args ->
            (args[3] as DecoratorExecution).createHttpEntity(args[0], args[1], args[2])
        }
        0 * enabledDecorator2.decorate([:], [:], context, _)
    }

    @Test
    def 'decorator is #decoratorStatus when #decoratorDescription'() {
        given:
        def context = decoratorContext()
        contextFactory.createContext(_ as SyncParameters) >> context

        when:
        decoratorService.createHttpEntity(syncParameters())

        then:
        1 * enabledDecorator1.isEnabled() >> decoratorEnabled
        (decoratorEnabled ? 1 : 0) * enabledDecorator1.isApplicable(context) >> decoratorApplicable

        decorate * enabledDecorator1.decorate([:], [:], context, _) >> { List args ->
            (args[3] as DecoratorExecution).createHttpEntity(args[0], args[1], args[2])
        }

        where:
        decoratorStatus | decoratorDescription          | decoratorEnabled | decoratorApplicable | decorate
        "not invoked"   | "disabled and not applicable" | false            | false               | 0
        "not invoked"   | "disabled and applicable"     | false            | true                | 0
        "not invoked"   | "enabled and not applicable"  | true             | false               | 0
        "invoked"       | "enabled and applicable"      | true             | true                | 1
    }

    @Test
    def 'does not report an exception when a decorator context contains errors'() {
        given: 'created decorator context contains errors'
        def parameters = syncParameters()
        def decoratorContext = decoratorContext()
        decoratorContext.getErrors() >> ['problem 1', 'problem 2']
        decoratorContext.hasErrors() >> true
        contextFactory.createContext(parameters) >> decoratorContext

        when: 'service is called'
        decoratorService.createHttpEntity(parameters)

        then: 'decorators are invoked'
        1 * enabledDecorator1.isEnabled() >> true
        1 * enabledDecorator2.isEnabled() >> false

        1 * enabledDecorator1.decorate([:], [:], decoratorContext, _)
    }

    private OutboundRequestDecorator mockDecorator() {
        Mock(OutboundRequestDecorator) {
            isApplicable(_ as DecoratorContext) >> true
            decorate(HEADERS, PAYLOAD, _, _) >> Stub(HttpEntity)
        }
    }

    private DecoratorContext decoratorContext() {
        decoratorContext(DESTINATION)
    }

    private DecoratorContext decoratorContext(final ConsumedDestinationModel dest) {
        Stub(DecoratorContext) {
            getItemModel() >> ITEM
            getDestinationModel() >> dest
            getIntegrationObjectCode() >> IO_CODE
            getIntegrationObjectItem() >> ITEM
        }
    }

    SyncParameters syncParameters() {
        SyncParameters.syncParametersBuilder()
                .withDestination(DESTINATION)
                .withIntegrationObject(IO)
                .withPayloadObject(ITEM)
                .build()
    }
}
