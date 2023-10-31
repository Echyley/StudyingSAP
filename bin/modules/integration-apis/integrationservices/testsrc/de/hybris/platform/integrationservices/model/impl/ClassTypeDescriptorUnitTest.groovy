/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.enums.TestEnum
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.ReferencePath
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ClassTypeDescriptorUnitTest extends JUnitPlatformSpecification {

    def integrationObject = Stub IntegrationObjectModel
    def ioDescriptor = Stub IntegrationObjectDescriptor

    def descriptorFactory = Stub(DescriptorFactory) {
        createIntegrationObjectDescriptor(integrationObject) >> ioDescriptor
    }

    def classModel = Stub(IntegrationObjectClassModel) {
        getIntegrationObject() >> integrationObject
    }

    def descriptor = new ClassTypeDescriptor(ioDescriptor, classModel, descriptorFactory)

    @Test
    def "ClassTypeDescriptor uses DefaultDescriptorFactory as default descriptor factory"() {
        given:
        def descriptor = new ClassTypeDescriptor(ioDescriptor, classModel, null)

        expect:
        descriptor.factory.class == DefaultDescriptorFactory
    }

    @Test
    def "ClassTypeDescriptor uses AttributePathFinder as default when ReferencePathFinder is not set"() {
        expect:
        descriptor.referencePathFinder.class == AttributePathFinder
    }

    @Test
    def "setReferencePathFinder sets AttributePathFinder as default when passed ReferencePathFinder is null"() {
        given:
        descriptor.setReferencePathFinder(null)

        expect:
        descriptor.referencePathFinder.class == AttributePathFinder
    }

    @Test
    def "getIntegrationObjectCode reads integration object code"() {
        given:
        def code = "someCode"
        ioDescriptor.getCode() >> code

        expect:
        descriptor.getIntegrationObjectCode() is code
    }

    @Test
    def "getItemCode returns value of the integration object item code"() {
        given:
        def code = "someCode"
        classModel.getCode() >> code

        expect:
        descriptor.getItemCode() is code
    }

    @Test
    def "getTypeCode reads integration object class name"() {
        given:
        def type = CustomerModel
        def code = CustomerModel.name

        and:
        classModel.getType() >> type
        classModel.getType().getName() >> code

        expect:
        descriptor.getTypeCode() is code
    }

    @Test
    def "getAttribute returns empty optional when class model has no attributes"() {
        expect:
        descriptor.getAttribute("someAttribute").empty
    }

    @Test
    def "getAttribute returns empty optional when given name doesn't match with any existing attribute"() {
        given:
        def nonExistingAttributeName = "someName"
        def attr1 = attributeModel("someOtherName")
        givenClassHasAttributes([attr1])
        givenDescriptorFactoryCanCreateTypeAttributeDescriptorsFor([attr1])

        when:
        def attributeDescriptor = descriptor.getAttribute(nonExistingAttributeName)

        then:
        attributeDescriptor.empty
    }

    @Test
    def "getAttribute returns optional attribute descriptor of the existing attribute"() {
        given:
        def expectedName = "someName"
        def attr1 = attributeModel("someOtherName")
        def attr2 = attributeModel(expectedName)
        givenClassHasAttributes([attr1, attr2])
        givenDescriptorFactoryCanCreateTypeAttributeDescriptorsFor([attr1, attr2])

        when:
        def attributeDescriptor = descriptor.getAttribute(expectedName)

        then:
        attributeDescriptor.present
        attributeDescriptor.get().attributeName.is expectedName
    }

    @Test
    def "getAttributes returns empty set when class model has no attributes"() {
        expect:
        descriptor.getAttributes().empty
    }

    @Test
    def "getAttributes returns attribute descriptors for all existing attributes"() {
        given:
        def firstAttributeName = "firstAttribute"
        def secondAttributeName = "secondAttribute"
        def attr1 = attributeModel(firstAttributeName)
        def attr2 = attributeModel(secondAttributeName)
        givenClassHasAttributes([attr1, attr2])
        givenDescriptorFactoryCanCreateTypeAttributeDescriptorsFor([attr1, attr2])

        when:
        def attributeDescriptors = descriptor.getAttributes()

        then:
        attributeDescriptors.collect({ it.getAttributeName() }).containsAll([firstAttributeName, secondAttributeName])
    }

    @Test
    def "getAttributes does not leak references"() {
        given:
        def attribute = attributeModel('someAttribute')
        givenClassHasAttributes([attribute])
        givenDescriptorFactoryCanCreateTypeAttributeDescriptorFor(attribute)

        when:
        descriptor.attributes.clear()

        then:
        descriptor.attributes.size() == 1
    }

    @Test
    def "isPrimitive always returns false"() {
        expect:
        !descriptor.isPrimitive()
    }

    @Test
    def "isMap always returns false"() {
        expect:
        !descriptor.isMap()
    }

    @Test
    def "isEnumeration returns #returnValue if class model is #condition enumeration type"() {
        given:
        classModel.type >> clazz

        expect:
        descriptor.isEnumeration() == returnValue

        where:
        clazz     | returnValue | condition
        TestEnum  | true        | "an"
        ItemModel | false       | "not an"
    }

    @Test
    def "isAbstract returns #returnValue if class model is #condition abstract type"() {
        given:
        classModel.type >> clazz

        expect:
        descriptor.isAbstract() == returnValue

        where:
        clazz             | returnValue | condition
        TestAbstractClass | true        | "an"
        TestFinalClass    | false       | "not an"
    }

    @Test
    def "isInstance returns #returnValue if the specified object is #condition instance of the type presented by this type descriptor"() {
        given:
        classModel.type >> clazz

        expect:
        descriptor.isInstance(obj) == returnValue

        where:
        clazz          | obj                  | returnValue | condition
        Object         | null                 | false       | "null, and therefore not an"
        Object         | new TestFinalClass() | true        | "an"
        TestFinalClass | new Object()         | false       | "not an"
    }

    @Test
    def "isRoot returns #returnValue when class model #condition"() {
        given:
        classModel.root >> rootValue

        expect:
        descriptor.isRoot() == returnValue

        where:
        rootValue     | returnValue | condition
        null          | false       | "returns null when getRoot()"
        Boolean.TRUE  | true        | "is root"
        Boolean.FALSE | false       | "is not root"
    }

    @Test
    def "getKeyDescriptor always returns an ItemKeyDescriptor"() {
        expect:
        ItemKeyDescriptor.isInstance(descriptor.getKeyDescriptor())
    }

    @Test
    def "path to root doesn't exist when integration object does not have a root item"() {
        given:
        ioDescriptor.rootType >> Optional.empty()

        expect:
        !descriptor.hasPathToRoot()
        descriptor.getPathsToRoot().empty
    }

    @Test
    def "path to root doesn't exist when the provided ReferencePathFinder cannot find any path to root"() {
        given:
        def root = givenThatRootExistsForIntegrationObject()
        and: "a ReferencePathFinder is provided"
        descriptor.setReferencePathFinder(Stub(ReferencePathFinder))
        and:
        givenThatPathFinderCannotFindAnyPathTo(root)

        expect:
        !descriptor.hasPathToRoot()
        descriptor.getPathsToRoot().empty
    }

    @Test
    def "provided ReferencePathFinder is used to find paths to root"() {
        given:
        def root = givenThatRootExistsForIntegrationObject()
        and: "a ReferencePathFinder is provided"
        descriptor.setReferencePathFinder(Stub(ReferencePathFinder))
        and:
        def paths = givenThatPathFinderCanFindPathsTo(root)

        expect:
        descriptor.hasPathToRoot()
        descriptor.getPathsToRoot() == paths
    }

    @Test
    def "pathFrom returns an empty list when source class type is null"() {
        expect:
        descriptor.pathFrom(null).empty
    }

    @Test
    def "pathFrom returns an empty list when the provided ReferencePathFinder cannot find any path from source class type"() {
        given:
        def sourceType = Stub TypeDescriptor
        and: "a ReferencePathFinder is provided"
        descriptor.setReferencePathFinder(Stub(ReferencePathFinder))
        and:
        givenThatPathFinderCannotFindAnyPathFrom(sourceType)

        expect:
        descriptor.pathFrom(sourceType).empty
    }

    @Test
    def "pathFrom uses provided ReferencePathFinder to find paths from the source class type"() {
        given:
        def sourceType = Stub TypeDescriptor
        and: "a ReferencePathFinder is provided"
        descriptor.setReferencePathFinder(Stub(ReferencePathFinder))
        and:
        def paths = givenThatPathFinderCanFindPathsFrom(sourceType)

        expect:
        descriptor.pathFrom(sourceType) == paths
    }

    def attributeModel(String name) {
        Stub(IntegrationObjectClassAttributeModel) {
            getAttributeName() >> name
        }
    }

    def attributeDescriptor(IntegrationObjectClassAttributeModel model) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> model.attributeName
        }
    }

    def givenClassHasAttributes(attributeModels) {
        classModel.attributes >> attributeModels
    }

    def givenDescriptorFactoryCanCreateTypeAttributeDescriptorFor(model) {
        descriptorFactory.createTypeAttributeDescriptor(descriptor, model) >> attributeDescriptor(model)
    }

    def givenDescriptorFactoryCanCreateTypeAttributeDescriptorsFor(attributeModels) {
        attributeModels.forEach(model -> givenDescriptorFactoryCanCreateTypeAttributeDescriptorFor(model))
    }

    def givenThatRootExistsForIntegrationObject() {
        def root = Stub TypeDescriptor
        ioDescriptor.rootType >> Optional.of(root)
        root
    }

    def givenThatPathFinderCanFindPathsFrom(TypeDescriptor source) {
        pathFinderCanFindPaths(source, descriptor)
    }

    def givenThatPathFinderCanFindPathsTo(TypeDescriptor destination) {
        pathFinderCanFindPaths(descriptor, destination)
    }

    def pathFinderCanFindPaths(TypeDescriptor source, TypeDescriptor destination) {
        def paths = [Stub(ReferencePath), Stub(ReferencePath)]
        descriptor.referencePathFinder.findAllPaths(source, destination) >> paths
        paths
    }

    def givenThatPathFinderCannotFindAnyPathFrom(TypeDescriptor source) {
        pathFinderCannotFindAnyPath(source, descriptor)
    }

    def givenThatPathFinderCannotFindAnyPathTo(TypeDescriptor destination) {
        pathFinderCannotFindAnyPath(descriptor, destination)
    }

    def pathFinderCannotFindAnyPath(TypeDescriptor source, TypeDescriptor destination) {
        descriptor.referencePathFinder.findAllPaths(source, destination) >> Collections.emptyList()
    }

    private static abstract class TestAbstractClass {}

    private static final class TestFinalClass {}
}
