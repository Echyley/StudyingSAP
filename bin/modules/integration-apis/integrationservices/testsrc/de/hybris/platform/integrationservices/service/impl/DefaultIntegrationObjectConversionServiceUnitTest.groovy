/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.service.IntegrationObjectAndItemMismatchException
import de.hybris.platform.integrationservices.service.IntegrationObjectNotFoundException
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.servicelayer.dto.converter.Converter
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultIntegrationObjectConversionServiceUnitTest extends JUnitPlatformSpecification {
    private static final String INTEGRATION_OBJECT = "ProductIntegrationObject"
    private static final def CONVERSION_RESULT = [:]
    private static final def ITEM_MODEL = new ItemModel()

    def integrationObjectService = Stub IntegrationObjectService
    def descriptorFactory = Stub DescriptorFactory
    def conversionService = new DefaultIntegrationObjectConversionService(
            integrationObjectService: integrationObjectService, descriptorFactory: descriptorFactory)

    def setup() {
        conversionService.itemToIntegrationObjectMapConverter = Stub(Converter) {
            convert(_, _) >> CONVERSION_RESULT
        }
    }

    @Test
    def "populate result map when item matches an integration object item type"() {
        when:
        def map = conversionService.convert new ItemToMapConversionContext(ITEM_MODEL, typeDescriptor(ITEM_MODEL))

        then:
        map == CONVERSION_RESULT
    }

    @Test
    def "throws exception when item model is null"() {
        when:
        conversionService.convert(null, INTEGRATION_OBJECT)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def "throws exception when specified integration object is not found"() {
        given: 'the specified integration object does not exist'
        integrationObjectService.findIntegrationObject(INTEGRATION_OBJECT) >> { throw new ModelNotFoundException('') }

        when:
        conversionService.convert(ITEM_MODEL, INTEGRATION_OBJECT)

        then:
        def e = thrown IntegrationObjectNotFoundException
        e.message.contains INTEGRATION_OBJECT
        e.cause instanceof ModelNotFoundException
    }

    @Test
    def "throws exception when data item is not present in the integration object model"() {
        given: "a data item"
        and: "an integration object with an integration object item for type other than the data item's type"
        def ioModel = Stub IntegrationObjectModel
        def ioDescriptor = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM_MODEL) >> Optional.empty()
        }
        integrationObjectService.findIntegrationObject(INTEGRATION_OBJECT) >> ioModel
        descriptorFactory.createIntegrationObjectDescriptor(ioModel) >> ioDescriptor

        when:
        conversionService.convert(ITEM_MODEL, INTEGRATION_OBJECT)

        then:
        def e = thrown IntegrationObjectAndItemMismatchException
        e.dataItem.is ITEM_MODEL
        e.integrationObject.is ioDescriptor
        e.message.contains ioDescriptor.toString()
        e.message.contains ITEM_MODEL.toString()
    }

    @Test
    def "context conversion places the conversion result into the context"() {
        ItemToMapConversionContext context = Mock() {
            getTypeDescriptor() >> Stub(TypeDescriptor)
        }

        when:
        def result = conversionService.convert context

        then:
        result.is CONVERSION_RESULT
        1 * context.setConversionResult([:])
    }

    @Test
    def "context conversion returns matching conversion result in the context"() {
        given:
        def contextConversionResult = [source: 'context']
        def context = Stub(ItemToMapConversionContext) {
            getConversionResult() >> contextConversionResult
            getTypeDescriptor() >> Stub(IntegrationObjectItemModel)
        }

        when:
        def result = conversionService.convert context

        then:
        result == contextConversionResult
    }

    @Test
    def "populate result map when item descriptor matches the integration object item descriptor"() {
        given: "a data item"
        and: "a matching integration object descriptor"
        def ioDescriptor = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM_MODEL) >> Optional.of(typeDescriptor(ITEM_MODEL))
        }

        when:
        def map = conversionService.convert(ITEM_MODEL, ioDescriptor)

        then:
        map == CONVERSION_RESULT
    }

    @Test
    def "throws exception when item descriptor does not match the integration object item descriptor"() {
        given: "a data item"
        and: "a non-matching integration object item descriptor"
        def ioDescriptor = Stub(IntegrationObjectDescriptor) {
            getTypeDescriptor(ITEM_MODEL) >> Optional.empty()
        }

        when:
        conversionService.convert(ITEM_MODEL, ioDescriptor)

        then:
        def e = thrown IntegrationObjectAndItemMismatchException
        e.dataItem.is ITEM_MODEL
        e.integrationObject.is ioDescriptor
        e.message.contains ioDescriptor.toString()
        e.message.contains ITEM_MODEL.toString()
    }

    @Test
    @Unroll
    def "throws exception because #msg"() {
        when:
        conversionService.convert(itemModel, ioDescriptor as IntegrationObjectDescriptor)

        then:
        def ex = thrown IllegalArgumentException
        ex.message == msg

        where:
        itemModel  | ioDescriptor                      | msg
        null       | Stub(IntegrationObjectDescriptor) | 'ItemModel cannot be null'
        ITEM_MODEL | null                              | 'IntegrationObjectDescriptor cannot be null'
    }

    private TypeDescriptor typeDescriptor(ItemModel item) {
        Stub(TypeDescriptor) {
            isInstance(item) >> true
        }
    }
}
