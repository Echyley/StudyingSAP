/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultIntegrationObjectDecoratorUnitTest extends JUnitPlatformSpecification {

    def integrationObjectDecorator = new DefaultIntegrationObjectDecorator(
            getDescriptorFactory(),
            getConversionService(),
            getIntegrationObjectService(),
            getNameGenerator())

    @Test
    @Unroll
    def "constructor fails because #error"() {
        when:
        new DefaultIntegrationObjectDecorator(descriptorFactoryParam, conversionServiceParam, integrationObjectServiceParam, nameGeneratorParam)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == error

        where:
        descriptorFactoryParam | conversionServiceParam | integrationObjectServiceParam | nameGeneratorParam | error
        null                   | getConversionService() | getIntegrationObjectService() | getNameGenerator() | 'descriptorFactory must not be null.'
        getDescriptorFactory() | null                   | getIntegrationObjectService() | getNameGenerator() | 'conversionService must not be null.'
        getDescriptorFactory() | getConversionService() | null                          | getNameGenerator() | 'integrationObjectService must not be null.'
        getDescriptorFactory() | getConversionService() | getIntegrationObjectService() | null               | 'nameGenerator must not be null.'
    }

    @Unroll
    @Test
    def "verify that the #itemType is augmented by the referenced integration object"() {

        given: "an export entity set of OutboundChannelConfigurations or WebhookConfigurations"
        def exportEntities = getExportEntities(exportedIO)

        when: "the integration object decorator is called"
        def augmentedExportEntities = integrationObjectDecorator.decorate(exportEntities)

        then: "the export entity set of OutboundChannelConfigurations or WebhookConfigurations is augmented with the referenced integration object"
        augmentedExportEntities.size() == exportEntities.size() + 1
        with(augmentedExportEntities[0]) {
            requestUrl.contains("IntegrationService/IntegrationObjects")
            requestBodies.size() == 1
            requestBodies.first() == getAugmentedRequestBody()
        }
        with(augmentedExportEntities[1]) {
            requestUrl.contains(exportedIO)
            requestBodies.size() == 1
            requestBodies.first() == getRequestBody()
        }

        where:
        exportedIO              | itemType
        "OutboundChannelConfig" | "OutboundChannelConfiguration"
        "WebhookService"        | "WebhookConfiguration"
    }

    @Test
    def "verify that the InboundChannelConfiguration is not augmented"() {

        given: "an export entity set of InboundChannelConfigurations"
        def exportUrl = "http://configuration/export/endpoint/"
        def exportEntities = getExportEntities(exportUrl)

        when: "the integration object decorator is called"
        def augmentedExportEntities = integrationObjectDecorator.decorate(exportEntities)

        then: "the export entity set of InboundChannelConfigurations is not augmented"
        with(augmentedExportEntities) {
            size() == exportEntities.size()
            first().requestUrl == exportUrl
            first().requestBodies.size() == 1
            first().requestBodies.first() == getRequestBody()
        }
    }

    def getDescriptorFactory() {
        Stub(DescriptorFactory) {
            createIntegrationObjectDescriptor(_ as IntegrationObjectModel) >> Stub(IntegrationObjectDescriptor) {
                getTypeDescriptor(_ as ItemModel) >> Optional.of(Stub(TypeDescriptor) {
                    isInstance(_) >> true
                })
            }
        }
    }

    def getConversionService() {
        Stub(IntegrationObjectConversionService) {
            convert(_ as ItemModel, _ as IntegrationObjectDescriptor) >> [key: 'augmentedBody']
        }
    }

    def getIntegrationObjectService() {
        Stub(IntegrationObjectService) {
            findIntegrationObject(_) >> Stub(IntegrationObjectModel)
        }
    }

    def getExportEntities(final String requestUrl) {
        ExportEntity exportEntity = new ExportEntity()
        exportEntity.requestUrl = requestUrl
        exportEntity.requestBodies = [getRequestBody()] as Set
        return [exportEntity] as Set
    }

    def getAugmentedRequestBody() {
        '{\n    "key": "augmentedBody"\n}'
    }

    def getRequestBody() {
        '{\n    "key": "originalBody"\n}'
    }

    def getNameGenerator() {
        Stub(EntitySetNameGenerator) {
            generate(_) >> "IntegrationObjects"
        }
    }
}
