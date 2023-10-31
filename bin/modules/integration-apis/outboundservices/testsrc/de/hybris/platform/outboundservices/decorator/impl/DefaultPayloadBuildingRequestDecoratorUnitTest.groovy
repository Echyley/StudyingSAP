/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.service.IntegrationObjectAndItemMismatchException
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import spock.lang.Unroll

@UnitTest
class DefaultPayloadBuildingRequestDecoratorUnitTest extends JUnitPlatformSpecification {

    private static final Map<String, Object> CONVERTED_ENTITY = ["HEADER": "VALUE"]
    private static final ItemModel CONTEXT_ITEM = new ItemModel()
    private static final HttpHeaders HEADERS = new HttpHeaders()

    def integrationObjectDescriptor = Stub(IntegrationObjectDescriptor)
    def integrationObjectConversionService = Stub(IntegrationObjectConversionService)
    def typeDescriptor = createContextTypeDescriptor()

    def decorator = new DefaultPayloadBuildingRequestDecorator(integrationObjectConversionService: integrationObjectConversionService)

    @Test
    @Unroll
    def 'decorator isApplicable returns #applicable when http method is #condition'() {
        given:
        def context = Stub(DecoratorContext) {
            getHttpMethod() >> condition
        }
        expect:
        decorator.isApplicable(context) == applicable

        where:
        applicable | condition
        false      | HttpMethod.DELETE
        true       | HttpMethod.POST
        true       | HttpMethod.PATCH
    }

    @Test
    def 'decorated payload includes map representing the integration object'() {
        given:
        def context = decoratorContext()
        def createdEntity = new HttpEntity([:])
        def execution = Stub(DecoratorExecution) {
            createHttpEntity(HEADERS, CONVERTED_ENTITY, context) >> createdEntity
        }
        integrationObjectConversionService.convert(_ as ItemToMapConversionContext) >> CONVERTED_ENTITY

        when:
        def entity = decorator.decorate(HEADERS, new HashMap<>(), context, execution)

        then:
        entity.is createdEntity
    }

    @Test
    def 'an exception is thrown when item does not match the integration object structure'() {
        given:
        def context = Stub(DecoratorContext) {
            getIntegrationObjectItem() >> Optional.empty()
            getIntegrationObject() >> integrationObjectDescriptor
        }

        when:
        decorator.decorate(HEADERS, new HashMap<>(), context, Stub(DecoratorExecution))

        then:
        def e = thrown(IntegrationObjectAndItemMismatchException)
        e.integrationObject == integrationObjectDescriptor
    }

    @Test
    def 'decorator propagates exceptions thrown by the conversion service'() {
        given:
        integrationObjectConversionService.convert(_) >> { throw new NullPointerException() }

        when:
        decorator.decorate(HEADERS, new HashMap<>(), decoratorContext(), Stub(DecoratorExecution))

        then:
        thrown(NullPointerException)
    }

    TypeDescriptor createContextTypeDescriptor() {
        Stub(TypeDescriptor) {
            isInstance(CONTEXT_ITEM) >> true
        }
    }

    DecoratorContext decoratorContext() {
        Stub(DecoratorContext) {
            getPayloadObject() >> CONTEXT_ITEM
            getIntegrationObject() >> integrationObjectDescriptor
            getIntegrationObjectItem() >> Optional.of(typeDescriptor)
        }
    }
}
