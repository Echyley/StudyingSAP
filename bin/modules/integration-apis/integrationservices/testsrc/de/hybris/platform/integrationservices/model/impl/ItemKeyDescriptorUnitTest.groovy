/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.CircularKeyReferenceException
import de.hybris.platform.integrationservices.integrationkey.KeyValue
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class ItemKeyDescriptorUnitTest extends JUnitPlatformSpecification {
    private static final String ITEM_CODE = 'SomeItemCode'

    @Test
    def "cannot be instantiated with null type descriptor"() {
        when:
        ItemKeyDescriptor.create(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'TypeDescriptor'
    }

    @Test
    def "cannot be instantiated with a primitive type descriptor"() {
        given:
        def primitiveType = Stub(TypeDescriptor) {
            isPrimitive() >> true
        }

        when:
        ItemKeyDescriptor.create primitiveType

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'primitive'
    }

    @Test
    @Unroll
    def "calculates empty key when item has no #desc"() {
        given:
        def descriptor = ItemKeyDescriptor.create(model)

        expect:
        descriptor.calculateKey([seq: 1, item: [code: 'abc']]) == new KeyValue()

        where:
        desc             | model
        'attributes'     | item()
        'key attributes' | item([nonUniqueAttribute('seq'), nonUniqueAttribute('item', item())])
    }

    @Test
    def "calculates key when item has multiple simple key attributes only"() {
        given:
        def itemIO = item('Item', [uniqueAttribute('key1'), uniqueAttribute('key2'), nonUniqueAttribute('qty')])
        def descriptor = ItemKeyDescriptor.create(itemIO)

        expect:
        descriptor.calculateKey([key1: 1, key2: 2, qty: 3]) == new KeyValue.Builder()
                .withValue(findAttribute(itemIO, 'key1'), 1)
                .withValue(findAttribute(itemIO, 'key2'), 2)
                .build()
    }

    @Test
    def "calculates key when item has multiple reference key attributes only"() {
        given:
        def companyIO = item 'Company', [uniqueAttribute('name')]
        def positionIO = item 'Position', [uniqueAttribute('code')]
        def descriptor = ItemKeyDescriptor.create(item([
                uniqueAttribute('company', companyIO),
                uniqueAttribute('position', positionIO),
                nonUniqueAttribute('description')]))
        def dataItem = [description: 'Good Job', company: [name: 'SAP'], position: [code: 'dev123']]

        expect:
        descriptor.calculateKey(dataItem) == new KeyValue.Builder()
                .withValue(findAttribute(companyIO, 'name'), 'SAP')
                .withValue(findAttribute(positionIO, 'code'), 'dev123')
                .build()
    }

    @Test
    def "calculate key when reference key attribute is null"() {
        given:
        def companyIO = item 'Company', [uniqueAttribute('name')]
        def descriptor = ItemKeyDescriptor.create(item([
                uniqueAttribute('company', companyIO),
                nonUniqueAttribute('description')]))
        def dataItem = [description: 'Good Job', company: null, position: null]

        expect:
        descriptor.calculateKey(dataItem) == new KeyValue.Builder()
                .withValue(findAttribute(companyIO, 'name'), null)
                .build()
    }

    @Test
    def "calculate key when reference key attribute with nested simple and reference key attributes is null"() {
        given:
        def nestedCompanyRefKeyIO = item('A', [uniqueAttribute('innerId')])
        def companyIO = item 'Company', [uniqueAttribute('name'), uniqueAttribute('loopback', nestedCompanyRefKeyIO)]
        def descriptor = ItemKeyDescriptor.create(item([
                uniqueAttribute('company', companyIO),
                nonUniqueAttribute('description')]))
        def dataItem = [description: 'Good Job', company: null]

        expect:
        descriptor.calculateKey(dataItem) == new KeyValue.Builder()
                .withValue(findAttribute(companyIO, 'name'), null)
                .withValue(findAttribute(nestedCompanyRefKeyIO, 'innerId'), null)
                .build()
    }

    @Test
    def 'calculates key when item key attribute refers another item with multiple simple key attributes'() {
        given:
        def ioAddress = item 'Address', [uniqueAttribute('name'), uniqueAttribute('zipCode')]
        def descriptor = ItemKeyDescriptor.create(item([uniqueAttribute('city', ioAddress), nonUniqueAttribute('description')]))
        def dataItem = [description: 'Good Place', city: [name: 'Boulder', zipCode: '80306']]

        expect:
        descriptor.calculateKey(dataItem) == new KeyValue.Builder()
                .withValue(findAttribute(ioAddress, 'name'), 'Boulder')
                .withValue(findAttribute(ioAddress, 'zipCode'), '80306')
                .build()
    }

    @Test
    def "calculates key when item has simple and reference key attributes"() {
        given:
        def catalogIO = item 'Catalog', [uniqueAttribute('id')]
        def catalogVersionIO = item 'CatalogVersion', [uniqueAttribute('version'), uniqueAttribute('catalog', catalogIO)]
        def productIO = item('Product', [uniqueAttribute('code'),
                                         uniqueAttribute('catalogVersion', catalogVersionIO),
                                         nonUniqueAttribute('name')])
        def descriptor = ItemKeyDescriptor.create(productIO)
        def dataItem = [code: 'XYZ', name: 'Good Product', catalogVersion: [version: 'Staged', catalog: [id: 'Default']]]

        expect:
        descriptor.calculateKey(dataItem) == new KeyValue.Builder()
                .withValue(findAttribute(productIO, 'code'), 'XYZ')
                .withValue(findAttribute(catalogVersionIO, 'version'), 'Staged')
                .withValue(findAttribute(catalogIO, 'id'), 'Default')
                .build()
    }

    @Test
    def "calculates key when item references itself in a non-key attributes"() {
        given:
        def itemIO = item 'SomeItem', [uniqueAttribute('code'), nonUniqueAttribute('otherItem', item('SomeItem'))]
        def descriptor = ItemKeyDescriptor.create(itemIO)

        expect:
        descriptor.calculateKey([code: 1, otherItem: [code: 1]]) == new KeyValue.Builder()
                .withValue(findAttribute(itemIO, 'code'), 1)
                .build()
    }

    @Test
    @Unroll
    def "throws CircularKeyReferenceException when key attribute reference chain is #condition"() {
        given:
        def validKey = uniqueAttribute 'code'

        when:
        ItemKeyDescriptor.create item('A', [validKey, invalidKey])

        then:
        def e = thrown CircularKeyReferenceException
        with(e) {
            integrationItemCode == type
            attributeName == 'loopback'
            referencedType == refType
            message.contains "${type}.loopback"
            message.contains refType
        }

        where:
        condition               | type | refType | invalidKey
        'A -> A'                | 'A'  | 'A'     | uniqueAttribute('loopback', item('A'))
        'A -> B -> B'           | 'B'  | 'B'     | uniqueAttribute('b', item('B', [uniqueAttribute('loopback', item('B'))]))
        'A -> B -> A'           | 'B'  | 'A'     | uniqueAttribute('b', item('B', [uniqueAttribute('loopback', item('A'))]))
        'A -> B -> C -> B'      | 'C'  | 'B'     | uniqueAttribute('b', item('B', [uniqueAttribute('c', item('C', [uniqueAttribute('loopback', item('B'))]))]))
        'A -> B -> C -> A'      | 'C'  | 'A'     | uniqueAttribute('b',
                item('B', [uniqueAttribute('c',
                        item('C', [uniqueAttribute('loopback',
                                item('A'))]))]))
        'A -> B -> C -> D -> B' | 'D'  | 'B'     | uniqueAttribute('b',
                item('B', [uniqueAttribute('c',
                        item('C', [uniqueAttribute('d',
                                item('D', [uniqueAttribute('loopback',
                                        item('B'))]))]))]))
    }

    @Test
    def 'descriptor can be created when same type appears in different key attribute references and does not form a loop'() {
        when:
        ItemKeyDescriptor.create(item('TypeA', [
                uniqueAttribute('ref1', item('TypeB', [uniqueAttribute('id')])),
                uniqueAttribute('ref2', item('TypeB', [uniqueAttribute('id')]))]))

        then:
        notThrown CircularKeyReferenceException
    }

    @Test
    @Unroll
    def "isKeyAttribute return #res for #attr"() {
        given:
        def desc = ItemKeyDescriptor.create item([uniqueAttribute('key'),
                                                  uniqueAttribute('nestedItemKey', item('NestedItem')),
                                                  nonUniqueAttribute('non-key')])

        expect:
        desc.isKeyAttribute(attr) == res

        where:
        attr            | res
        'key'           | true
        'nestedItemKey' | true
        'non-key'       | false
        ''              | false
        null            | false
    }

    private def item(List<TypeAttributeDescriptor> attributes) {
        item(ITEM_CODE, attributes)
    }

    private TypeDescriptor item(String type = 'DoesNotMatter', List<TypeAttributeDescriptor> attributes = []) {
        def item = Stub TypeDescriptor
        item.getItemCode() >> type
        item.getAttributes() >> attributes.collect { attribute(item, it) } // this is the reason to use findAttribute()
        item
    }

    def nonUniqueAttribute(String name, TypeDescriptor item = null) {
        attribute(name, item, false)
    }

    def uniqueAttribute(String name, TypeDescriptor item = null) {
        attribute(name, item, true)
    }

    private TypeAttributeDescriptor attribute(String name, TypeDescriptor item = null, boolean unique = false) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            isKeyAttribute() >> unique
            getAttributeType() >> item
            isPrimitive() >> (item == null)
        }
    }

    private TypeAttributeDescriptor attribute(TypeDescriptor item, TypeAttributeDescriptor attribute) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attribute.attributeName
            isKeyAttribute() >> attribute.keyAttribute
            getAttributeType() >> attribute.attributeType
            isPrimitive() >> attribute.primitive
            getTypeDescriptor() >> item
        }
    }

    /**
     * Used to lookup attributes in the specified item model because an exact match of the attribute is need for correct
     * {@code equals( )} behavior
     * @param item an item containing the attribute
     * @param attrName name of the attribute to find
     * @return a matching attribute descriptor or {@code null}, if such attribute is not found
     */
    private static TypeAttributeDescriptor findAttribute(TypeDescriptor item, String attrName) {
        item.attributes.find { it.attributeName == attrName }
    }
}
