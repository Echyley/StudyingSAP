/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.services

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel
import de.hybris.platform.catalog.model.classification.ClassificationClassModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.TypeModel
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultListItemAttributeDTOUpdater
import de.hybris.platform.testframework.JUnitPlatformSpecification

class DefaultListItemAttributeDTOUpdaterUnitTest extends JUnitPlatformSpecification {

    private final ListItemAttributeDTOUpdater updater = new DefaultListItemAttributeDTOUpdater()

    def "Test that the updateDTOs method overrides old definition with new one for single entry"() {
        given:
        def old = [createDTO("name1", false, false, false, "alias1", "q1")] as List
        def newer = [createDTO("name2", true, true, true, "alias2", "q1")] as List
        def size = old.size()
        def soloAttrIndex = 0

        when:
        def result = updater.updateDTOs(old, newer)

        then:
        result.size() == size
        final updatedDTO = result.get(soloAttrIndex) as ListItemAttributeDTO
        updatedDTO.getQualifier() == "q1"
        updatedDTO.getAlias() == "name2"
        updatedDTO.getTypeAlias() == "alias2"
        updatedDTO.isAutocreate()
        updatedDTO.isCustomUnique()
        updatedDTO.isSelected()
    }

    def "Test that the updateDTOs method does not modify DTOs when not present in old list"() {
        given:
        def old = [createDTO("name1", false, false, false, "alias1", "q1")] as List
        def newer = [createDTO("name2", true, true, true, "alias2", "q2")] as List
        def size = old.size()
        def oldIndex = 0

        when:
        def result = updater.updateDTOs(old, newer)

        then:
        result.size() == size

        final unchangedDTO = result.get(oldIndex) as ListItemAttributeDTO
        unchangedDTO.getQualifier() == "q1"
        unchangedDTO.getAlias() == "name1"
        unchangedDTO.getTypeAlias() == "alias1"
        !unchangedDTO.isAutocreate()
        !unchangedDTO.isCustomUnique()
        !unchangedDTO.isSelected()
    }

    def "Test that the updateDTOs method adds instances of implementations of AbstractListItemAttributeDTO (other than ListItemAttributeDTO) to the list while not modifying existing entries"() {
        given:
        def old = [createDTO("name1", false, false, false, "alias1", "q1")] as List
        def newer = [createClassificationDTO()] as List
        def size = old.size() + newer.size()
        def oldIndex = 0
        def newerIndex = 1

        when:
        def result = updater.updateDTOs(old, newer)

        then:
        result.size() == size

        final unchangedDTO = result.get(oldIndex) as ListItemAttributeDTO
        unchangedDTO.getQualifier() == "q1"
        unchangedDTO.getAlias() == "name1"
        unchangedDTO.getTypeAlias() == "alias1"
        !unchangedDTO.isAutocreate()
        !unchangedDTO.isCustomUnique()
        !unchangedDTO.isSelected()

        final classificationDTO = result.get(newerIndex)
        classificationDTO instanceof ListItemClassificationAttributeDTO

    }

    def "Testing that the updateDTOs method does not modify an existing entry if the new list is empty"() {
        given:
        def old = [createDTO("name1", false, false, false, "alias1", "q1")] as List
        def newer = [] as List
        def size = old.size()
        def oldIndex = 0

        when:
        def result = updater.updateDTOs(old, newer)

        then:
        result.size() == size

        final unchangedDTO = result.get(oldIndex) as ListItemAttributeDTO
        unchangedDTO.getQualifier() == "q1"
        unchangedDTO.getAlias() == "name1"
        unchangedDTO.getTypeAlias() == "alias1"
        !unchangedDTO.isAutocreate()
        !unchangedDTO.isCustomUnique()
        !unchangedDTO.isSelected()
    }

    def "Testing that the updateDTOs method does not add anything if old list is empty and the new list does not contain an instance of AbstractListItemAttributeDTO other than ListItemAttributeDTO"() {
        given:
        def old = [] as List
        def newer = [createDTO("name1", false, false, false, "alias1", "q1")] as List
        def size = old.size()

        when:
        def result = updater.updateDTOs(old, newer)

        then:
        result.size() == size
    }

    private ListItemAttributeDTO createDTO(final String name, final boolean autoCreate, final boolean unique, final boolean selected,
                                           final String alias, final String qualifier) {
        new ListItemAttributeDTOBuilder(createAttributeTypeDTO(qualifier))
                .withAttributeName(name)
                .withAutocreate(autoCreate)
                .withCustomUnique(unique)
                .withTypeAlias(alias)
                .withSelected(selected)
                .build()
    }

    private ListItemClassificationAttributeDTO createClassificationDTO() {
        new ListItemClassificationAttributeDTOBuilder(createCAA())
                .build()
    }

    private AttributeTypeDTO createAttributeTypeDTO(final String qualifier) {
        final type = createCTM()
        final adm = createADM(type, qualifier)

        new AttributeTypeDTOBuilder(adm)
                .withType(type)
                .build()
    }

    private ComposedTypeModel createCTM() {
        new ComposedTypeModel(
                code: "codeA"
        )
    }

    private AttributeDescriptorModel createADM(final TypeModel type, final String qualifier) {
        new AttributeDescriptorModel(
                attributeType: type,
                qualifier: qualifier
        )
    }

    private ClassAttributeAssignmentModel createCAA() {
        new ClassAttributeAssignmentModel(
                classificationClass: new ClassificationClassModel(
                        code: "codeA"
                ),
                classificationAttribute: new ClassificationAttributeModel(
                        code: "codeA"
                ),
                attributeType: ClassificationAttributeTypeEnum.BOOLEAN
        )
    }
}
