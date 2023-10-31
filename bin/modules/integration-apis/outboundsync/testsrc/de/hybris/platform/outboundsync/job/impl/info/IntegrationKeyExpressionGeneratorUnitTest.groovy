/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.CircularKeyReferenceException
import de.hybris.platform.integrationservices.model.KeyAttribute
import de.hybris.platform.integrationservices.model.KeyDescriptor
import de.hybris.platform.integrationservices.model.ReferencePath
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class IntegrationKeyExpressionGeneratorUnitTest extends JUnitPlatformSpecification {
    @Shared
    def STRING = type 'java.lang.String'
    @Shared
    def DATE = type 'java.util.Date'

    def generator = new IntegrationKeyExpressionGenerator()

    @Test
    @Unroll
    def "expression is null when provided item type #condition"() {
        expect:
        generator.generateExpression(type) == null

        where:
        type                 | condition
        Stub(TypeDescriptor) | 'has no key attribute'
        null                 | 'is null'
    }

    @Test
    def 'expression is null when the context item type and the key attribute are not related'() {
        given: 'there is no path from the item to the key attribute'
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([attribute(item, 'someKey', null)])

        expect:
        generator.generateExpression(item) == null
    }

    @Test
    def 'expression has key attribute qualifier when item type has a single simple key attribute'() {
        given:
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([attribute(item, 'code', '')])

        expect:
        generator.generateExpression(item) == '#{code}'
    }

    @Test
    def 'expression contains paths to all primitive key attribute in all nested key attribute types'() {
        given:
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([
                attribute(item, 'id', 'catalogVersion.catalog'),
                attribute(item, 'version', 'catalogVersion'),
                attribute(item, 'code', 'unit'),
                attribute(item, 'code', '')])

        when:
        def expr = generator.generateExpression(item)

        then:
        expr.contains '#{catalogVersion?.catalog?.id}'
        expr.contains '#{catalogVersion?.version}'
        expr.contains '#{code}'
        expr.contains '#{unit?.code}'
    }

    @Test
    def 'expression sorts same type key attributes in their attribute name order'() {
        given: 'attribute names and qualifiers are in opposite order'
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([
                attribute(item: item, name: 'attr2', qualifier: 'aSecond', path: ''),
                attribute(item: item, name: 'attr1', qualifier: 'beFirst', path: '')])

        expect: 'the expression parts are in order of the attribute name'
        generator.generateExpression(item) == '#{beFirst}|#{aSecond}'
    }

    @Test
    def 'expression sorts complex key attributes in order of their type code'() {
        given:
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([
                attribute(item: item, itemType: 'A', name: 'code', path: 'aRef'),
                attribute(item: item, itemType: 'C', name: 'code', path: 'aRef.cRef'),
                attribute(item: item, itemType: 'B', name: 'code', path: '')])

        expect:
        generator.generateExpression(item) == '#{aRef?.code}|#{code}|#{aRef?.cRef?.code}'
    }

    @Test
    def 'expression is null when integration key attributes form a loop'() {
        given:
        def item = Stub(TypeDescriptor) {
            getKeyDescriptor() >> {
                throw new CircularKeyReferenceException(Stub(TypeAttributeDescriptor))
            }
        }

        expect:
        generator.generateExpression(item) == null
    }

    @Test
    def 'expression converts key Date attributes to epoch time'() {
        given:
        def item = Stub TypeDescriptor
        item.getKeyDescriptor() >> keyDescriptor([attribute(item, 'dateAttribute', '', DATE)])

        expect:
        generator.generateExpression(item) == '#{dateAttribute?.time}'
    }

    private KeyDescriptor keyDescriptor(List<TypeAttributeDescriptor> keys) {
        Stub(KeyDescriptor) {
            getKeyAttributes() >> keys.collect { asKeyAttribute(it) }
        }
    }

    private TypeAttributeDescriptor attribute(TypeDescriptor type, String name, String path, TypeDescriptor attrType = STRING) {
        attribute(item: type, name: name, path: path, attrType: attrType)
    }

    private TypeAttributeDescriptor attribute(Map params) {
        def item = params['item'] as TypeDescriptor
        def itemTypeCode = params['itemType'] as String
        def attrName = params['name'] as String
        def qualifier = params['qualifier'] as String
        def path = params['path'] as String
        def attrType = params['attrType'] as TypeDescriptor

        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attrName
            getQualifier() >> (qualifier ?: attrName)
            getTypeDescriptor() >> Stub(TypeDescriptor) {
                getItemCode() >> itemTypeCode
                pathFrom(item) >> (path == null ? [] : [refPath(path)])
            }
            getAttributeType() >> (attrType ?: STRING)
        }
    }

    private TypeDescriptor type(String code) {
        Stub(TypeDescriptor) {
            getTypeCode() >> code
        }
    }

    private ReferencePath refPath(String p) {
        Stub(ReferencePath) {
            toPropertyPath() >> p
            length() >> (p == '' ? 0 : p.count('.') + 1)
        }
    }

    private KeyAttribute asKeyAttribute(TypeAttributeDescriptor descriptor) {
        Stub(KeyAttribute) {
            getAttributeDescriptor() >> descriptor
        }
    }
}
