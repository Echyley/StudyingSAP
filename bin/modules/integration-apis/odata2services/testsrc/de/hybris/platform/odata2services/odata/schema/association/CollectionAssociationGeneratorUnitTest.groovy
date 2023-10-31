/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.lang.StringUtils
import org.apache.olingo.odata2.api.edm.EdmMultiplicity
import org.apache.olingo.odata2.api.edm.FullQualifiedName
import org.apache.olingo.odata2.api.edm.provider.Association
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class CollectionAssociationGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final String ATTRIBUTE_NAME = 'addresses'
    private static final String SOURCE = "MyB2BUnit"
    private static final String TARGET = "MyAddress"
    def generator = new CollectionAssociationGenerator()

    @Test
    @Unroll
    def "is applicable is #expectedResult when isCollection=#collection, isMap=#map, and isLocalized=#localized as TypeAttributeDescriptor"() {
        given:
        def typeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            isCollection() >> collection
            isMap() >> map
            isLocalized() >> localized
        }

        expect:
        generator.isApplicable(typeAttributeDescriptor as TypeAttributeDescriptor) == expectedResult

        where:
        collection | map   | localized | expectedResult
        true       | false | false     | true
        true       | false | true      | true
        false      | true  | true      | false
        false      | true  | false     | true
        false      | false | false     | false
    }

    @Test
    def "is not applicable when TypeAttributeDescriptor is null"() {
        expect:
        !generator.isApplicable(null as TypeAttributeDescriptor)
    }

    @Test
    def "generate for collection attribute"() {
        given:
        def collectionAttributeDescriptor = typeAttributeDescriptor(
                [
                        'attributeName'              : ATTRIBUTE_NAME,
                        'integrationObjectItem'      : SOURCE,
                        'returnIntegrationObjectItem': TARGET
                ])

        and:
        collectionAttributeDescriptor.isCollection() >> true

        when:
        def association = generator.generate(collectionAttributeDescriptor as TypeAttributeDescriptor)

        then:
        equalToExpectedAssociation(association, ['attributeName': ATTRIBUTE_NAME, 'source': SOURCE, 'target': TARGET, 'toRole': TARGET])
    }

    @Test
    def "generate for collection when source is same as target type"() {
        given:
        def collectionAttributeDescriptor = typeAttributeDescriptor(
                [
                        'attributeName'              : ATTRIBUTE_NAME,
                        'integrationObjectItem'      : SOURCE,
                        'returnIntegrationObjectItem': SOURCE
                ])

        and:
        collectionAttributeDescriptor.isCollection() >> true

        when:
        final Association association = generator.generate(collectionAttributeDescriptor as TypeAttributeDescriptor)

        then:
        equalToExpectedAssociation(association, ['attributeName': ATTRIBUTE_NAME, 'source': SOURCE, 'target': SOURCE, 'toRole': StringUtils.capitalize(ATTRIBUTE_NAME)])
    }

    @Test
    def "generate for map"() {
        given:
        def mapTypeAttributeDescriptor = typeAttributeDescriptor(
                [
                        'attributeName'              : ATTRIBUTE_NAME,
                        'integrationObjectItem'      : SOURCE,
                        'returnIntegrationObjectItem': TARGET
                ])

        and: 'attributeDescriptor is a map'
        mapTypeAttributeDescriptor.isMap() >> true

        when:
        final Association association = generator.generate(mapTypeAttributeDescriptor as TypeAttributeDescriptor)

        then:
        equalToExpectedAssociation(association, ['attributeName': ATTRIBUTE_NAME, 'source': SOURCE, 'target': TARGET, 'toRole': TARGET])
    }

    private TypeAttributeDescriptor typeAttributeDescriptor(Map<String, String> params) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> params['attributeName']
            getTypeDescriptor() >> Stub(TypeDescriptor) {
                getItemCode() >> params['integrationObjectItem']
            }
            getAttributeType() >> Stub(TypeDescriptor) {
                getItemCode() >> params['returnIntegrationObjectItem']
            }
        }
    }

    private void equalToExpectedAssociation(Association association, Map<String, String> params) {
        with(association) {
            name == "FK_" + params['source'] + "_" + params['attributeName']
            with(end1) {
                type == new FullQualifiedName("HybrisCommerceOData", params['source'])
                role == params['source']
                multiplicity == EdmMultiplicity.ZERO_TO_ONE
            }
            with(end2) {
                type == new FullQualifiedName("HybrisCommerceOData", params['target'])
                role == params['toRole']
                multiplicity == EdmMultiplicity.MANY
            }
        }
    }
}
