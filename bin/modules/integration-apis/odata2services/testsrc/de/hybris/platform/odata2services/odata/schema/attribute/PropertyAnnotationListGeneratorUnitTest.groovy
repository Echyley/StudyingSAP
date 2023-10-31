/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.attribute

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class PropertyAnnotationListGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final AnnotationAttribute ANNOTATION_ATTRIBUTE = new ImmutableAnnotationAttribute()

    def generator = new PropertyAnnotationListGenerator()

    @Test
    @Unroll
    def "generate attributes when generators are #generators"() {
        given:
        generator.setAnnotationGenerators generators

        expect:
        generator.generate(Stub(TypeAttributeDescriptor)) == result

        where:
        condition   | generators                                        | result
        'empty'     | []                                                | []
        'not empty' | [applicableGenerator(), nonApplicableGenerator()] | [ANNOTATION_ATTRIBUTE]
    }

    def applicableGenerator() {
        Stub(PropertyAnnotationGenerator) {
            isApplicable(_ as TypeAttributeDescriptor) >> true
            generate(_ as TypeAttributeDescriptor) >> ANNOTATION_ATTRIBUTE
        }
    }

    def nonApplicableGenerator() {
        Stub(PropertyAnnotationGenerator) {
            isApplicable(_ as TypeAttributeDescriptor) >> false
        }
    }
}
