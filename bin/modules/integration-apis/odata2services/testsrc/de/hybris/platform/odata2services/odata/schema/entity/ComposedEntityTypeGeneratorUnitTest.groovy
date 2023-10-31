/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException
import de.hybris.platform.odata2services.odata.schema.KeyGenerator
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertyListGeneratorRegistry
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.Key
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty
import org.apache.olingo.odata2.api.edm.provider.Property
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test

@UnitTest
class ComposedEntityTypeGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final def IO_ITEM = new IntegrationObjectItemModel(code: 'code1')

    def propertyListGenerator = Stub(AbstractPropertyListGenerator)
    def navPropertyListGeneratorRegistry = Stub(NavigationPropertyListGeneratorRegistry)
    def descriptorFactory = Stub(DescriptorFactory)
    def keyGenerator = Stub(KeyGenerator)


    private ComposedEntityTypeGenerator composedEntityTypeGenerator = new ComposedEntityTypeGenerator(
            registry: navPropertyListGeneratorRegistry,
            descriptorFactory: descriptorFactory,
            propertiesGenerator: propertyListGenerator,
            keyGenerator: keyGenerator
    )

    @Test
    def 'generated entity type includes properties and key from the nested generators'() {
        given: "item type descriptor is found"
        def typeDescriptor = Stub(TypeDescriptor)
        descriptorFactory.createItemTypeDescriptor(IO_ITEM) >> typeDescriptor

        and: "property list generator generates a list of properties"
        def generatedSimpleProperties = [simpleProperty('property1'), simpleProperty('property2')]
        propertyListGenerator.generate(IO_ITEM) >> generatedSimpleProperties

        and: "key generator generates a key"
        def generatedKey = new Key()
        keyGenerator.generate(generatedSimpleProperties) >> Optional.of(generatedKey)

        and: "navigation properties generated"
        def generatedNavigationProperties = [navigationProperty('ref1'), navigationProperty('ref2')]
        navPropertyListGeneratorRegistry.generate(typeDescriptor) >> generatedNavigationProperties

        when:
        def entityTypes = composedEntityTypeGenerator.generate IO_ITEM

        then:
        entityTypes.size() == 1
        with(entityTypes[0]) {
            name == IO_ITEM.code
            properties == generatedSimpleProperties
            navigationProperties == generatedNavigationProperties
            key == generatedKey
        }
    }

    @Test
    def "generate when typeDescriptor is null populates the expected entityType"() {
        given: "item type descriptor is not found"
        descriptorFactory.createItemTypeDescriptor(IO_ITEM) >> null

        and: "property list generator generates a list of properties"
        def expectedPropertyList = [simpleProperty('property1')]
        propertyListGenerator.generate(IO_ITEM) >> expectedPropertyList

        and: "key generator generates a key"
        final Key expectedKey = new Key()
        keyGenerator.generate(expectedPropertyList) >> Optional.of(expectedKey)

        when:
        def entityTypes = composedEntityTypeGenerator.generate IO_ITEM

        then:
        entityTypes.size() == 1
        def entityType = entityTypes.get(0)
        with(entityType) {
            name == IO_ITEM.code
            key == expectedKey
            properties == expectedPropertyList
            navigationProperties.empty
        }
    }

    @Test
    def "generate key generation throws exception"() {
        given: 'simple properties are generated'
        def generatedProperties = [simpleProperty()]
        propertyListGenerator.generate(IO_ITEM) >> generatedProperties

        and: "key generator throws exception"
        def exception = new IllegalArgumentException()
        keyGenerator.generate(generatedProperties) >> { throw exception }

        when:
        composedEntityTypeGenerator.generate(IO_ITEM)

        then:
        def thrown = thrown IllegalArgumentException
        thrown.is exception
    }

    @Test
    def "generate without key property throws MissingKeyException"() {
        given: 'properties are generated'
        def generatedProperties = [simpleProperty()]
        propertyListGenerator.generate(IO_ITEM) >> generatedProperties

        and: 'key is not generated for the properties'
        keyGenerator.generate(generatedProperties) >> Optional.empty()

        when:
        composedEntityTypeGenerator.generate(IO_ITEM)

        then:
        def e = thrown(MissingKeyException)
        e.message.contains(IO_ITEM.code)
    }

    @Test
    def "generate with null IntegrationObjectItemModel throws exception"() {
        when:
        composedEntityTypeGenerator.generate(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'IntegrationObjectItemModel'
    }

    private static Property simpleProperty(final String propertyName = 'doesNotMatter') {
        new SimpleProperty(name: propertyName)
    }

    private static NavigationProperty navigationProperty(final String propertyName) {
        new NavigationProperty(name: propertyName)
    }
}
