/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test
import spock.lang.Shared

@UnitTest
class LocalizedPropertyListGeneratorUnitTest extends JUnitPlatformSpecification {
    @Shared
    def itemModel = Stub IntegrationObjectItemModel

    def descriptorFactory = Stub DescriptorFactory
    def propertyGenerator = Stub(SchemaElementGenerator) {
        generate(_) >> { args -> simpleEdmProperty(args[0].attributeName) }
    }
    def propertyListGenerator = new LocalizedPropertyListGenerator(
            descriptorFactory: descriptorFactory, simplePropertyGenerator: propertyGenerator)

    @Test
    def "generates only 'language' property when item has #condition attributes"() {
        given: 'TypeDescriptor for the item model has no applicable attributes'
        descriptorFactory.createItemTypeDescriptor(itemModel) >> Stub(TypeDescriptor) {
            getAttributes() >> attributes
        }

        when:
        def properties = propertyListGenerator.generate(itemModel)

        then: 'properties contain only language attribute'
        properties.size() == 1
        with(properties[0]) {
            name == 'language'
            type == EdmSimpleTypeKind.String
            !annotationAttributes.empty
            with(annotationAttributes[0]) {
                name == 'Nullable'
                text == 'false'
            }
        }

        where:
        condition            | attributes
        'no'                 | []
        'only non-primitive' | [nonPrimitiveAttribute()]
        'only collection'    | [collectionAttribute()]
        'only non-localized' | [nonLocalizedAttribute()]
    }

    @Test
    def 'generates properties when item model contains simple localized attribute'() {
        given: 'item model has a primitive attribute'
        descriptorFactory.createItemTypeDescriptor(itemModel) >> Stub(TypeDescriptor) {
            getAttributes() >> [primitiveLocalizedAttribute('name')]
        }

        when:
        def properties = propertyListGenerator.generate itemModel

        then:
        properties.collect({ it.name }) == ['name', 'language']
    }

    IntegrationObjectItemModel mockIntegrationObjectItemModel(final HashSet<IntegrationObjectItemAttributeModel> attributes) {
        Stub(IntegrationObjectItemModel) {
            getAttributes() >> attributes
        }
    }

    static SimpleProperty simpleEdmProperty(String name) {
        new SimpleProperty().setName(name)
    }

    TypeAttributeDescriptor nonPrimitiveAttribute() {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> false
            isLocalized() >> true
        }
    }

    TypeAttributeDescriptor collectionAttribute() {
        Stub(TypeAttributeDescriptor) {
            isCollection() >> true
        }
    }

    TypeAttributeDescriptor nonLocalizedAttribute() {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> true
            isLocalized() >> false
        }
    }

    TypeAttributeDescriptor primitiveLocalizedAttribute(String name) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            isPrimitive() >> true
            isLocalized() >> true
        }
    }
}
