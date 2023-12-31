/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.servicelayer.i18n.I18NService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultMapType2MapPopulatorUnitTest extends JUnitPlatformSpecification {

    private static final def ATTR_NAME = "codex"
    private static final def LOCALIZED_VALUE = "france text"

    private def i18NService = Stub(I18NService)
    def populator = new DefaultMapType2MapPopulator(i18NService: i18NService)

    private Map<String, Object> targetMap = [:]
    private def attributeDescriptor = Stub(TypeAttributeDescriptor) {
        getAttributeName() >> ATTR_NAME
    }

    private def pojo = new Object()

    @Test
    def "populates current session local value to map"() {
        given:
        mapAttributeIsLocalized()
        and:
        localizedValueIsPresent()

        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap == [(ATTR_NAME): LOCALIZED_VALUE]
    }

    @Test
    def "populates empty string to map when localized value is empty string"() {
        given:
        mapAttributeIsLocalized()
        and:
        localizedValueIsEmptyString()

        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap == [(ATTR_NAME): ""]
    }

    @Test
    def "does not populate to map when no localized value found"() {
        given:
        mapAttributeIsNotLocalized()

        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate to map when localized value is null"() {
        given:
        mapAttributeIsLocalized()
        and:
        localizedValueIsNull()

        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap.isEmpty()
    }

    @Test
    def "should not populate localized to map when not map attribute"() {
        given:
        localizedAttributeIsNotMap()

        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap.isEmpty()
    }

    private ItemToMapConversionContext conversionContext() {
        attributeDescriptor.accessor() >> attributeValueGetter()
        Stub(ItemToMapConversionContext) {
            getPayloadObject() >> pojo
            getTypeDescriptor() >>
                    Stub(TypeDescriptor) {
                        getAttributes() >> [attributeDescriptor]
                    }
        }
    }

    def mapAttributeIsLocalized() {
        attributeDescriptor.isLocalized() >> true
        attributeDescriptor.isMap() >> true
    }

    def mapAttributeIsNotLocalized() {
        attributeDescriptor.isLocalized() >> false
        attributeDescriptor.isMap() >> true
    }

    def localizedAttributeIsNotMap() {
        attributeDescriptor.isLocalized() >> true
        attributeDescriptor.isMap() >> false
    }

    def localizedValueIsPresent() {
        i18NService.getCurrentLocale() >> Locale.FRENCH
    }

    def localizedValueIsNull() {
        i18NService.getCurrentLocale() >> Locale.GERMAN
    }

    def localizedValueIsEmptyString() {
        i18NService.getCurrentLocale() >> Locale.ENGLISH
    }

    def attributeValueGetter() {
        Stub(AttributeValueAccessor) {
            getValue(pojo, Locale.ENGLISH) >> ""
            getValue(pojo, Locale.FRENCH) >> LOCALIZED_VALUE
            getValue(pojo, Locale.GERMAN) >> null
        }
    }
}
