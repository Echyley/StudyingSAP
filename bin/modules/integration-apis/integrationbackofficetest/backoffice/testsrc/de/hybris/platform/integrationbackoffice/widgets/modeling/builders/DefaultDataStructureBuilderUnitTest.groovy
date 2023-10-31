/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.builders

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel
import de.hybris.platform.catalog.model.classification.ClassificationClassModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.TypeModel
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTOBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorAttributesFilteringService
import de.hybris.platform.testframework.JUnitPlatformSpecification

import static de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils.createListItemAttributeDTO

@UnitTest
class DefaultDataStructureBuilderUnitTest extends JUnitPlatformSpecification {

    private static final ComposedTypeModel TYPE1 = new ComposedTypeModel()
    private static final ComposedTypeModel TYPE2 = new ComposedTypeModel()
    private static final ComposedTypeModel TYPE3 = new ComposedTypeModel()

    private ReadService rs = Stub(ReadService)
    private EditorAttributesFilteringService filteringService = Stub(EditorAttributesFilteringService)
    private ListItemAttributeDTOUpdater dtoUpdater = Stub(ListItemAttributeDTOUpdater)
    private DataStructureBuilder structureBuilder = new DefaultDataStructureBuilder(rs, filteringService, dtoUpdater)

    def "Test that the populateAttributesMap adds an attribute as #message when unique=#unique and optional=#optional"() {
        given:
        def mapKey = createMapKey("codeA")
        final descriptorModelSet = [createADM(TYPE1, "q1", unique, optional)] as Set
        filteringService.filterAttributesForAttributesMap(mapKey.getType()) >> descriptorModelSet
        def definition = new IntegrationObjectDefinition([:])

        when:
        def result = structureBuilder.populateAttributesMap(mapKey, definition)

        then:
        selected == result.getDefinitionMap().get(mapKey).first().isSelected()

        where:
        unique | optional | selected | message
        true   | true     | false    | "not selected"
        false  | true     | false    | "not selected"
        true   | false    | true     | "selected"
        false  | false    | false    | "not selected"
        null   | true     | false    | "not selected"
        null   | false    | false    | "not selected"
        true   | null     | true     | "selected"
        false  | null     | false    | "not selected"
    }

    def "Test that the populateAttributesMap method adds attributes to an existing definition and values are correct"() {
        given:
        def mapKey = createMapKey("codeB")
        def adm1 = createADM(TYPE1, "q1", false, false)
        def adm2 = createADM(TYPE2, "q2", true, false)
        def descriptorModelSet = [adm1, adm2] as Set
        def numberOfAttributes = descriptorModelSet.size()
        filteringService.filterAttributesForAttributesMap(mapKey.getType()) >> descriptorModelSet
        def map = createSimpleDefinitionMap()
        def definition = new IntegrationObjectDefinition(map)

        when:
        def result = structureBuilder.populateAttributesMap(mapKey, definition)

        then:
        def attributes = result.getAttributesByKey(mapKey)
        def types = [] as List
        attributes.forEach(a -> types.add(a.getType()))
        attributes.size() == numberOfAttributes
        types.containsAll(TYPE1, TYPE2)
    }

    def "Testing that the loadExistingDefinitions method overrides old definition correctly"() {
        given:
        def definitionMapCurrent = createSimpleDefinitionMap()
        def definitionCurrent = new IntegrationObjectDefinition(definitionMapCurrent)
        def definitionMapExisting = createSimpleDefinitionMap()
        def definitionExisting = new IntegrationObjectDefinition(definitionMapExisting)

        final dtoList = [createSimpleDTO("q1", TYPE2)] as List
        dtoUpdater.updateDTOs(_ as List<AbstractListItemDTO>, _ as List<AbstractListItemDTO>) >> dtoList

        when:
        def result = structureBuilder.loadExistingDefinitions(definitionExisting, definitionCurrent)

        then:
        def type = result.getDefinitionMap().values().first().first().getType()
        type == TYPE2
    }

    def "Testing that the ListItemAttributeDTO.class::isInstance filter in compileSubtypeDataSet returns only attributes of that type"() {
        given:
        def definitionMap = createDefinitionMap(true)

        def definition = new IntegrationObjectDefinition(definitionMap)
        def expectedSize = 2

        expect:
        expectedSize == structureBuilder.compileSubtypeDataSet(definition, [] as Set).size()
    }

    def "Testing the ListItemAttributeDTO.class::isSubtype filter in the compileSubtypeDataSet returns only those attributes with true values"() {
        given:
        def definitionMap = createDefinitionMap(false)
        def definition = new IntegrationObjectDefinition(definitionMap)
        def expectedSize = 2

        expect:
        expectedSize == structureBuilder.compileSubtypeDataSet(definition, [] as Set).size()
    }

    def "Testing that the findSubtypeMatch match returns correct type (or NULL if no criteria match)"() {
        given:
        def mapKey = createMapKey(key)

        TYPE1.getCode() >> "type1"
        TYPE2.getCode() >> "type2"
        TYPE3.getCode() >> "type3"
        rs.getComposedTypeModelFromTypeModel(TYPE1) >> TYPE1
        rs.getComposedTypeModelFromTypeModel(TYPE2) >> TYPE2
        rs.getComposedTypeModelFromTypeModel(TYPE3) >> TYPE3

        def mapKeyA = createMapKey("codeA")
        def mapKeyB = createMapKey("codeB")

        final Set<SubtypeData> subtypeDataSet = createSubtypeDataSet(mapKeyA, mapKeyB, TYPE1, TYPE2, TYPE3)

        expect:
        expected == structureBuilder.findSubtypeMatch(mapKey, qualifier, type, subtypeDataSet)

        where:
        key     | qualifier | type  | expected
        "codeA" | "q1"      | TYPE1 | TYPE1
        "codeA" | "q1"      | TYPE3 | TYPE3
        "codeA" | "q9"      | TYPE1 | null
        "codeA" | "q1"      | TYPE2 | null
        "codeB" | "q1"      | TYPE1 | null
        "codeB" | "q2"      | TYPE2 | TYPE2
        "codeC" | "q1"      | TYPE1 | null
    }

    private Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> createSimpleDefinitionMap() {
        final IntegrationMapKeyDTO key = createMapKey("codeA")

        [(key): [createSimpleDTO('q1', TYPE1)]]
    }

    private Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> createDefinitionMap(final boolean withClassification) {
        final mapKeyDTO = createMapKey("codeA")
        final dto1 = Stub(ListItemAttributeDTO) {
            isSubType() >> true
            getType() >> TYPE1
            getBaseType() >> TYPE1
            getAlias() >> "a1"
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getQualifier() >> "adq1"
            }
        }
        final dto2 = Stub(ListItemAttributeDTO) {
            isSubType() >> true
            getType() >> TYPE2
            getBaseType() >> TYPE2
            getAlias() >> "a2"
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getQualifier() >> "adq1"
            }
        }
        final dto3
        if (withClassification) {
            dto3 = Stub(ListItemClassificationAttributeDTO)
        } else {
            dto3 = Stub(ListItemAttributeDTO) {
                isSubType() >> false
            }
        }
        final dtoList = [dto1, dto2, dto3] as List
        [(mapKeyDTO): dtoList]
    }

    private Set<SubtypeData> createSubtypeDataSet(final IntegrationMapKeyDTO mapKeyA, final IntegrationMapKeyDTO mapKeyB,
                                                  final ComposedTypeModel type1, final ComposedTypeModel type2, final ComposedTypeModel type3) {
        final ListItemAttributeDTO dto1 = createSimpleDTO("q1", type1)
        final ListItemAttributeDTO dto2 = createSimpleDTO("q2", type2)
        final ListItemAttributeDTO dto3 = createSimpleDTO("q1", type3)
        final ListItemAttributeDTO dto4 = createSimpleDTO("q2", type1)

        final SubtypeData s1 = new SubtypeData(mapKeyA, dto1)
        final SubtypeData s2 = new SubtypeData(mapKeyA, dto2)
        final SubtypeData s3 = new SubtypeData(mapKeyA, dto3)
        final SubtypeData s4 = new SubtypeData(mapKeyA, dto4)
        final SubtypeData s5 = new SubtypeData(mapKeyB, dto2)
        final SubtypeData s6 = new SubtypeData(mapKeyB, dto3)
        final SubtypeData s7 = new SubtypeData(mapKeyB, dto4)

        [s1, s2, s3, s4, s5, s6, s7] as Set
    }

    private ListItemAttributeDTO createSimpleDTO(final String qualifier, final typeModel) {
        return createListItemAttributeDTO(qualifier, false, false,
                false, false, ListItemStructureType.NONE, typeModel);
    }

    private IntegrationMapKeyDTO createMapKey(final String code) {
        new IntegrationMapKeyDTO(new ComposedTypeModel(), code)
    }

    private AttributeDescriptorModel createADM(final TypeModel type, final String qualifier, final Boolean unique, final Boolean optional) {
        new AttributeDescriptorModel(
                attributeType: type,
                qualifier: qualifier,
                unique: unique,
                optional: optional
        )
    }
}
