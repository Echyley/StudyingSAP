/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.data

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class RenameAttributeModalDataUnitTest extends JUnitPlatformSpecification {
    def attributes = List.of(Stub(AbstractListItemDTO))
    def listItemDTO = Stub(AbstractListItemDTO)
    def parentKey = Stub(IntegrationMapKeyDTO)
    def modalData = new RenameAttributeModalData(attributes, listItemDTO, parentKey)
    @Shared
    def listItemDTO2 = Stub(AbstractListItemDTO)

    @Test
    def "RenameAttributeModalData is initialized correctly"() {
        expect:
        modalData.getAttributes() == attributes
        modalData.getDto() == listItemDTO
        modalData.getParent() == parentKey
    }

    @Test
    def "constructor can handle null as parameter:attributes"() {
        when:
        def modalData = new RenameAttributeModalData(null, listItemDTO, parentKey)

        then: "The reference is not stored"
        modalData.getAttributes() == Collections.emptyList()
    }

    @Test
    @Unroll
    def "#resultDescription is stored when setting attributes with #input"() {
        when:
        def modalData = new RenameAttributeModalData(input, listItemDTO, parentKey)
        input.add(Stub(AbstractListItemDTO))

        then: "The reference is not stored"
        modalData.getAttributes() != input

        where:
        input          | resultDescription
        []             | "An empty list"
        [listItemDTO2] | "A copy of attributes"
    }

    @Test
    def "Getter returns an unmodifiable list"() {
        given:
        def modalData = new RenameAttributeModalData(input as List<AbstractListItemDTO>, listItemDTO, parentKey)

        when:
        modalData.getAttributes().add(Stub(AbstractListItemDTO))

        then:
        thrown(UnsupportedOperationException)

        where:
        input << [null, []]
    }

}
