/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationLocalizationService
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.apache.olingo.odata2.core.edm.provider.EdmNavigationPropertyImplProv
import org.apache.olingo.odata2.core.edm.provider.EdmSimplePropertyImplProv
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl
import org.junit.Rule
import org.junit.Test
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModelUtils.falseIfNull

@UnitTest
class LocalizedAttributesPropertyProcessorUnitTest extends JUnitPlatformSpecification {
    private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes"
    private static final String ITEM_TYPE = "itemType"
    private static final String IO_CODE = "objectCode"

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def oDataEntry = Stub(ODataEntryImpl)

    def entityType = Stub(EdmEntityType) {
        getProperty(_ as String) >> Stub(EdmNavigationPropertyImplProv)
        getName() >> ITEM_TYPE
    }
    def item = Stub(ItemModel)

    def integrationLocalizationService = Stub(IntegrationLocalizationService) {
        getSupportedLocale('en') >> Locale.ENGLISH
        getSupportedLocale('de') >> Locale.GERMAN
        getAllSupportedLocales() >> [Locale.ENGLISH, Locale.GERMAN]
    }

    def itemTypeDescriptorService = Stub(ItemTypeDescriptorService)

    def localizedAttributesPropertyProcessor = Spy(LocalizedAttributesPropertyProcessor)

    def setup() {
        localizedAttributesPropertyProcessor.setLocalizationService(integrationLocalizationService)
        localizedAttributesPropertyProcessor.setItemTypeDescriptorService(itemTypeDescriptorService)
    }

    @Test
    def 'get the integration localization service in the application context when integration localization service is not injected'() {
        given: 'integration localization service is not injected'
        localizedAttributesPropertyProcessor.localizationService = null
        and: 'integration localization service is present in the application context'
        applicationContext.addBean 'integrationLocalizationService', Stub(IntegrationLocalizationService)

        expect:
        localizedAttributesPropertyProcessor.getIntegrationLocalizationService() != null
    }

    @Test
    def "processEntity ignores items with null localizedAttributes"() {
        setup:
        entityType.getProperty(_) >> null

        when:
        localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

        then:
        oDataEntry.properties.isEmpty()
    }

    @Test
    def "processEntity ignores items if localizedAttributes not an instanceof EdmNavigationProperty"() {
        setup:
        entityType.getProperty(_) >> Stub(EdmSimplePropertyImplProv)

        when:
        localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

        then:
        oDataEntry.properties.isEmpty()
    }

    @Test
    def "processEntity() ignores non-localizable property"() {
        setup:
        def propertyName = 'id'
        entityType.getPropertyNames() >> [propertyName]

        def typeAttributeDescriptor = typeAttributeDescriptor(propertyName, [localized: false, primitive: true, readable: true])
        def itemTypeDescriptor = Stub(TypeDescriptor) {
            getAttribute(propertyName) >> Optional.of(typeAttributeDescriptor)
            getAttributes() >> [typeAttributeDescriptor]
        }
        itemTypeDescriptorService.getTypeDescriptorByTypeCode(IO_CODE, ITEM_TYPE) >> Optional.of(itemTypeDescriptor)
        oDataEntry.getProperties() >> [:]

        when:
        localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

        then:
        def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
        feed.entries.size() == 0
    }

    @Test
    @Unroll
    def "processEntity localizable property '#name' with value #value"() {
        setup:
        entityType.getPropertyNames() >> [name]
        def nameAttribute = applicableDescriptor(name)
        givenTypeDescriptorWithAttributesIsFound(nameAttribute)
        oDataEntry.getProperties() >> [:]

        when:
        localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

        then:
        def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
        feed.entries.size() == 1

        where:
        name             | value
        "stringProp"     | "stringValue"
        "booleanProp"    | Boolean.TRUE
        "byteProp"       | Byte.MAX_VALUE
        "characterProp"  | Character.MAX_VALUE
        "doubleProp"     | Double.MAX_VALUE
        "floatProp"      | Float.MAX_VALUE
        "integerProp"    | Integer.MAX_VALUE
        "longProp"       | Long.MAX_VALUE
        "shortProp"      | Short.MAX_VALUE
        "bigDecimalProp" | BigDecimal.ONE
        "bigIntegerProp" | BigInteger.ONE
        "dateProp"       | new Date()
    }

    @Test
    @Unroll
    def "processEntity() sets #expected localizable properties when expand is #expandCond and navigationSegments is #navigationCond"() {
        setup:
        def propertyName = 'name'
        def conversionRequest = Stub(ItemConversionRequest) {
            getEntityType() >> entityType
            getValue() >> item
            getOptions() >> Stub(ConversionOptions) {
                isNavigationSegmentPresent() >> navigations
                isExpandPresent() >> expand
            }
            getIntegrationObjectCode() >> IO_CODE
        }
        entityType.getPropertyNames() >> [propertyName]
        def attribute = applicableDescriptor(propertyName)
        givenTypeDescriptorWithAttributesIsFound(attribute)
        oDataEntry.getProperties() >> [:]

        when:
        localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest)

        then:
        def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
        feed.entries.size() == expected

        where:
        expandCond    | expand | navigationCond | navigations | expected
        'not present' | false  | 'not present'  | false       | 0
        'present'     | true   | 'not present'  | false       | 1
        'not present' | false  | 'present'      | true        | 1
        'present'     | true   | 'present'      | true        | 1
    }

    def givenTypeDescriptorWithAttributesIsFound(final TypeAttributeDescriptor... typeAttributeDescriptors) {
        def itemDescriptor = Stub(ItemTypeDescriptor)
        typeAttributeDescriptors.each {
            itemDescriptor.getAttribute(it.attributeName) >> Optional.of(it)
        }
        itemDescriptor.getAttributes() >> typeAttributeDescriptors

        itemTypeDescriptorService.getTypeDescriptorByTypeCode(IO_CODE, ITEM_TYPE) >> Optional.of(itemDescriptor)
    }

    private TypeAttributeDescriptor applicableDescriptor(String name) {
        typeAttributeDescriptor(name, [localized: true, primitive: true, readable: true])
    }

    private TypeAttributeDescriptor typeAttributeDescriptor(String attrName, Map<String, Boolean> properties) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attrName
            isLocalized() >> falseIfNull(properties['localized'])
            isPrimitive() >> falseIfNull(properties['primitive'])
            isReadable() >> falseIfNull(properties['readable'])
            accessor() >> Stub(AttributeValueAccessor) {
                getValues(_, _) >> [(Locale.ENGLISH): "English name"]
            }
        }
    }

    def conversionRequest() {
        Stub(ItemConversionRequest) {
            getEntityType() >> entityType
            getValue() >> item
            getOptions() >> conversionOptions()
            getIntegrationObjectCode() >> IO_CODE
        }
    }

    def conversionOptions() {
        Stub(ConversionOptions) {
            getNavigationSegments() >> [Stub(NavigationSegment)]
            isNavigationSegmentPresent() >> true
        }
    }
}
