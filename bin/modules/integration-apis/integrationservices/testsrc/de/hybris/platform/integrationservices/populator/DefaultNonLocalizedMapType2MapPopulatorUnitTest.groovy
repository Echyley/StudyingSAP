/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultNonLocalizedMapType2MapPopulatorUnitTest extends JUnitPlatformSpecification {

    private static final String KEY = "key"
    private static final String VALUE = "value"
    private static final String MAP_KEY = "map_key"
    private static final String ATTR_KEY = "attr_key"
    private static final String ATTR_VALUE = "attr_value"
    private Map<String, Object> targetMap = [:]

    def populator = new DefaultNonLocalizedMapType2MapPopulator()

    @Test
    def "populates a map from a not localized map attribute"() {
        when:
        populator.populate(conversionContext(), targetMap)

        then:
        targetMap == [(MAP_KEY): [[(KEY): ATTR_KEY, (VALUE): ATTR_VALUE]]]
    }

    @Test
    @Unroll
    def "populates a map from a not localized map attribute with #description map key"() {
        when:
        populator.populate(conversionContext(mapKey: mapKey), targetMap)

        then:
        targetMap == result

        where:
        result                                                          | mapKey            | description
        [:]                                                             | null              | "a null"
        [(StringUtils.EMPTY): [[(KEY): ATTR_KEY, (VALUE): ATTR_VALUE]]] | StringUtils.EMPTY | "an empty"
    }

    @Test
    @Unroll
    def "populates a map from a not localized map attribute with #description attribute key"() {
        when:
        populator.populate(conversionContext(key: attributeKey), targetMap)

        then:
        targetMap == result

        where:
        result                                                         | attributeKey      | description
        [(MAP_KEY): []]                                                | null              | "a null"
        [(MAP_KEY): [[(KEY): StringUtils.EMPTY, (VALUE): ATTR_VALUE]]] | StringUtils.EMPTY | "an empty"
    }

    @Test
    @Unroll
    def "does not populate a map from a #decription attribute"() {
        when:
        populator.populate(conversionContext(isLocalizedType: isLocalized, isMapType: isMapType), targetMap)

        then:
        targetMap == [:]

        where:
        isLocalized | isMapType | decription
        true        | true      | "localized map"
        true        | false     | "localized not map"
        false       | false     | "not localized not map"
    }

    def conversionContext(final Map params = [:]) {
        def pojo = Stub(Object)
        Stub(ItemToMapConversionContext) {
            getPayloadObject() >> pojo
            getTypeDescriptor() >>
                    Stub(TypeDescriptor) {
                        getAttributes() >> [Stub(TypeAttributeDescriptor) {
                            isLocalized() >> params.get('isLocalizedType', false)
                            isMap() >> params.get('isMapType', true)
                            getAttributeName() >> params.get('mapKey', MAP_KEY)
                            accessor() >> Stub(AttributeValueAccessor) {
                                getValue(pojo) >> [(params.get('key', ATTR_KEY)): params.get('value', ATTR_VALUE)]
                            }
                        }]
                    }
        }

    }

}
