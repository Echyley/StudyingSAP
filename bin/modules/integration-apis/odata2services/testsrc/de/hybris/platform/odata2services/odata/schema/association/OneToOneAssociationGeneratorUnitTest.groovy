/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.schema.association

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.ODataAssociation
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.lang3.StringUtils
import org.apache.olingo.odata2.api.edm.EdmMultiplicity
import org.apache.olingo.odata2.api.edm.provider.Association
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class OneToOneAssociationGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final String SOURCE = "MyProduct"
    private static final String TARGET = "MyUnit"
    private static final String NAVIGATION_PROPERTY = "navigationProperty"
    private static final String CATEGORY = "Category"

    def generator = new OneToOneAssociationGenerator()

    @Test
    def "is not applicable for null attribute descriptor"() {
        expect:
        !generator.isApplicable((TypeAttributeDescriptor) null)
    }

    @Test
    @Unroll
    def "is applicable is #expectedResult when #msg"() {
        given:
        def typeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            isCollection() >> collection
            isMap() >> map
            isLocalized() >> localized
            isPrimitive() >> primitive
        }

        expect:
        generator.isApplicable(typeAttributeDescriptor as TypeAttributeDescriptor) == expectedResult

        where:
        msg                                  | collection | map   | localized | primitive | expectedResult
        'primitive collection'               | true       | false | false     | true      | false
        'non-primitive collection'           | true       | false | false     | false     | false
        'primitive map'                      | false      | true  | false     | true      | false
        'non-primitive map'                  | false      | true  | false     | false     | false
        'primitive single value'             | false      | false | false     | true      | false
        'non-primitive single value'         | false      | false | false     | false     | true
        'primitive localized collection'     | true       | false | true      | true      | false
        'non-primitive localized collection' | true       | false | true      | false     | false
        'primitive localized map'            | false      | true  | true      | true      | false
        'non-primitive localized map'        | false      | true  | true      | false     | true
    }

    @Test
    def "generate for attribute descriptor"() {
        given:
        final TypeAttributeDescriptor descriptor = oneToOneAttributeDescriptor()
        descriptor.getAttributeName() >> NAVIGATION_PROPERTY

        final TypeDescriptor sourceTypeDescriptor = Stub(TypeDescriptor)
        sourceTypeDescriptor.getItemCode() >> SOURCE

        final TypeDescriptor targetTypeDescriptor = Stub(TypeDescriptor)
        targetTypeDescriptor.getItemCode() >> TARGET
        descriptor.getTypeDescriptor() >> sourceTypeDescriptor
        descriptor.getAttributeType() >> targetTypeDescriptor
        descriptor.reverse() >> Optional.empty()

        when:
        final Association generated = generator.generate(descriptor)

        then:
        final ODataAssociation association = new ODataAssociation(generated)
        association.isAssociationBetweenTypes(SOURCE, TARGET)
        association.hasName("FK_${SOURCE}_${NAVIGATION_PROPERTY}")
        association.hasSource(SOURCE)
        association.hasTarget(TARGET)
        association.hasSourceMultiplicity(EdmMultiplicity.ZERO_TO_ONE)
        association.hasTargetMultiplicity(EdmMultiplicity.ZERO_TO_ONE)
    }

    @Test
    def "generate association for descriptor with same source and target type"() {
        given:
        final TypeAttributeDescriptor descriptor = oneToOneAttributeDescriptor()
        descriptor.getAttributeName() >> NAVIGATION_PROPERTY

        final TypeDescriptor sourceTypeDescriptor = Stub(TypeDescriptor)
        sourceTypeDescriptor.getItemCode() >> CATEGORY

        final TypeDescriptor targetTypeDescriptor = Stub(TypeDescriptor)
        targetTypeDescriptor.getItemCode() >> CATEGORY
        descriptor.getTypeDescriptor() >> sourceTypeDescriptor
        descriptor.getAttributeType() >> targetTypeDescriptor

        descriptor.reverse() >> Optional.empty()

        when:
        final Association generated = generator.generate(descriptor)

        then:
        final ODataAssociation association = new ODataAssociation(generated)
        association.isAssociationBetweenTypes(CATEGORY, CATEGORY)
        association.hasName("FK_${CATEGORY}_${NAVIGATION_PROPERTY}")
        association.hasSource(CATEGORY)
        association.hasTarget(StringUtils.capitalize(NAVIGATION_PROPERTY))
        association.hasSourceMultiplicity(EdmMultiplicity.ZERO_TO_ONE)
        association.hasTargetMultiplicity(EdmMultiplicity.ZERO_TO_ONE)
    }

    private TypeAttributeDescriptor oneToOneAttributeDescriptor() {
        final TypeAttributeDescriptor descriptor = Stub(TypeAttributeDescriptor)
        descriptor.isCollection() >> false
        descriptor.isPrimitive() >> false
        return descriptor
    }
}
