/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class AttributePathFinderUnitTest extends JUnitPlatformSpecification {
    private static final def CONTEXT_IO_CODE = 'TestIO'
    private static final def CONTEXT_ITEM_CODE = 'TestItem'
    @Shared
    private DESTINATION = typeDescriptor()

    def pathFinder = new AttributePathFinder()

    @Test
    @Unroll
    def "path is empty when #param descriptor is null"() {
        expect:
        pathFinder.findAllPaths(source, destination).empty

        where:
        source           | destination      | param
        null             | typeDescriptor() | 'source'
        typeDescriptor() | null             | 'destination'
    }

    @Test
    def 'path is empty when source descriptor is for a different integration object'() {
        given:
        def destination = typeDescriptor('IO_2')
        def source = typeDescriptor('IO_1', [attribute(destination)])

        expect:
        pathFinder.findAllPaths(source, destination).empty
    }

    @Test
    @Unroll
    def "path is empty when the source descriptor has #condition"() {
        given:
        def source = typeDescriptor(attributes)

        expect:
        pathFinder.findAllPaths(source, DESTINATION).empty

        where:
        condition                              | attributes
        'no attributes'                        | []
        'only primitive attributes'            | [primitiveAttribute('one'), primitiveAttribute('two')]
        'only map attributes'                  | [mapAttribute('one'), mapAttribute('two')]
        'no attributes leading to destination' | [attribute('one', typeDescriptor()), attribute('two', typeDescriptor())]
    }

    @Test
    def 'path has 0 length when source and destination is the same'() {
        given:
        def descriptor = typeDescriptor()

        when:
        def paths = pathFinder.findAllPaths(descriptor, descriptor)

        then:
        !paths.empty
        paths[0].length() == 0
    }

    @Test
    def 'finds all possible paths to the destination'() {
        given:
        def secondHop = typeDescriptor([
                attribute('miss', typeDescriptor()),
                attribute('hit', DESTINATION)])
        def firstHop = typeDescriptor([
                attribute('miss', typeDescriptor()),
                attribute('hit', DESTINATION),
                attribute('more', secondHop)])
        def source = typeDescriptor([
                attribute('miss', typeDescriptor()),
                attribute('hit', DESTINATION),
                attribute('more', firstHop)])

        when:
        def paths = pathFinder.findAllPaths(source, DESTINATION)

        then:
        paths.size() == 3
    }

    @Test
    def 'all found paths are ordered from shorters to the longest'() {
        given:
        def secondHop = typeDescriptor([
                attribute('hit', DESTINATION)])
        def firstHop = typeDescriptor([
                attribute('hit', DESTINATION),
                attribute('next', secondHop)])
        def source = typeDescriptor([
                attribute('hit', DESTINATION),
                attribute('next', firstHop)])

        when:
        def paths = pathFinder.findAllPaths(source, DESTINATION)

        then:
        paths.collect({ it.length() }) == [1, 2, 3]
    }

    @Test
    def 'finding path guards against the loops'() {
        given:
        def other = typeDescriptor()
        def source = typeDescriptor([attribute(other)])
        other.getAttributes() << attribute(source)

        expect:
        pathFinder.findAllPaths(source, DESTINATION).empty
    }

    TypeDescriptor typeDescriptor(List attributes) {
        typeDescriptor(CONTEXT_IO_CODE, attributes)
    }

    TypeDescriptor typeDescriptor(String ioCode, List attributes) {
        typeDescriptor(ioCode, CONTEXT_ITEM_CODE, attributes)
    }

    TypeDescriptor typeDescriptor(String ioCode = CONTEXT_IO_CODE,
                                  String itemCode = CONTEXT_ITEM_CODE,
                                  List attributes = []) {
        Stub(TypeDescriptor) {
            getIntegrationObjectCode() >> ioCode
            getItemCode() >> itemCode
            getAttributes() >> attributes
        }
    }

    TypeAttributeDescriptor attribute(TypeDescriptor type) {
        attribute('attribute', type)
    }

    TypeAttributeDescriptor attribute(String name, TypeDescriptor type = DESTINATION) {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> false
            isMap() >> false
            getAttributeName() >> name
            getAttributeType() >> type
        }
    }

    private TypeAttributeDescriptor primitiveAttribute(String name) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            isPrimitive() >> true
        }
    }

    private TypeAttributeDescriptor mapAttribute(String name) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            isMap() >> true
        }
    }
}
