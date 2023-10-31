/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class MapClassTypeDescriptorUnitTest extends JUnitPlatformSpecification {
    private static final String DEFAULT_IO_CODE = 'IO'
    private static final String DEFAULT_ITEM_CODE = 'Integer2StringMap'
    private static final Integer KEY = 9
    private static final String VALUE = 'Any Value'

    def pojoIntrospector = Stub(PojoIntrospector) {
        isMap() >> true
        getIntegrationObjectCode() >> DEFAULT_IO_CODE
        getReturnType() >> Optional.of(Map<Integer, String>)
        getMapKeyType() >> KEY.class
        getMapValueType() >> VALUE.class
    }
    def mapClassTypeDescriptor = new MapClassTypeDescriptor(pojoIntrospector)

    @Test
    def "create called with null argument throws an exception"() {
        when:
        MapClassTypeDescriptor.create null

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Non-null PojoIntrospector is required'
    }

    @Test
    def "create when attributeDescriptor has attributeType that is not an instanceof MapTypeModel throws exception"() {
        given:
        def introspector = Stub(PojoIntrospector) {
            isMap() >> false
        }

        when:
        MapClassTypeDescriptor.create introspector

        then:
        def e = thrown IllegalArgumentException
        e.message == 'PojoIntrospector must be for a Map type attribute'
    }

    @Test
    def "getIntegrationObjectCode returns code from the pojo introspector"() {
        expect:
        mapClassTypeDescriptor.getIntegrationObjectCode() == DEFAULT_IO_CODE
    }

    @Test
    def "getItemCode returns item code from the pojo introspector"() {
        expect:
        mapClassTypeDescriptor.getItemCode() == DEFAULT_ITEM_CODE
    }

    @Test
    def "getTypeCode always returns an empty string"() {
        expect:
        mapClassTypeDescriptor.getTypeCode() == ""
    }

    @Test
    def "MapClassTypeDescriptor.#method is always false"() {
        expect:
        !mapClassTypeDescriptor."$method"()

        where:
        method << ['isPrimitive', 'isEnumeration', 'isAbstract', 'isRoot', 'hasPathToRoot']
    }

    @Test
    def "getPathsToRoot() is always empty"() {
        expect:
        mapClassTypeDescriptor.getPathsToRoot().isEmpty()
    }

    @Test
    def 'pathFrom() is always empty'() {
        given:
        def source = Stub TypeDescriptor

        expect:
        mapClassTypeDescriptor.pathFrom(source).empty
    }

    @Test
    def "getAttributes() is always empty"() {
        expect:
        mapClassTypeDescriptor.getAttributes().isEmpty()
    }

    @Test
    def "getAttribute(String) always returns empty result"() {
        expect:
        mapClassTypeDescriptor.getAttribute('anyAttributeName').empty
    }

    @Test
    def "isInstance() returns #bool when the object is a #container"() {
        expect:
        mapClassTypeDescriptor.isInstance(new HashMap())
    }

    @Test
    def "isInstance() returns false when the object is not Map"() {
        expect:
        !mapClassTypeDescriptor.isInstance(new HashSet())
    }

    @Test
    def "getKeyDescriptor() always returns a new NullKeyDescriptor"() {
        when:
        def descriptor = mapClassTypeDescriptor.getKeyDescriptor()

        then:
        descriptor instanceof NullKeyDescriptor
    }
}
