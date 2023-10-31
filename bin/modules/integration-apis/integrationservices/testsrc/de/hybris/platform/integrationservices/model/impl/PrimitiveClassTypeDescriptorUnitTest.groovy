/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PrimitiveClassTypeDescriptorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_CODE = 'testIO'
    private static final def objectModel = new IntegrationObjectModel()
    private static final def classModel = new IntegrationObjectClassModel(integrationObject: objectModel)
    private static final def attributeModel = new IntegrationObjectClassAttributeModel(integrationObjectClass: classModel)

    @Test
    def "creates primitive class type descriptor"() {
        expect:
        primitiveClassDescriptor(String, IO_CODE)
    }

    @Test
    def "create() throws exception when objCode is null "() {
        when:
        primitiveClassDescriptor(String, null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "PojoIntrospector should contain IntegrationObjectModel with non-null integration object code."
    }

    @Test
    def "create() throws exception when typeClass is null"() {
        when:
        primitiveClassDescriptor(null, IO_CODE)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Non-null type Class is required"
    }

    @Test
    def "reads back integration object code specified during creation"() {
        given:
        def descriptor = primitiveClassDescriptor(Integer, IO_CODE)

        expect:
        descriptor.integrationObjectCode == IO_CODE
    }

    @Test
    def "typeCode value is always empty"() {
        given:
        def descriptor = primitiveClassDescriptor(Integer)

        expect:
        descriptor.typeCode == ""
    }

    @Test
    def "is always primitive"() {
        expect:
        primitiveClassDescriptor().primitive
    }

    @Test
    def "is never enumeration"() {
        expect:
        !primitiveClassDescriptor().enumeration
    }

    @Test
    def "does not have attributes"() {
        expect:
        primitiveClassDescriptor().attributes.empty
    }

    @Test
    def "getAttributes() does not leak mutability"() {
        given:
        def descriptor = primitiveClassDescriptor()

        when:
        descriptor.attributes.add Stub(TypeAttributeDescriptor)

        then:
        thrown(UnsupportedOperationException)
    }

    @Test
    def "getAttribute() is always empty"() {
        expect:
        !primitiveClassDescriptor().getAttribute("someAttribute").present
    }

    @Test
    def "KeyDescriptor is of type NullKeyDescriptor"() {
        expect:
        primitiveClassDescriptor().keyDescriptor as NullKeyDescriptor
    }

    @Test
    @Unroll
    def "isInstance() is true when the object is instance of the type descriptor #condition"() {
        given:
        def descriptor = primitiveClassDescriptor(Date)

        expect:
        descriptor.isInstance(obj)

        where:
        condition  | obj
        'type'     | Stub(Date)
        'sub-type' | Stub(SubDate)
    }

    @Test
    @Unroll
    def "isInstance() is false when the object is #condition"() {
        given:
        def descriptor = primitiveClassDescriptor(Integer)

        expect:
        !descriptor.isInstance(obj)

        where:
        condition                                | obj
        'null'                                   | null
        'not an instance of the type descriptor' | Long.valueOf(1)
    }

    @Test
    def "primitive descriptor is not the root"() {
        expect:
        !primitiveClassDescriptor().isRoot()
    }

    @Test
    def 'never has path to root item'() {
        given:
        def descriptor = primitiveClassDescriptor()

        expect:
        !descriptor.hasPathToRoot()
        descriptor.pathsToRoot.empty
    }

    @Test
    def 'never has path from the specified type'() {
        given:
        def descriptor = primitiveClassDescriptor()

        expect:
        descriptor.pathFrom(Stub(TypeDescriptor)).empty
    }

    @Test
    @Unroll
    def "not equals to other type descriptor if other type descriptor #condition"() {
        given:
        def sample = primitiveClassDescriptor(Integer)

        expect:
        sample != other

        where:
        condition                                                                 | other
        'is null'                                                                 | null
        'is not instance of PrimitiveClassTypeDescriptor'                         | Stub(TypeDescriptor)
        'is for a different primitive type'                                       | primitiveClassDescriptor(Float)
        'is for a different integration object code and different primitive type' | primitiveClassDescriptor(Float, "floatIO")
        'is for a different integration object code and same primitive type'      | primitiveClassDescriptor(Integer, "integerIO")
    }

    @Test
    def "equals to another primitive type descriptor for the same primitive type and same integration object code"() {
        given:
        def sample = primitiveClassDescriptor(Integer)

        expect:
        sample == other

        where:
        condition                                                       | other
        'is for a same primitive type and same integration object code' | primitiveClassDescriptor(Integer)
    }

    @Test
    def 'equals to same object'() {
        given:
        def sample = primitiveClassDescriptor(Integer)

        expect:
        sample.equals(sample)
    }

    @Test
    @Unroll
    def "hashCode is different if other descriptor #condition"() {
        given:
        def sample = primitiveClassDescriptor(Date)

        expect:
        sample.hashCode() != other.hashCode()

        where:
        condition                                                  | other
        'is for a different type'                                  | primitiveClassDescriptor(String)
        'is for a same type and different integration object code' | primitiveClassDescriptor(Date, "dateIO")
        'is not instance of PrimitiveClassTypeDescriptor'          | Stub(TypeDescriptor)
    }

    @Test
    def "hashCode() is equals to another primitive type descriptor for the same primitive type and same integration object code"() {
        given:
        def sample = primitiveClassDescriptor(Date)
        expect:
        sample.hashCode() == primitiveClassDescriptor(Date).hashCode()
    }

    private static PrimitiveClassTypeDescriptor primitiveClassDescriptor(Class typeClass = String, String objCode = IO_CODE) {
        objectModel.code = objCode
        classModel.type = typeClass
        def pojoIntrospector = new PojoIntrospector(attributeModel)
        return PrimitiveClassTypeDescriptor.create(pojoIntrospector, typeClass)
    }

    private static class SubDate extends Date {}

}