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
import de.hybris.platform.scripting.engine.repository.impl.ModelScriptsRepository
import de.hybris.platform.scripting.model.ScriptModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultScriptDecoratorUnitTest extends JUnitPlatformSpecification {

    def defaultScriptDecorator = new DefaultScriptDecorator(
            descriptorFactory(),
            integrationObjectService(),
            conversionService(),
            scriptsRepository(),
            nameGenerator()
    )

    @Test
    @Unroll
    def "constructor fails because #error"() {
        when:
        new DefaultScriptDecorator(descriptorFactoryParam, integrationObjectServiceParam, conversionServiceParam, scriptsRepositoryParam, nameGeneratorParam)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == error + ' must not be null.'

        where:
        descriptorFactoryParam | integrationObjectServiceParam | conversionServiceParam | scriptsRepositoryParam | nameGeneratorParam | error
        null                   | integrationObjectService()    | conversionService()    | scriptsRepository()    | nameGenerator()    | 'descriptorFactory'
        descriptorFactory()    | null                          | conversionService()    | scriptsRepository()    | nameGenerator()    | 'integrationObjectService'
        descriptorFactory()    | integrationObjectService()    | null                   | scriptsRepository()    | nameGenerator()    | 'conversionService'
        descriptorFactory()    | integrationObjectService()    | conversionService()    | null                   | nameGenerator()    | 'scriptsRepository'
        descriptorFactory()    | integrationObjectService()    | conversionService()    | scriptsRepository()    | null               | 'nameGenerator'

    }

    @Unroll
    @Test
    def "verify that the #ioItemType is decorated by the referenced script"() {

        given: "an export entity set of IntegrationObjects, InboundChannelConfigurations or WebhookConfigurations"
        def exportEntities = exportEntities(exportEndpoint, exportBody)

        when: "the script decorator is called"
        def augmentedExportEntities = defaultScriptDecorator.decorate(exportEntities)

        then: "the exported entity set is augmented with the referenced script"
        augmentedExportEntities.size() == exportEntities.size() + 1
        with(augmentedExportEntities[0]) {
            requestUrl.contains("ScriptService/Scripts")
            requestBodies.size() == 1
            requestBodies.first() == augmentedScriptBody()
        }
        with(augmentedExportEntities[1]) {
            requestUrl.contains(exportEndpoint)
            requestBodies.size() == 1
            requestBodies.first() == exportBody
        }

        where:
        exportEndpoint                                    | ioItemType                    | exportBody
        "IntegrationService/IntegrationObjects"           | "IntegrationObject"           | ioBody()
        "IntegrationService/InboundChannelConfigurations" | "InboundChannelConfiguration" | iccBody()
        "WebhookService/WebhookConfigurations"            | "WebhookConfiguration"        | webhookBody()
    }

    @Test
    def "verify that the OutboundChannelConfiguration is not decorated"() {

        given: "an export entity set of OutboundChannelConfigurations"
        def exportEntities = exportEntities("/OutboundChannelConfig/OutboundChannelConfigurations", "occExportBody")

        when: "the integration object decorator is called"
        def augmentedExportEntities = defaultScriptDecorator.decorate(exportEntities)

        then: "the export entity set of OutboundChannelConfigurations is not augmented"
        with(augmentedExportEntities) {
            size() == exportEntities.size()
            first().requestUrl == "/OutboundChannelConfig/OutboundChannelConfigurations"
            first().requestBodies.size() == 1
            first().requestBodies.first() == "occExportBody"
        }
    }

    @Test
    def "when the webhook filterLocation is null the WebhookConfiguration is not decorated"() {
        given: "an export entity set of WebhookConfigurations with a null filterLocation"
        def webhookEndpoint = "WebhookService/WebhookConfigurations"
        def webhookBody = webhookBodyWithNullFilterLocation()
        def webhookExportEntities = exportEntities(webhookEndpoint, webhookBody)
        when: "the script decorator is called"
        def decoratedExportEntities = defaultScriptDecorator.decorate(webhookExportEntities)
        then: "the exported entity set is not decorated"
        decoratedExportEntities.size() == webhookExportEntities.size()
        with(decoratedExportEntities[0]) {
            requestUrl.contains(webhookEndpoint)
            requestBodies.size() == 1
            requestBodies.first() == webhookBody
        }
    }

    def descriptorFactory() {
        Stub(DescriptorFactory) {
            createIntegrationObjectDescriptor(_ as IntegrationObjectModel) >> Stub(IntegrationObjectDescriptor) {
                getTypeDescriptor(_ as ItemModel) >> Optional.of(Stub(TypeDescriptor) {
                    isInstance(_) >> true
                })
            }
        }
    }

    def conversionService() {
        Stub(IntegrationObjectConversionService) {
            convert(_ as ItemModel, _ as IntegrationObjectDescriptor) >> [code: 'scriptName', content: 'scriptContent']
        }
    }

    def integrationObjectService() {
        Stub(IntegrationObjectService) {
            findIntegrationObject(_) >> Stub(IntegrationObjectModel)
        }
    }

    def exportEntities(final String requestUrl, final String requestBody) {
        ExportEntity exportEntity = new ExportEntity()
        exportEntity.requestUrl = requestUrl
        exportEntity.requestBodies = [requestBody] as Set
        return [exportEntity] as Set
    }

    def augmentedScriptBody() {
        '{\n' +
                '    "code": "scriptName",\n' +
                '    "content": "scriptContent"\n' +
                '}'
    }

    def webhookBody() {
        '{\n' +
                '  "filterLocation": "model://script"\n' +
                '}'
    }

    def webhookBodyWithNullFilterLocation() {
        '{\n' +
                '  "filterLocation": null\n' +
                '}'
    }

    def ioBody() {
        '{\n' +
                '  "items": [\n' +
                '    {\n' +
                '      "virtualAttributes": [\n' +
                '        {\n' +
                '          "retrievalDescriptor": {\n' +
                '            "logicLocation": "model://redScript"\n' +
                '          }\n' +
                '        }\n' +
                '      ]\n' +
                '    }\n' +
                '  ]\n' +
                '}'
    }

    def iccBody() {
        '{\n' +
                '  "integrationObject": {\n' +
                '    "items": [\n' +
                '      {\n' +
                '        "virtualAttributes": [\n' +
                '          {\n' +
                '            "retrievalDescriptor": {\n' +
                '              "logicLocation": "model://redScript"\n' +
                '            }\n' +
                '          }\n' +
                '        ]\n' +
                '      }\n' +
                '    ]\n' +
                '  }\n' +
                '}'
    }

    def nameGenerator() {
        Stub(EntitySetNameGenerator) {
            generate("IntegrationObject") >> "IntegrationObjects"
            generate("InboundChannelConfiguration") >> "InboundChannelConfigurations"
            generate("WebhookConfiguration") >> "WebhookConfigurations"
            generate("Script") >> "Scripts"
        }
    }

    def scriptsRepository() {
        Stub(ModelScriptsRepository) {
            findActiveScript(_) >> Stub(ScriptModel)
        }
    }

}
