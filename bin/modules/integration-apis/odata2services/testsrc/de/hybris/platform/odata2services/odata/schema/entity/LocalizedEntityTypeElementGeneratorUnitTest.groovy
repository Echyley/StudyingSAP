package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException
import de.hybris.platform.odata2services.odata.schema.KeyGenerator
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.Key
import org.apache.olingo.odata2.api.edm.provider.Property
import org.junit.Test

@UnitTest
class LocalizedEntityTypeElementGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final String ITEM_TYPE = 'ItemType'

    def propertiesGenerator = Stub(AbstractPropertyListElementGenerator)
    def keyGenerator = Stub(KeyGenerator)
    def generator = new LocalizedEntityTypeElementGenerator(keyGenerator, propertiesGenerator)

    @Test
    def "constructor fails without #param provided"() {
        when:
        new LocalizedEntityTypeElementGenerator(keyGen, propertyListGen)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param must not be null"

        where:
        param                 | keyGen             | propertyListGen
        'keyGenerator'        | null               | Stub(AbstractPropertyListElementGenerator)
        'propertiesGenerator' | Stub(KeyGenerator) | null
    }

    @Test
    def "does not generate entity type when no localized attributes present in the item"() {
        given: "item does not contain localized attributes"
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> [Stub(TypeAttributeDescriptor) { isLocalized() >> false }]
        }

        when: "generation is called for the item"
        def types = generator.generate typeDescriptor

        then: "no entity types generated"
        types.isEmpty()
    }

    @Test
    def "generates entity type when localized attributes are present in the item"() {
        given: "item contains localized attribute"
        def typeDescriptor = itemContainsLocalizedAttributes()

        and: "key generator generates a key"
        keyGenerated()

        when: "generation is called for the item"
        def types = generator.generate typeDescriptor

        then: "the localized entity type is generated"
        types.size() == 1
        types[0].name == "${TestConstants.LOCALIZED_ENTITY_PREFIX}$ITEM_TYPE"
    }

    @Test
    def "generated localized entity type contains all properties generated by the properties generator"() {
        given: "item contains localized attributes"
        def typeDescriptor = itemContainsLocalizedAttributes()

        and: "properties generator generates some properties"
        def properties = propertiesGeneratedForItem typeDescriptor, property("localizedOne"), property("localizedTwo"), property("language")

        and: 'key generator generates a key'
        keyGenerated properties

        when: "generation is called for the item"
        def types = generator.generate typeDescriptor

        then: "the generated entity type contains all the generated properties"
        types[0].getProperties() == properties
    }

    @Test
    def "generated localized entity type contains key generated by the key generator"() {
        given: "item contains at least one localized attribute"
        def typeDescriptor = itemContainsLocalizedAttributes()

        and: "properties generator generates properties"
        def properties = propertiesGeneratedForItem typeDescriptor

        and: "key generator generates a key"
        Key key = keyGenerated properties

        when: "generation is called for the item"
        def types = generator.generate typeDescriptor

        then: "the generated entity type contains the generated key"
        types[0].key == key
    }

    @Test
    def "throws exception when entity type has no key properties"() {
        given: "item with localized attributes"
        def typeDescriptor = itemContainsLocalizedAttributes()

        and: "key generator generates no key"
        keyGenerator.generate(_ as List) >> Optional.empty()

        when: "generation is called for the item"
        generator.generate typeDescriptor

        then: "then an exception will be thrown"
        thrown MissingKeyException
    }

    private Key keyGenerated(List<Property> properties = []) {
        def key = new Key()
        keyGenerator.generate(properties) >> Optional.of(key)
        key
    }

    private List<Property> propertiesGeneratedForItem(TypeDescriptor typeDescriptor, Property... properties) {
        List<Property> list = properties.toList()
        propertiesGenerator.generate(typeDescriptor) >> list
        list
    }

    def property(final String name) {
        Stub(Property) { getName() >> name }
    }

    def itemContainsLocalizedAttributes() {
        Stub(TypeDescriptor) {
            getAttributes() >> [Stub(TypeAttributeDescriptor) { isLocalized() >> true }]
            getItemCode() >> ITEM_TYPE
        }
    }
}