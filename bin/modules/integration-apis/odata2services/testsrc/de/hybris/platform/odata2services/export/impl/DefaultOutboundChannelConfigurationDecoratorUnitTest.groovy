/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

import java.util.stream.Collectors

@UnitTest
class DefaultOutboundChannelConfigurationDecoratorUnitTest extends JUnitPlatformSpecification {

    def defaultOutboundChannelConfigurationDecorator = new DefaultOutboundChannelConfigurationDecorator(
            descriptorFactory(),
            conversionService(),
            integrationObjectService(),
            nameGenerator()
    )

    def setup() {
        defaultOutboundChannelConfigurationDecorator.setSearchService(exportConfigurationSearchService())
    }

    @Test
    @Unroll
    def "constructor fails because #error"() {
        when:
        new DefaultOutboundChannelConfigurationDecorator(
                descriptorFactoryParam,
                conversionServiceParam,
                integrationObjectServiceParam,
                nameGeneratorParam)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == error + ' must not be null.'

        where:
        descriptorFactoryParam | conversionServiceParam | integrationObjectServiceParam | nameGeneratorParam | error
        null                   | conversionService()    | integrationObjectService()    | nameGenerator()    | 'descriptorFactory'
        descriptorFactory()    | null                   | integrationObjectService()    | nameGenerator()    | 'conversionService'
        descriptorFactory()    | conversionService()    | null                          | nameGenerator()    | 'integrationObjectService'
        descriptorFactory()    | conversionService()    | integrationObjectService()    | null               | 'nameGenerator'
    }

    @Test
    @Unroll
    def "verify that the #requestBody is not decorated"() {
        given: "an export entity set of IntegrationObjects, InboundChannelConfigurations or WebhookConfigurations"
        def exportEntities = exportEntities(url, requestBody)

        when: "the outbound channel configuration decorator is called"
        def decoratedExportEntities = defaultOutboundChannelConfigurationDecorator.decorate(exportEntities)

        then: "the export entity set is not decorated"
        decoratedExportEntities == exportEntities
        decoratedExportEntities.size() == 1
        with(decoratedExportEntities) {
            first().requestUrl == url
            first().requestBodies.size() == 1
            first().requestBodies.first() == requestBody
        }

        where:
        url                                                   | requestBody
        'IntegrationService/IntegrationObjects'               | 'ioExportBody'
        'IntegrationService/InboundChannelConfigurations'     | 'iccExportBody'
        'WebhookService/WebhookConfigurations'                | 'webhookExportBody'
    }

    @Test
    def "verify that the OutboundChannelConfiguration is decorated"() {
        given: "a list of expected export urls"
        def expectedUrls = ['{{hostUrl}}/odata2webservices/OutboundChannelConfig/OutboundChannelConfigurations',
                            '{{hostUrl}}/odata2webservices/OutboundChannelConfig/OutboundSyncStreamConfigurations',
                            '{{hostUrl}}/odata2webservices/OutboundChannelConfig/OutboundSyncJobs',
                            '{{hostUrl}}/odata2webservices/OutboundChannelConfig/OutboundSyncCronJobs',
                            '{{hostUrl}}/odata2webservices/OutboundChannelConfig/Triggers'] as List

        and: "an export entity set of one outbound channel configuration"
        def exportEntities = exportEntities("{{hostUrl}}/odata2webservices/OutboundChannelConfig/OutboundChannelConfigurations", "{'code': 'occCode'}")

        when: "the outbound channel configuration decorator is called"
        def decoratedExportEntities = defaultOutboundChannelConfigurationDecorator.decorate(exportEntities)

        then: "the export entity set is decorated with three more entities"
        decoratedExportEntities == exportEntities
        decoratedExportEntities.size() == expectedUrls.size()
        decoratedExportEntities.stream().map({ entry -> entry.requestUrl }).collect(Collectors.toList()) == expectedUrls
    }

    def exportEntities(final String requestUrl, final String requestBody) {
        ExportEntity exportEntity = new ExportEntity()
        exportEntity.requestUrl = requestUrl
        exportEntity.requestBodies = [requestBody] as Set
        return [exportEntity] as Set
    }

    def exportConfigurationSearchService() {
        Stub(ExportConfigurationSearchService) {
            findJobs(_) >> [Stub(ItemModel)]
            findCronJobs(_) >> [Stub(ItemModel)]
            findSteamConfigurations(_) >> [Stub(ItemModel)]
            findTriggers(_) >> [Stub(ItemModel)]
        }
    }

    def descriptorFactory() {
        Stub(DescriptorFactory) {
            createIntegrationObjectDescriptor(_) >>
                    Stub(IntegrationObjectDescriptor) {
                        getTypeDescriptor(_) >> Optional.of(Stub(TypeDescriptor) {
                            isInstance(_) >> true
                        })
                    }
        }
    }

    def conversionService() {
        Stub(IntegrationObjectConversionService) {
            convert(_ as ItemToMapConversionContext) >> [code: 'occCode', container: [id: 'containerId']]
        }
    }

    def integrationObjectService() {
        Stub(IntegrationObjectService)
    }

    def nameGenerator() {
        Stub(EntitySetNameGenerator) {
            generate("OutboundSyncStreamConfiguration") >> "OutboundSyncStreamConfigurations"
            generate("OutboundSyncJob") >> "OutboundSyncJobs"
            generate("OutboundSyncCronJob") >> "OutboundSyncCronJobs"
            generate("Trigger") >> "Triggers"
        }
    }

}
