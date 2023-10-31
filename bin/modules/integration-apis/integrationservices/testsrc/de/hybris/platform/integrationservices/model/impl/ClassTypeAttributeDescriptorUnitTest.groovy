/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Nonnull
import javax.validation.constraints.NotNull

import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.CIRCULAR_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.INTEGER_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.LIST_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.MAP_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.NON_CIRCULAR_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.NON_LOCALIZED_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.NON_NULL_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.NOT_NULL_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.POJO_COLLECTION_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.POJO_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.PRIMITIVE_COLLECTION_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.SET_PROPERTY_NAME
import static de.hybris.platform.integrationservices.model.impl.ClassTypeAttributeDescriptorUnitTest.Order.STRING_PROPERTY_NAME

@UnitTest
class ClassTypeAttributeDescriptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CODE = 'anyIO'
    private static final def ORDER_IO = new IntegrationObjectModel(code: IO_CODE)
    private static final def ORDER_MODEL = new IntegrationObjectClassModel(type: Order, integrationObject: ORDER_IO)
    private static final def ADDRESS_MODEL = new IntegrationObjectClassModel(type: Address, integrationObject: ORDER_IO)
    private static final def ORDER_ENTRY_MODEL = new IntegrationObjectClassModel(type: OrderEntry, integrationObject: ORDER_IO)

    @Shared
    def DEFAULT_DESCRIPTOR = classAttributeDescriptorFor attribute()
    @Shared
    def IO_DESCRIPTOR = Stub(IntegrationObjectDescriptor)
    @Shared
    def ORDER_CLASS_DESCRIPTOR = Stub(TypeDescriptor)
    @Shared
    def ORDER_ENTRY_CLASS_DESCRIPTOR = Stub(TypeDescriptor) {
        attributes >> createAttributeDescriptorsForAttributesIn(ORDER_ENTRY_MODEL)
    }
    @Shared
    def ADDRESS_CLASS_DESCRIPTOR = Stub(TypeDescriptor) {
        attributes >> createAttributeDescriptorsForAttributesIn(ADDRESS_MODEL)
    }

    @Test
    def "ClassTypeAttributeDescriptor uses DefaultDescriptorFactory if provided DescriptorFactory is null"() {
        given:
        def descriptor = classAttributeDescriptorFor(attribute(), null)

        expect:
        descriptor.factory.class == DefaultDescriptorFactory
    }

    @Test
    def "getAttributeName returns attribute name"() {
        given:
        def attribute = attribute "someName"
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.getAttributeName() is attribute.attributeName
    }

    @Test
    def "getQualifier always returns an empty string"() {
        expect:
        DEFAULT_DESCRIPTOR.getQualifier().empty
    }

    @Test
    def "isCollection returns #returnValue when attribute has a #type return type"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.isCollection() == returnValue

        where:
        attribute             | returnValue | type
        listAttribute()       | true        | 'List'
        setAttribute()        | true        | 'Set'
        collectionAttribute() | true        | 'Collection'
        primitiveAttribute()  | false       | 'primitive'
        mapAttribute()        | false       | 'Map'
        pojoAttribute()       | false       | 'POJO'
    }

    @Test
    def "getAttributeType returns correct type descriptor when attribute is a #condition"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.getAttributeType() is ORDER_ENTRY_CLASS_DESCRIPTOR

        where:
        attribute                 | condition
        pojoAttribute()           | "POJO"
        pojoCollectionAttribute() | "Collection of POJOs"
    }

    @Test
    def "getAttributeType returns #expectedType when attribute is a #type"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.getAttributeType().class == expectedType

        where:
        attribute                      | expectedType                 | type
        mapAttribute()                 | MapClassTypeDescriptor       | "Map type"
        primitiveAttribute()           | PrimitiveClassTypeDescriptor | "primitive type"
        primitiveCollectionAttribute() | PrimitiveClassTypeDescriptor | "collection type"
    }

    @Test
    def "getTypeDescriptor returns type descriptor for the container class"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute()

        expect:
        descriptor.getTypeDescriptor() is ORDER_CLASS_DESCRIPTOR
    }

    @Test
    def "reverse returns Optional.empty when attribute does not link back to the parent class"() {
        given:
        def descriptor = classAttributeDescriptorFor nonCircularDependentAttribute()

        expect:
        descriptor.reverse().empty
    }

    @Test
    def "reverse returns correct TypeAttributeDescriptor when attribute links back to the parent class"() {
        given:
        def descriptor = classAttributeDescriptorFor circularDependentAttribute()

        when:
        def rev = descriptor.reverse()

        then:
        rev.present
        rev.get().attributeType == ORDER_CLASS_DESCRIPTOR
    }

    @Test
    def "isNullable returns #returnValue when attribute is #condition"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.isNullable() == returnValue

        where:
        attribute            | returnValue | condition
        nonNullAttribute()   | false       | "@Nonnull."
        notNullAttribute()   | false       | "@NotNull."
        primitiveAttribute() | true        | "not @NotNull or @Nonnull."
    }

    @Test
    def "isPartOf always returns false"() {
        expect:
        !DEFAULT_DESCRIPTOR.isPartOf()
    }

    @Test
    def "isAutoCreate always returns false"() {
        expect:
        !DEFAULT_DESCRIPTOR.isAutoCreate()
    }

    @Test
    def "isLocalized always return false"() {
        expect:
        !DEFAULT_DESCRIPTOR.isLocalized()
    }

    @Test
    def "isPrimitive returns false when attribute is a #type"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        !descriptor.isPrimitive()

        where:
        attribute                 | type
        pojoAttribute()           | "POJO"
        pojoCollectionAttribute() | "Collection of POJOs"
    }

    @Test
    def "isPrimitive returns #returnValue when attribute is a #condition"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.isPrimitive() == returnValue

        where:
        attribute                      | returnValue | condition
        mapAttribute()                 | false       | "Map"
        primitiveCollectionAttribute() | true        | "Collection of Primitives"
        primitiveAttribute()           | true        | "Primitive"
    }

    @Test
    def "isMap returns #returnValue when attribute is a #condition"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.isMap() == returnValue

        where:
        attribute                      | returnValue | condition
        primitiveAttribute()           | false       | "Primitive"
        primitiveCollectionAttribute() | false       | "Collection"
        mapAttribute()                 | true        | "Map"
    }

    @Test
    def "isSettable always returns false"() {
        expect:
        !DEFAULT_DESCRIPTOR.isSettable(_)
    }

    @Test
    def "isKeyAttribute always returns #expectedKeyValue when unique flag is #uniqueValue"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute('name', null, uniqueValue)

        expect:
        descriptor.keyAttribute == expectedKeyValue

        where:
        uniqueValue            | expectedKeyValue
        null                   | false
        Boolean.valueOf(false) | false
        Boolean.valueOf(true)  | true
    }

    @Test
    def "isReadable is always true"() {
        expect:
        DEFAULT_DESCRIPTOR.isReadable()
    }

    @Test
    def "getCollectionDescriptor returns a ClassCollectionDescriptor"() {
        expect:
        DEFAULT_DESCRIPTOR.getCollectionDescriptor().class == ClassCollectionDescriptor
    }

    @Test
    def "getMapDescriptor returns Optional.empty when attribute is a #type"() {
        given:
        def descriptor = classAttributeDescriptorFor attribute

        expect:
        descriptor.getMapDescriptor().empty

        where:
        attribute                      | type
        primitiveAttribute()           | "Primitive"
        primitiveCollectionAttribute() | "Collection"
    }

    @Test
    def "getMapDescriptor returns Optional of ClassMapDescriptor when attribute is a map"() {
        given:
        def descriptor = classAttributeDescriptorFor mapAttribute()

        expect:
        def mapDesc = descriptor.getMapDescriptor()
        mapDesc.present
        mapDesc.get().class == ClassMapDescriptor
    }

    @Test
    def "accessor returns DelegatingAttributeValueAccessor with ClassAttributeValueGetter as getter"() {
        given:
        def descriptor = classAttributeDescriptorFor stringAttribute()
        def pojo = new Order(stringAttr: "some id")

        when:
        def accessor = descriptor.accessor()
        def actualId = accessor.getValue(pojo)

        then:
        actualId == pojo.stringAttr
    }

    @Test
    def "accessor returns DelegatingAttributeValueAccessor with NullAttributeValueSetter as setter"() {
        given:
        def descriptor = classAttributeDescriptorFor stringAttribute()
        def originalId = "some id"
        def pojo = new Order(stringAttr: originalId)

        when:
        def accessor = descriptor.accessor()
        accessor.setValue(pojo, "new id")

        then:
        pojo.stringAttr == originalId
    }

    @Test
    def "getAttributeType() is cached"() {
        given:
        def descriptor = classAttributeDescriptorFor stringAttribute()
        and:
        def descriptorFromFirstCall = descriptor.getAttributeType()
        def descriptorFromSecondCall = descriptor.getAttributeType()

        expect:
        descriptorFromFirstCall.is descriptorFromSecondCall
    }


    private IntegrationObjectClassAttributeModel attribute(String attributeName = "",
                                                           IntegrationObjectClassModel returnIOClass = null,
                                                           Boolean unique = null) {
        new IntegrationObjectClassAttributeModel(
                integrationObjectClass: ORDER_MODEL,
                attributeName: attributeName,
                returnIntegrationObjectClass: returnIOClass,
                unique: unique)
    }

    private IntegrationObjectDescriptor IO_DESCRIPTOR() {
        Stub(IntegrationObjectDescriptor)
    }

    private DescriptorFactory factory() {
        Stub(DescriptorFactory) {
            createIntegrationObjectDescriptor(ORDER_IO) >> IO_DESCRIPTOR
            createClassTypeDescriptor(_ as IntegrationObjectDescriptor, ORDER_ENTRY_MODEL) >> ORDER_ENTRY_CLASS_DESCRIPTOR
            createClassTypeDescriptor(_ as IntegrationObjectDescriptor, ADDRESS_MODEL) >> ADDRESS_CLASS_DESCRIPTOR
        }
    }

    private ClassTypeAttributeDescriptor classAttributeDescriptorFor(IntegrationObjectClassAttributeModel attribute,
                                                                     DescriptorFactory factory = factory()) {
        new ClassTypeAttributeDescriptor(ORDER_CLASS_DESCRIPTOR, attribute, factory)
    }

    private createAttributeDescriptorsForAttributesIn(IntegrationObjectClassModel pojo) {
        def attrModels = createAttributeModelsFromAttributesInClass(pojo)
        attrModels.collect(attr -> attributeDescriptorFor(attr))
    }

    private createAttributeModelsFromAttributesInClass(IntegrationObjectClassModel pojo) {
        pojo.type.declaredFields.collect(
                field -> new IntegrationObjectClassAttributeModel(attributeName: field.name)
        )
    }

    private attributeDescriptorFor(IntegrationObjectClassAttributeModel attrModel) {
        def desc = Stub(TypeAttributeDescriptor) {
            attributeName >> attrModel.attributeName
        }
        if (attrModel.attributeName == "currentOrder") {
            desc.attributeType >> ORDER_CLASS_DESCRIPTOR
        }
        desc
    }

    private IntegrationObjectClassAttributeModel listAttribute() { attribute(LIST_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel setAttribute() { attribute(SET_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel collectionAttribute() { attribute(PRIMITIVE_COLLECTION_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel primitiveAttribute() { attribute(INTEGER_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel stringAttribute() { attribute(STRING_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel mapAttribute() { attribute(MAP_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel pojoAttribute() { attribute(POJO_PROPERTY_NAME, ORDER_ENTRY_MODEL) }

    private IntegrationObjectClassAttributeModel pojoCollectionAttribute() {
        attribute(POJO_COLLECTION_PROPERTY_NAME, ORDER_ENTRY_MODEL)
    }

    private IntegrationObjectClassAttributeModel notNullAttribute() { attribute(NOT_NULL_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel nonNullAttribute() { attribute(NON_NULL_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel nonLocalizedAttribute() { attribute(NON_LOCALIZED_PROPERTY_NAME) }

    private IntegrationObjectClassAttributeModel nonCircularDependentAttribute() {
        attribute(NON_CIRCULAR_PROPERTY_NAME, ADDRESS_MODEL)
    }

    private IntegrationObjectClassAttributeModel circularDependentAttribute() {
        attribute(CIRCULAR_PROPERTY_NAME, ORDER_ENTRY_MODEL)
    }

    private IntegrationObjectClassAttributeModel primitiveCollectionAttribute() {
        attribute(PRIMITIVE_COLLECTION_PROPERTY_NAME)
    }

    private IntegrationObjectClassAttributeModel composedReturnTypeWithoutConfigReturnClass() {
        attribute(POJO_PROPERTY_NAME)
    }


    private static class Order {
        static final def STRING_PROPERTY_NAME = "stringAttr"
        static final def INTEGER_PROPERTY_NAME = "integerAttr"
        static final def MAP_PROPERTY_NAME = "mapAttr"
        static final def POJO_PROPERTY_NAME = "orderEntry"
        static final def PRIMITIVE_PROPERTY_NAME = INTEGER_PROPERTY_NAME
        static final def PRIMITIVE_COLLECTION_PROPERTY_NAME = "collectionAttr"
        static final def POJO_COLLECTION_PROPERTY_NAME = "orderEntries"
        static final def NON_CIRCULAR_PROPERTY_NAME = "orderAddress"
        static final def CIRCULAR_PROPERTY_NAME = POJO_PROPERTY_NAME
        static final def NON_LOCALIZED_PROPERTY_NAME = PRIMITIVE_PROPERTY_NAME
        static final def LIST_PROPERTY_NAME = "listAttr"
        static final def SET_PROPERTY_NAME = "setAttr"
        static final def NOT_NULL_PROPERTY_NAME = MAP_PROPERTY_NAME
        static final def NON_NULL_PROPERTY_NAME = PRIMITIVE_COLLECTION_PROPERTY_NAME

        String stringAttr
        int integerAttr
        Address orderAddress
        OrderEntry orderEntry
        Map<String, String> mapAttr
        Collection<String> collectionAttr
        List<String> listAttr
        Set<String> setAttr
        Collection<OrderEntry> orderEntries

        String getStringAttr(Locale l) { stringAttr }

        int getIntegerAttr() { integerAttr }

        @NotNull
        Map<String, String> getMapAttr() { mapAttr }

        @Nonnull
        Collection<String> getCollectionAttr() { collectionAttr }

        Collection<OrderEntry> getOrderEntries() { orderEntries }

        OrderEntry getOrderEntry() { orderEntry }

        Address getOrderAddress() { orderAddress }

        void setStringAttr(String a) { stringAttr = a }
    }

    private static class Address extends ItemModel {
        String country

        String getCountry() { country }
    }

    private static class OrderEntry extends ItemModel {
        Order currentOrder

        Order getCurrentOrder() { currentOrder }
    }
}
