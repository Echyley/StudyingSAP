/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException
import de.hybris.platform.odata2services.odata.schema.KeyGenerator
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.Key
import org.apache.olingo.odata2.api.edm.provider.Property
import org.junit.Test

@UnitTest
class LocalizedEntityTypeGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final String ITEM_TYPE = 'ItemType'

    AbstractPropertyListGenerator propertiesGenerator = Stub(AbstractPropertyListGenerator)
    KeyGenerator keyGenerator = Stub(KeyGenerator)
    DescriptorFactory descriptorFactory = Stub(DescriptorFactory)
    def generator = new LocalizedEntityTypeGenerator(
            propertiesGenerator: propertiesGenerator,
            keyGenerator: keyGenerator,
            descriptorFactory: descriptorFactory
    )

    @Test
    def "does not generate entity type when no localized attributes present in the item"() {
        given: "item does not contain localized attributes"
        def item = Stub IntegrationObjectItemModel
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> [Stub(TypeAttributeDescriptor) { isLocalized() >> false }]
        }
        descriptorFactory.createItemTypeDescriptor(item) >> typeDescriptor

        when: "generation is called for the item"
        def types = generator.generate item

        then: "no entity types generated"
        types.isEmpty()
    }

    @Test
    def "generates entity type when localized attributes are present in the item"() {
        given: "item contains localized attribute"
        def item = itemContainsLocalizedAttributes()

        and: "key generator generates a key"
        keyGenerated()

        when: "generation is called for the item"
        def types = generator.generate item

        then: "the localized entity type is generated"
        types.size() == 1
        types[0].name == "${TestConstants.LOCALIZED_ENTITY_PREFIX}$ITEM_TYPE"
    }

    @Test
    def "generated localized entity type contains all properties generated by the properties generator"() {
        given: "item contains localized attributes"
        def item = itemContainsLocalizedAttributes()

        and: "properties generator generates some properties"
        def properties = propertiesGeneratedForItem item, property("localizedOne"), property("localizedTwo"), property("language")

        and: 'key generator generates a key'
        keyGenerated properties

        when: "generation is called for the item"
        def types = generator.generate item

        then: "the generated entity type contains all the generated properties"
        types.get(0).getProperties() == properties
    }

    @Test
    def "generated localized entity type contains key generated by the key generator"() {
        given: "item contains at least one localized attribute"
        def item = itemContainsLocalizedAttributes()

        and: "properties generator generates properties"
        def properties = propertiesGeneratedForItem item

        and: "key generator generates a key"
        Optional<Key> key = keyGenerated properties

        when: "generation is called for the item"
        def types = generator.generate item

        then: "the generated entity type contains the generated key"
        types.get(0).getKey() == key.orElseThrow({new IllegalStateException("Stubbing is done incorrectly for the test")})
    }

    @Test
    def "throws exception when entity type has no key properties"() {
        given: "item with localized attributes"
        def item = itemContainsLocalizedAttributes()

        and: "key generator generates no key"
        keyGenerator.generate(_ as List) >> Optional.empty()

        when: "generation is called for the item"
        generator.generate item

        then: "then an exception will be thrown"
        thrown MissingKeyException
    }

    private Optional<Key> keyGenerated(List<Property> properties = []) {
        final Optional<Key> key = Optional.of new Key()
        keyGenerator.generate(properties) >> key
        key
    }

    private List<Property> propertiesGeneratedForItem(IntegrationObjectItemModel item, Property... properties) {
        List<Property> list = properties.toList()
        propertiesGenerator.generate(item) >> list
        list
    }

    def property(final String name) {
        Stub(Property) { getName() >> name }
    }

    def itemContainsLocalizedAttributes() {
        def item = Stub(IntegrationObjectItemModel) {
            getCode() >> ITEM_TYPE
        }
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> [Stub(TypeAttributeDescriptor) { isLocalized() >> true }]
        }
        descriptorFactory.createItemTypeDescriptor(item) >> typeDescriptor
        item
    }
}
