/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.attribute.AliasAnnotationGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test

@UnitTest
class IntegrationKeyPropertyElementGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final def EXCEPTION_MESSAGE =
            "An Integration Key Property cannot be generated from a null TypeDescriptor"
    private static final def PROPERTY_NAME = "integrationKey"

    def descriptor = Stub(TypeDescriptor)
    def aliasGenerator = Stub(AliasAnnotationGenerator)

    def generator = new IntegrationKeyPropertyElementGenerator(aliasGenerator: aliasGenerator)

    @Test
    def "generate returns empty Optional when alias generator is null"() {
        given: "null generator injected"
        generator.setAliasGenerator(null)

        expect:
        generator.generate(descriptor).isEmpty()
    }

    @Test
    def "generate throws IllegalArgumentException when type descriptor is null"() {
        when:
        generator.generate(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == EXCEPTION_MESSAGE
    }

    @Test
    def "generate returns empty Optional when no key found"() {
        given:
        aliasGenerator.generate(descriptor) >> null

        expect:
        generator.generate(descriptor).isEmpty()
    }

    @Test
    def "generate successful"() {
        given:
        def aliasAnnotation = new AnnotationAttribute().setName("s:Alias").setText("MyAlias")
        def nullableAnnotation = new AnnotationAttribute().setName("Nullable").setText("false")
        def expectedAttributes = [aliasAnnotation, nullableAnnotation]
        aliasGenerator.generate(descriptor) >> aliasAnnotation

        when:
        def generatedProperty = generator.generate(descriptor)

        then:
        generatedProperty.isPresent()
        with(generatedProperty.get() as SimpleProperty) {
            name == PROPERTY_NAME
            type == EdmSimpleTypeKind.String
            nameAndTextFieldsOf(annotationAttributes).containsAll nameAndTextFieldsOf(expectedAttributes)
        }
    }

    def nameAndTextFieldsOf(List<AnnotationAttribute> attributes) {
        return attributes.collect { [it.name, it.text] }
    }
}
