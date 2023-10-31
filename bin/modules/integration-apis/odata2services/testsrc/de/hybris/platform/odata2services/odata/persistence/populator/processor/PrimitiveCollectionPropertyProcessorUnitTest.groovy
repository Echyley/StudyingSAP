/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.*
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PrimitiveCollectionPropertyProcessorUnitTest extends JUnitPlatformSpecification {
    private static final def TEST_PROPERTY = "testProperty"
    private static final def INTEGRATION_OBJECT_CODE = "TestIntegrationObject"
    private static final def TEST_TYPE = "TestType"
    private def IO_ITEM = Stub IntegrationObjectItemModel
    private ItemModel ITEM_MODEL = Stub(ItemModel) {
        getItemtype() >> TEST_TYPE
    }

    def valueAccessor = Stub AttributeValueAccessor
    def itemTypeDescriptorService = Stub(ItemTypeDescriptorService) {
        getTypeDescriptorByTypeCode(INTEGRATION_OBJECT_CODE, TEST_TYPE) >> Optional.of(typeDescriptor(applicableAttribute()))
    }
    def processor = new PrimitiveCollectionPropertyProcessor(
            itemTypeDescriptorService: itemTypeDescriptorService
    )

    @Test
    def "processEntity() returns collection of primitives as feed with a list of ODataEntry"() {
        given: 'an empty ODataEntry to be populated'
        def entry = oDataEntry()
        and: 'attribute descriptor returns a list of URL strings'
        def valueList = ["/this/url", "/another/url"]
        valueAccessor.getValue(ITEM_MODEL) >> valueList
        and:
        def request = conversionRequest()
        request.isPropertyValueShouldBeConverted(TEST_PROPERTY) >> true

        when:
        processor.processEntity entry, request

        then:
        entry.properties[TEST_PROPERTY].entries[0].properties["value"] == "/this/url"
        entry.properties[TEST_PROPERTY].entries[1].properties["value"] == "/another/url"
    }

    @Test
    def "processEntity() does not process an unsupported property"() {
        given:
        def request = conversionRequest()
        request.isPropertyValueShouldBeConverted(TEST_PROPERTY) >> false
        def entry = oDataEntry()

        when:
        processor.processEntity entry, request

        then:
        entry.properties.isEmpty()
    }

    @Test
    @Unroll
    def "processEntity() does not process an attribute when it has collection=#col and primitive=#prim"() {
        given: 'attribute descriptor is not applicable'
        processor.descriptorFactory >> Stub(DescriptorFactory) {
            def notApplicableAttribute = Stub(TypeAttributeDescriptor) {
                isCollection() >> col
                isPrimitive() >> prim
            }
            createItemTypeDescriptor(IO_ITEM) >> Stub(TypeDescriptor) {
                getAttribute(TEST_PROPERTY) >> Optional.of(notApplicableAttribute)
            }
        }
        def request = conversionRequest()
        request.isPropertyValueShouldBeConverted(TEST_PROPERTY) >> true
        def entry = oDataEntry()

        when:
        processor.processEntity entry, request

        then:
        entry.properties.isEmpty()

        where:
        col   | prim
        false | false
        true  | false
        false | true
    }

    def typeDescriptor(def attribute) {
        Stub(TypeDescriptor) {
            getAttribute(TEST_PROPERTY) >> Optional.of(attribute)
        }
    }

    def applicableAttribute() {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> true
            isCollection() >> true
            isReadable() >> true
            accessor() >> valueAccessor
        }
    }

    def conversionRequest() {
        Stub(ItemConversionRequest) {
            getIntegrationObjectCode() >> INTEGRATION_OBJECT_CODE
            getValue() >> ITEM_MODEL
            getAllPropertyNames() >> [TEST_PROPERTY]
        }
    }

    private static ODataEntryImpl oDataEntry(def values = [:]) {
        new ODataEntryImpl(values, new MediaMetadataImpl(), new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl())
    }
}
