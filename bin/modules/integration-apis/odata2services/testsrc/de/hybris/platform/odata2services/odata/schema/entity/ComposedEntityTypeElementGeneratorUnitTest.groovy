package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException
import de.hybris.platform.odata2services.odata.schema.KeyGenerator
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertyListGeneratorRegistry
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.Key
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty
import org.apache.olingo.odata2.api.edm.provider.Property
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test

@UnitTest
class ComposedEntityTypeElementGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final String CODE = "Any Code"

    def propertyListGenerator = Stub(AbstractPropertyListElementGenerator)
    def navPropertyListGeneratorRegistry = Stub(NavigationPropertyListGeneratorRegistry)
    def keyGenerator = Stub(KeyGenerator)

    private ComposedEntityTypeElementGenerator composedEntityTypeGenerator = new ComposedEntityTypeElementGenerator(
            keyGenerator,
            propertyListGenerator,
            navPropertyListGeneratorRegistry)

    @Test
    def "constructor fails without #param provided"() {
        when:
        new ComposedEntityTypeElementGenerator(keyGen, propertyListGen, navPropertyListRegistry)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param must not be null"

        where:
        param                 | keyGen             | propertyListGen                            | navPropertyListRegistry
        'keyGenerator'        | null               | Stub(AbstractPropertyListElementGenerator) | Stub(NavigationPropertyListGeneratorRegistry)
        'propertiesGenerator' | Stub(KeyGenerator) | null                                       | Stub(NavigationPropertyListGeneratorRegistry)
        'registry'            | Stub(KeyGenerator) | Stub(AbstractPropertyListElementGenerator) | null
    }

    @Test
    def 'generated entity type includes properties and key from the nested generators'() {
        given: "item type descriptor is found"
        def typeDescriptor = Stub(TypeDescriptor) { getItemCode() >> CODE }

        and: "property list generator generates a list of properties"
        def generatedSimpleProperties = [simpleProperty('property1'), simpleProperty('property2')]
        propertyListGenerator.generate(typeDescriptor) >> generatedSimpleProperties

        and: "key generator generates a key"
        def generatedKey = new Key()
        keyGenerator.generate(generatedSimpleProperties) >> Optional.of(generatedKey)

        and: "navigation properties generated"
        def generatedNavigationProperties = [navigationProperty('ref1'), navigationProperty('ref2')]
        navPropertyListGeneratorRegistry.generate(typeDescriptor) >> generatedNavigationProperties

        composedEntityTypeGenerator = new ComposedEntityTypeElementGenerator(keyGenerator,
                propertyListGenerator,
                navPropertyListGeneratorRegistry)

        when:
        def entityTypes = composedEntityTypeGenerator.generate typeDescriptor

        then:
        entityTypes.size() == 1
        with(entityTypes[0]) {
            name == CODE
            properties == generatedSimpleProperties
            navigationProperties == generatedNavigationProperties
            key == generatedKey
        }
    }

    @Test
    def "generate without key property throws MissingKeyException"() {
        given: "item type descriptor is found"
        def typeDescriptor = Stub(TypeDescriptor) { getItemCode() >> CODE }

        and: 'properties are generated'
        def generatedProperties = [simpleProperty()]
        propertyListGenerator.generate(typeDescriptor) >> generatedProperties

        and: 'key is not generated for the properties'
        keyGenerator.generate(generatedProperties) >> Optional.empty()

        when:
        composedEntityTypeGenerator.generate(typeDescriptor)

        then:
        def e = thrown(MissingKeyException)
        e.message.contains(CODE)
    }

    private static Property simpleProperty(final String propertyName = 'doesNotMatter') {
        new SimpleProperty(name: propertyName)
    }

    private static NavigationProperty navigationProperty(final String propertyName) {
        new NavigationProperty(name: propertyName)
    }
}
