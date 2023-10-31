/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultRuntimeAttributeConverterUnitTest extends JUnitPlatformSpecification {

    private static final String EXPORT_URL = "{{hostUrl}}/odata2webservices/RuntimeAttributeService/AttributeDescriptors"
    private static final String RUNTIME_ATTRIBUTE_SERVICE = "RuntimeAttributeService"
    private static final String ATTRIBUTE_DESCRIPTORS = "AttributeDescriptors"

    def searchService = Stub(ExportConfigurationSearchService)
    def runtimeAttributeConverter = new DefaultRuntimeAttributeConverter(
            descriptorFactory(),
            nameGenerator(),
            integrationObjectService(),
            searchService,
            conversionService()
    )

    @Test
    def "convert an integration object with runtime attributes"() {
        given: "an integration object with runtime attributes"
        searchService.findRuntimeAttributeDescriptors(_ as ConfigurationBundleEntity) >> [Stub(AttributeDescriptorModel)]
        when:
        def exportEntity = runtimeAttributeConverter.convert(Stub(ConfigurationBundleEntity))
        then:
        exportEntity.size() == 1
        with(exportEntity.first()) {
            requestUrl == EXPORT_URL
            requestBodies.size() == 1
            requestBodies.first() == '{\n    "key1": "value1",\n    "key2": "value2"\n}'
        }
    }

    @Test
    def "convert an integration object without runtime attributes"() {
        given: "an integration object without runtime attributes"
        searchService.findRuntimeAttributeDescriptors(_ as ConfigurationBundleEntity) >> []
        when:
        def exportEntity = runtimeAttributeConverter.convert(Stub(ConfigurationBundleEntity))
        then:
        exportEntity.isEmpty()
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

    def nameGenerator() {
        Stub(EntitySetNameGenerator) {
            generate(_) >> ATTRIBUTE_DESCRIPTORS
        }
    }

    def integrationObjectService() {
        Stub(IntegrationObjectService) {
            findIntegrationObject(RUNTIME_ATTRIBUTE_SERVICE) >> Stub(IntegrationObjectModel) {
                getCode() >> RUNTIME_ATTRIBUTE_SERVICE
            }
        }
    }

    def conversionService() {
        Stub(IntegrationObjectConversionService) {
            convert(_ as ItemModel, _ as IntegrationObjectDescriptor) >> [key1: 'value1', key2: 'value2']
        }
    }

}
