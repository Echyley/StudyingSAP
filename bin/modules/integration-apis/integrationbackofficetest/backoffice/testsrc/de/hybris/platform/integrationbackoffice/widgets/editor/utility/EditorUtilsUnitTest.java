/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Treeitem;

import spock.lang.Issue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EditorUtilsUnitTest
{
	private static final String COLLECTION_TYPE = "CollectionItem";
	private static final String MAP_TYPE = "MapItem";
	private static final int AUTOCREATE_CHECKBOX_INDEX = 4;

	private static final List<String> labels = Arrays.asList("does", "not", "matter");

	@Mock(lenient = true)
	private ReadService readService;

	@Before
	public void setup()
	{
		lenient().doReturn(true).when(readService).isCollectionType(COLLECTION_TYPE);
		lenient().doReturn(true).when(readService).isMapType(MAP_TYPE);
	}

	@Test
	@Issue("IAPI-5157")
	public void autoCreateCheckboxIsEnabledForRequiredAttribute()
	{
		final ComposedTypeModel typeModel = mock(ComposedTypeModel.class);
		lenient().when(typeModel.getCode()).thenReturn("Catalog");
		lenient().when(typeModel.getAbstract()).thenReturn(false);

		final AbstractListItemDTO dto = TypeCreatorTestUtils.createListItemAttributeDTO("catalog", false, true, false,
				false, ListItemStructureType.NONE, typeModel);

		final Listitem listItem = EditorUtils.createListItem(dto, true, false, labels, true, readService);

		assertFalse(extractCheckbox(listItem).isDisabled());
	}

	@Test
	@Issue("IAPI-5157")
	public void autoCreateCheckboxIsDisabledForPrimitiveAttribute()
	{
		final AtomicTypeModel typeModel = mock(AtomicTypeModel.class);
		lenient().when(typeModel.getCode()).thenReturn("code");

		final AbstractListItemDTO dto = TypeCreatorTestUtils.createListItemAttributeDTO("code", false, true, true,
				false, ListItemStructureType.NONE, typeModel);
		final Listitem listItem = EditorUtils.createListItem(dto, false, false, labels, true, readService);

		assertTrue(extractCheckbox(listItem).isDisabled());
	}

	@Test
	@Issue("IAPI-5157")
	public void autoCreateCheckboxIsDisabledForAbstractAttribute()
	{
		final ComposedTypeModel typeModel = mock(ComposedTypeModel.class);
		lenient().when(typeModel.getCode()).thenReturn("AbstractCatalog");
		lenient().when(typeModel.getAbstract()).thenReturn(true);

		final AbstractListItemDTO dto = TypeCreatorTestUtils.createListItemAttributeDTO("catalog", false, true, true,
				false, ListItemStructureType.NONE, typeModel);
		final Listitem listItem = EditorUtils.createListItem(dto, true, false, labels, true, readService);

		assertTrue(extractCheckbox(listItem).isDisabled());
	}

	private Checkbox extractCheckbox(final Listitem listitem)
	{
		return (Checkbox) listitem.getChildren().get(AUTOCREATE_CHECKBOX_INDEX).getFirstChild();
	}

	private ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel model = mock(ComposedTypeModel.class);
		lenient().doReturn(code).when(model).getCode();
		return model;
	}

	@Test
	public void renameTreeitemTest()
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final IntegrationMapKeyDTO mapKeyDTO = mock(IntegrationMapKeyDTO.class);
		lenient().when(mapKeyDTO.getType()).thenReturn(composedTypeModel);
		lenient().when(mapKeyDTO.getCode()).thenReturn("Media");
		lenient().when(composedTypeModel.getCode()).thenReturn("Media");

		final TreeNodeData treeNodeData = new TreeNodeData("icon", null, mapKeyDTO);
		final Treeitem treeitem = new Treeitem();
		treeitem.setValue(treeNodeData);

		final ListItemAttributeDTO dto = mock(ListItemAttributeDTO.class);
		final String alias = "aliasOfIcon";
		lenient().when(dto.getAlias()).thenReturn(alias);

		final Treeitem actualTreeitem = EditorUtils.renameTreeitem(treeitem, dto);
		final String expected = alias + " [" + treeNodeData.getMapKeyDTO().getCode() + " : " + treeNodeData.getMapKeyDTO()
		                                                                                                   .getType()
		                                                                                                   .getCode() + "]";

		assertEquals(expected, actualTreeitem.getLabel());
	}

	@Test
	public void isClassificationAttributePresentTest()
	{
		final ClassAttributeAssignmentModel assignment = new ClassAttributeAssignmentModel();
		final ClassificationAttributeModel checkedAttribute = new ClassificationAttributeModel();
		final ClassificationClassModel classificationClassModel = new ClassificationClassModel();

		checkedAttribute.setCode("Built-in speakers, 2433");

		classificationClassModel.setCode("2910");

		assignment.setClassificationAttribute(checkedAttribute);
		assignment.setClassificationClass(classificationClassModel);

		final ListItemClassificationAttributeDTO dto1 = TypeCreatorTestUtils.createClassificationAttributeDTO("name1",
				"Builtinspeakers2433",
				ClassificationAttributeTypeEnum.BOOLEAN, "2910");
		final ListItemClassificationAttributeDTO dto2 = TypeCreatorTestUtils.createClassificationAttributeDTO("name2",
				"Built-in microphone, 1025",
				ClassificationAttributeTypeEnum.BOOLEAN, "2910");
		final ListItemClassificationAttributeDTO dto3 = TypeCreatorTestUtils.createClassificationAttributeDTO("name3",
				"Audio system, 442",
				ClassificationAttributeTypeEnum.BOOLEAN, "2910");

		final List<AbstractListItemDTO> dtos1 = new ArrayList<>();
		dtos1.add(dto1);
		dtos1.add(dto2);
		dtos1.add(dto3);

		final List<AbstractListItemDTO> dtos2 = new ArrayList<>();
		dtos2.add(dto2);
		dtos2.add(dto3);

		assertTrue(EditorUtils.isClassificationAttributePresent(assignment, dtos1));
		assertFalse(EditorUtils.isClassificationAttributePresent(assignment, dtos2));
	}
}
