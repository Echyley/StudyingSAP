/*
 *
 *  * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.data

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class SelectedClassificationAttributesDataUnitTest extends JUnitPlatformSpecification {
    @Shared
    def assignment = Stub(ClassAttributeAssignmentModel)
    @Shared
    def notMatters = true

    @Test
    def "constructor can handle null as parameter:assignment"() {
        when:
        def attributeData = new SelectedClassificationAttributesData(null, notMatters)

        then:
        attributeData.getAssignments() == []
    }

    @Test
    @Unroll
    def "#resultDescription is stored when creating SelectedClassificationAttributesData with #input as assignments"() {
        when:
        def attributeData = new SelectedClassificationAttributesData(input, notMatters)
        input.add(Stub(ClassAttributeAssignmentModel))

        then: "The reference is not stored"
        attributeData.getAssignments() != input

        where:
        input        | resultDescription
        []           | "An empty list"
        [assignment] | "A copy of assignments"
    }

    @Test
    def "Getter returns an unmodifiable list"() {
        given:
        def attributeData = new SelectedClassificationAttributesData(input as List, notMatters)

        when:
        attributeData.getAssignments().add(Stub(ClassAttributeAssignmentModel.class))

        then:
        thrown(UnsupportedOperationException.class)

        where:
        input << [null, []]
    }
}
