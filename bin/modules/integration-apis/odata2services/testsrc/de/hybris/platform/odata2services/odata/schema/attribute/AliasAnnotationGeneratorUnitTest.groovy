/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.attribute

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder

@UnitTest
class AliasAnnotationGeneratorUnitTest extends JUnitPlatformSpecification {

    def aliasGenerator = new AliasAnnotationGenerator()
    def integrationKeyMetadataGenerator = Stub IntegrationKeyMetadataGenerator

    @Shared
    def item = itemModelBuilder().build()
    @Shared
    def typeDescriptor = Stub TypeDescriptor

    def setup() {
        aliasGenerator.setIntegrationKeyMetadataGenerator(integrationKeyMetadataGenerator)
    }

    @Test
    def "generate for ItemModel is null when #condition"() {
        given:
        aliasGenerator.setIntegrationKeyMetadataGenerator(generator)
        integrationKeyMetadataGenerator.generateKeyMetadata(item) >> keyMetadata

        expect:
        aliasGenerator.generate(item) == null

        where:
        generator                             | keyMetadata | condition
        null                                  | "metadata"  | "IntegrationKeyMetadataGenerator is null"
        Stub(IntegrationKeyMetadataGenerator) | null        | "IntegrationKeyMetadata is null"
        Stub(IntegrationKeyMetadataGenerator) | ""          | "IntegrationKeyMetadata is empty"
    }

    @Test
    def "generate for ItemModel when item has key properties"() {
        given:
        integrationKeyMetadataGenerator.generateKeyMetadata(item) >> "SomeType_uniqueAttribute"

        when:
        def generatedAlias = aliasGenerator.generate(item)

        then:
        generatedAlias.name == "s:Alias"
        generatedAlias.text == "SomeType_uniqueAttribute"
    }

    @Test
    def "generate for TypeDescriptor returns null when #condition"() {
        given:
        aliasGenerator.setIntegrationKeyMetadataGenerator(generator)
        generator.generateKeyMetadata(typeDescriptor) >> metadata

        expect:
        aliasGenerator.generate(typeDescriptor) == null

        where:
        generator                             | metadata   | condition
        null                                  | "metadata" | "IntegrationKeyMetadataGenerator is null"
        Stub(IntegrationKeyMetadataGenerator) | null       | "IntegrationKeyMetadata is null"
        Stub(IntegrationKeyMetadataGenerator) | ""         | "IntegrationKeyMetadata is empty"
    }

    @Test
    def "generate for TypeDescriptor when item has key properties"() {
        given:
        integrationKeyMetadataGenerator.generateKeyMetadata(typeDescriptor) >> "SomeType_uniqueAttribute"

        when:
        def generatedAlias = aliasGenerator.generate(typeDescriptor)

        then:
        generatedAlias.name == "s:Alias"
        generatedAlias.text == "SomeType_uniqueAttribute"
    }
}
