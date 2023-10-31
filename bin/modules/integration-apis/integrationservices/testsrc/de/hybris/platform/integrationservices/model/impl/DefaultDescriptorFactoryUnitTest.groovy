/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.AttributeValueSetterFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultDescriptorFactoryUnitTest extends JUnitPlatformSpecification {

    def factory = new DefaultDescriptorFactory(attributeValueSetterFactory: Stub(AttributeValueSetterFactory))

    @Test
    def "factory is injected when a descriptor for a given integration #input model is created"() {
        when:
        def descriptor = factory.&(method)(model)

        then:
        descriptor
        descriptor.factory.is factory

        where:
        input                      | method                              | model
        'object'                   | 'createIntegrationObjectDescriptor' | Stub(IntegrationObjectModel)
        'item'                     | 'createItemTypeDescriptor'          | Stub(IntegrationObjectItemModel)
        'standard attribute'       | 'createTypeAttributeDescriptor'     | Stub(IntegrationObjectItemAttributeModel)
        'classification attribute' | 'createTypeAttributeDescriptor'     | Stub(IntegrationObjectItemClassificationAttributeModel)
        'virtual attribute'        | 'createTypeAttributeDescriptor'     | Stub(IntegrationObjectItemVirtualAttributeModel)
    }

    @Test
    def "injects factory when a descriptor for a given type descriptor and integration class model is created"() {
        given:
        def container = Stub(IntegrationObjectDescriptor)
        def model = new IntegrationObjectClassModel()

        when:
        def descriptor = factory.createClassTypeDescriptor(container, model)

        then:
        descriptor?.factory.is factory
    }

    @Test
    def "creates a descriptor for a given type descriptor and integration #input model"() {
        when:
        def descriptor = factory.&(method)(container, model)

        then:
        descriptor?.factory.is factory
        descriptor?.typeDescriptor.is container

        where:
        input             | method                          | model                                      | container
        'class attribute' | 'createTypeAttributeDescriptor' | new IntegrationObjectClassAttributeModel() | Stub(TypeDescriptor)
        'item attribute'  | 'createTypeAttributeDescriptor' | new IntegrationObjectItemAttributeModel()  | Stub(TypeDescriptor)
    }

    @Test
    def 'injects reference path finder into the type descriptor'() {
        given:
        def pathFinder = Stub ReferencePathFinder
        factory.referencePathFinder = pathFinder

        when:
        def typeDescriptor = factory.createItemTypeDescriptor Stub(IntegrationObjectItemModel)

        then:
        typeDescriptor.referencePathFinder.is pathFinder
    }

    @Test
    def 'injects reference path finder into the class type descriptor'() {
        given:
        def pathFinder = Stub ReferencePathFinder
        def ioDescriptor = Stub(IntegrationObjectDescriptor)
        def model = Stub(IntegrationObjectClassModel)
        factory.referencePathFinder = pathFinder

        when:
        def typeDescriptor = factory.createClassTypeDescriptor ioDescriptor, model

        then:
        typeDescriptor.referencePathFinder.is pathFinder
    }

    @Test
    def "throws exception when #method is called with null integration #component model"() {
        when:
        factory.&(method) null

        then:
        thrown IllegalArgumentException

        where:
        component   | method
        'object'    | 'createIntegrationObjectDescriptor'
        'item'      | 'createItemTypeDescriptor'
        'attribute' | 'createTypeAttributeDescriptor'
    }

    @Test
    def "throws exception when createTypeAttributeDescriptor is called with null #param"() {
        when:
        factory.createTypeAttributeDescriptor type, attribute as IntegrationObjectItemAttributeModel

        then:
        thrown IllegalArgumentException

        where:
        param                                 | type                 | attribute
        'TypeDescriptor'                      | null                 | new IntegrationObjectItemAttributeModel()
        'IntegrationObjectItemAttributeModel' | Stub(TypeDescriptor) | null
    }

    @Test
    def "get attribute value accessor factory returns #condition"() {
        given:
        factory.attributeValueAccessorFactory = accessorFactory

        when:
        def actualAccessorFactory = factory.getAttributeValueAccessorFactory()

        then:
        expectedAccessorFactory.isInstance actualAccessorFactory

        where:
        condition          | accessorFactory                            | expectedAccessorFactory
        'default factory'  | null                                       | DefaultAttributeValueAccessorFactory
        'provided factory' | Stub(DefaultAttributeValueAccessorFactory) | DefaultAttributeValueAccessorFactory
    }

    @Test
    def "get attribute value setter factory returns #condition"() {
        given:
        factory.attributeValueSetterFactory = setterFactory

        when:
        def actualSetterFactory = factory.getAttributeValueSetterFactory()

        then:
        expectedSetterFactory.isInstance actualSetterFactory

        where:
        condition          | setterFactory                            | expectedSetterFactory
        'default factory'  | null                                     | NullAttributeValueSetterFactory
        'provided factory' | Stub(DefaultAttributeValueSetterFactory) | DefaultAttributeValueSetterFactory
    }

    @Test
    def "get attribute settable checker returns #condition"() {
        given:
        factory.attributeSettableCheckerFactory = settableCheckerFactory

        when:
        def actualSettableChecker = factory.getAttributeSettableCheckerFactory()

        then:
        expectedSettableChecker.isInstance actualSettableChecker

        where:
        condition                 | settableCheckerFactory                       | expectedSettableChecker
        'null checker factory'    | null                                         | NullAttributeSettableCheckerFactory
        'default checker factory' | Stub(DefaultAttributeSettableCheckerFactory) | DefaultAttributeSettableCheckerFactory
    }
}
