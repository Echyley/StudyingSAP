/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import spock.lang.Unroll

@UnitTest
class DefaultExportConfigurationServiceUnitTest extends JUnitPlatformSpecification {

    def defaultExportConfigurationService = new DefaultExportConfigurationService()

    @Test
    def "verify that all the converters are called"() {
        given: "a converter"
        def converter = Mock(Converter) {
            convert(_) >> { [Stub(ExportEntity)] as Set<ExportEntity> }
        }
        and: "a runtime converter"
        def runtimeConverter = converter
        defaultExportConfigurationService.converters = [converter, runtimeConverter]

        when:
        defaultExportConfigurationService.generateExportConfiguration(Stub(ConfigurationBundleEntity))

        then: "every decorator is called once"
        1 * converter.convert(_)
        1 * runtimeConverter.convert(_)
    }

    @Test
    def "verify the size of the exported entity"() {
        given: "two converter"
        def convertedEntity = [Stub(ExportEntity)] as Set<ExportEntity>
        defaultExportConfigurationService.converters = [converter(convertedEntity), runtimeConverter(convertedEntity)]
        and: "two decorators"
        defaultExportConfigurationService.decorators = [Stub(DefaultIntegrationObjectDecorator), Stub(DefaultScriptDecorator)]

        when:
        def configuration = defaultExportConfigurationService.generateExportConfiguration(Stub(ConfigurationBundleEntity))

        then: "every decorator is called once"
        configuration.size() == 1
        configuration == convertedEntity
    }

    @Test
    @Unroll
    def "when the converted entity #convertedEntity each decorator is called #times time"() {
        given: "two converter"
        defaultExportConfigurationService.converters = [converter(convertedEntity), runtimeConverter(convertedEntity)]
        and: "two decorators"
        def integrationObjectDecorator = Mock(DefaultIntegrationObjectDecorator)
        def scriptDecorator = Mock(DefaultScriptDecorator)
        defaultExportConfigurationService.decorators = [integrationObjectDecorator, scriptDecorator]

        when:
        def exportEntities = defaultExportConfigurationService.generateExportConfiguration(Stub(ConfigurationBundleEntity))

        then:
        exportEntities.size() == size
        times * integrationObjectDecorator.decorate(_)
        times * scriptDecorator.decorate(_)

        where:
        convertedEntity      | size | times
        [Stub(ExportEntity)] | 1    | 1
        []                   | 0    | 0
    }

    def runtimeConverter(def convertedEntity){
       converter(convertedEntity)
    }

    def converter(def convertedEntity){
        Stub(Converter) {
            convert(_) >> { convertedEntity as Set<ExportEntity> }
        }
    }

}

