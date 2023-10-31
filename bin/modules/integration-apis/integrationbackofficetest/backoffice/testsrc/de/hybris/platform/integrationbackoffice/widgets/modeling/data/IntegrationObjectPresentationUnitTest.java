/*
 *
 *  * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectItemTypeMatchService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

@UnitTest
public class IntegrationObjectPresentationUnitTest
{
    final DataStructureBuilder dataStructureBuilder = mock(DataStructureBuilder.class);
    final IntegrationObjectItemTypeMatchService itemTypeMatchService = mock(IntegrationObjectItemTypeMatchService.class);
    final IntegrationObjectPresentation presentation =
            new IntegrationObjectPresentation(dataStructureBuilder, itemTypeMatchService);

    final SubtypeData subtypeData = mock(SubtypeData.class);

    @Test
    public void testAssignSubtypeDataSetToNull()
    {
        presentation.setSubtypeDataSet(null);

        assertEquals(Collections.emptySet(), presentation.getSubtypeDataSet());
    }

    @Test
    public void testSubtypeDataSetEscapeReference()
    {
        presentation.setSubtypeDataSet(Set.of(subtypeData));
        final Set<SubtypeData> copy = presentation.getSubtypeDataSet();
        copy.clear();

        assertFalse(presentation.getSubtypeDataSet().isEmpty());
    }

    @Test
    public void constructDuplicateMapNoDuplicatesTest()
    {
        final ComposedTypeModel product = TypeCreatorTestUtils.composedTypeModel("Product");
        final ComposedTypeModel variantProduct = TypeCreatorTestUtils.composedTypeModel("VariantProduct");
        final IntegrationMapKeyDTO productMapKey = new IntegrationMapKeyDTO(product, "Product");
        final IntegrationMapKeyDTO variantMapKey = new IntegrationMapKeyDTO(variantProduct, "VariantProduct");

        final ListItemClassificationAttributeDTO dto1 = TypeCreatorTestUtils.createClassificationAttributeDTO("name1", "q1",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");
        final ListItemClassificationAttributeDTO dto2 = TypeCreatorTestUtils.createClassificationAttributeDTO("name2", "q2",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");
        final ListItemClassificationAttributeDTO dto3 = TypeCreatorTestUtils.createClassificationAttributeDTO("name3", "q3",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");

        dto1.setSelected(true);
        dto2.setSelected(true);
        dto3.setSelected(true);

        final List<AbstractListItemDTO> dtos = new ArrayList<>();
        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);

        final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> map = new HashMap<>();
        map.put(productMapKey, dtos);
        map.put(variantMapKey, dtos);
        final IntegrationObjectDefinition definition = new IntegrationObjectDefinition(map);

        presentation.setCurrentAttributesMap(definition);
        presentation.setSelectedTypeInstance(productMapKey);

        final IntegrationObjectDefinitionDuplicationMap duplicationMap = presentation.compileDuplicationMap();

        assertTrue(duplicationMap.getDuplicationMap().isEmpty());
    }

    @Test
    public void constructDuplicateMapPresentDuplicatesTest()
    {
        final ComposedTypeModel product = TypeCreatorTestUtils.composedTypeModel("Product");
        final ComposedTypeModel variantProduct = TypeCreatorTestUtils.composedTypeModel("VariantProduct");
        final IntegrationMapKeyDTO productMapKey = new IntegrationMapKeyDTO(product, "Product");
        final IntegrationMapKeyDTO variantMapKey = new IntegrationMapKeyDTO(variantProduct, "VariantProduct");
        final ListItemClassificationAttributeDTO dto1 = TypeCreatorTestUtils.createClassificationAttributeDTO("name1", "q1",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");
        final ListItemClassificationAttributeDTO dto2 = TypeCreatorTestUtils.createClassificationAttributeDTO("name1", "q2",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");
        final ListItemClassificationAttributeDTO dto3 = TypeCreatorTestUtils.createClassificationAttributeDTO("name3", "q3",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");
        final ListItemClassificationAttributeDTO dto4 = TypeCreatorTestUtils.createClassificationAttributeDTO("name4", "q4",
                ClassificationAttributeTypeEnum.BOOLEAN, "type");


        dto1.setSelected(true);
        dto2.setSelected(true);
        dto3.setSelected(true);
        dto4.setSelected(true);

        final List<AbstractListItemDTO> dtos1 = new ArrayList<>();
        dtos1.add(dto1);
        dtos1.add(dto2);
        dtos1.add(dto3);

        final List<AbstractListItemDTO> dtos2 = new ArrayList<>();
        dtos2.add(dto1);
        dtos2.add(dto3);
        dtos2.add(dto4);

        final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> map = new HashMap<>();
        map.put(productMapKey, dtos1);
        map.put(variantMapKey, dtos2);
        final IntegrationObjectDefinition definition = new IntegrationObjectDefinition(map);

        presentation.setCurrentAttributesMap(definition);
        presentation.setSelectedTypeInstance(productMapKey);

        final IntegrationObjectDefinitionDuplicationMap duplicationMap = presentation.compileDuplicationMap();

        assertEquals(1, duplicationMap.getDuplicationMap().size());
        assertEquals(1, duplicationMap.getDuplicateAttributesByKey(productMapKey).keySet().size());
        assertEquals(2, duplicationMap.getDuplicateAttributesByKey(productMapKey).get("name1").size());

        final ListItemClassificationAttributeDTO firstDupEntry =
                (ListItemClassificationAttributeDTO) duplicationMap.getDuplicateAttributesByKey(productMapKey)
                        .get("name1")
                        .get(0);
        final ListItemClassificationAttributeDTO secondDupEntry =
                (ListItemClassificationAttributeDTO) duplicationMap.getDuplicateAttributesByKey(productMapKey)
                        .get("name1")
                        .get(1);

        assertEquals("q1", firstDupEntry.getClassificationAttributeCode());
        assertEquals("q2", secondDupEntry.getClassificationAttributeCode());
    }
}
