/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.Association
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class AssociationListGeneratorRegistryUnitTest extends JUnitPlatformSpecification {
    def registry = new AssociationListGeneratorRegistry()

    @Test
    @Unroll
    def "generates empty associations list when #condition"() {
        given:
        registry.associationGenerators = generators

        expect:
        registry.generateFor(descriptors).empty

        where:
        condition                    | descriptors           | generators
        'descriptors are null'       | null                  | [generatorWithNumberOfResults(1)]
        'nested generators are []'   | someTypeDescriptors() | []
        'nested generators are null' | someTypeDescriptors() | null
    }

    @Test
    @Unroll
    def "combines generated associations when one nested generator produces #cnt1 associations and the other one produces #cnt2 associations"() {
        given:
        registry.associationGenerators = [generatorWithNumberOfResults(cnt1), generatorWithNumberOfResults(cnt2)]

        expect:
        registry.generateFor(someTypeDescriptors()).size() == resultSize

        where:
        cnt1 | cnt2 | resultSize
        2    | 1    | 3
        2    | 0    | 2
        0    | 1    | 1
        0    | 0    | 0
    }

    @Test
    @Unroll
    def "nested association generators can be reset to #generators"() {
        given: 'the registry has a nested generator, which produces 2 associations'
        registry.associationGenerators = [generatorWithNumberOfResults(2)]

        when: 'the association generators are reset'
        registry.associationGenerators = generators

        then: 'result contains only association produced by the new nested generators'
        registry.generateFor(someTypeDescriptors()).empty

        where:
        generators << [null, [], [generatorWithNumberOfResults(0)]]
    }

    private List<TypeDescriptor> someTypeDescriptors() {
        [Stub(TypeDescriptor)]
    }

    private def generatorWithNumberOfResults(def count) {
        Stub(SchemaElementGenerator) {
            generate(_) >> [Stub(Association)] * count
        }
    }
}
