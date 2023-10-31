/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.decorator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

@UnitTest
class WebhookPayloadBuildingRequestDecoratorUnitTest extends JUnitPlatformSpecification {

    private static final Map<String, Object> CONVERTED_ENTITY = [key1: "value1", key2: "value2"]
    def decorator = new WebhookPayloadBuildingRequestDecorator(
            integrationObjectConversionService: Stub(IntegrationObjectConversionService))

    @Test
    def 'decorated payload includes map representing the integration object'() {
        given:
        def headers = new HttpHeaders()
        Map<String, Object> payload = new HashMap<>()
        def context = decoratorContext(item)
        def execution = Stub(DecoratorExecution) {
            createHttpEntity(headers, payload, context) >> new HttpEntity<>(payload, headers)
        }

        when:
        def httpEntity = decorator.decorate(headers, payload, context, execution)

        then:
        httpEntity.body == convertedEntity

        where:
        item                                            | convertedEntity
        new WebhookPayloadModel(data: CONVERTED_ENTITY) | CONVERTED_ENTITY
        new ItemModel()                                 | [:]
        new Object()                                    | [:]
    }

    private DecoratorContext decoratorContext(final Object item) {
        Stub(DecoratorContext) {
            getItemModel() >> item
            getPayloadObject() >> item
            getIntegrationObject() >> Stub(IntegrationObjectDescriptor)
            getIntegrationObjectItem() >> Optional.of(createContextTypeDescriptor(item))
        }
    }

    private TypeDescriptor createContextTypeDescriptor(final Object item) {
        Stub(TypeDescriptor) {
            isInstance(item) >> true
        }
    }

}
