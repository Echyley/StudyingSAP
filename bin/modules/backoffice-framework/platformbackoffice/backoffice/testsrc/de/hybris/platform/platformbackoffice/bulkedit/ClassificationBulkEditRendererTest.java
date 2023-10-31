/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.bulkedit.renderer.BulkEditRenderer;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import org.zkoss.zul.Checkbox;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationBulkEditRendererTest
{
	@Mock
	ObjectFacade objectFacade;
	@InjectMocks
	ClassificationBulkEditRenderer renderer;

	private ClassificationClassModel classificationClass;

	@Before
	public void setUp()
	{
		classificationClass = mock(ClassificationClassModel.class);
	}

	@Test
	public void shouldSortAttributesByPosition() throws ObjectNotFoundException
	{
		//given
		final Attribute attribute1 = createAttribute("attribute1", 1, null);
		final Attribute attribute2 = createAttribute("attribute2", 2, null);
		final Attribute attribute3 = createAttribute("attribute3", 3, null);
		final List<Attribute> attributeList = List.of(attribute3, attribute2, attribute1);

		//when
		final Map<ClassificationClassModel, List<Attribute>> result = renderer.groupAttributesByClassificationClass(attributeList);

		//then
		assertThat(result.get(classificationClass))
				.as("If attribute's position if not empty then attributes are sorted by position value")
				.containsExactly(attribute1, attribute2, attribute3);
	}

	@Test
	public void shouldSortAttributesByName() throws ObjectNotFoundException
	{
		//given
		final Attribute attribute1 = createAttribute("attribute1", null, "attributeA");
		final Attribute attribute2 = createAttribute("attribute2", null, "attributeB");
		final Attribute attribute3 = createAttribute("attribute3", null, "attributeC");
		final List<Attribute> attributeList = List.of(attribute3, attribute2, attribute1);

		//when
		final Map<ClassificationClassModel, List<Attribute>> result = renderer.groupAttributesByClassificationClass(attributeList);

		//then
		assertThat(result.get(classificationClass))
				.as("If attribute's position is null then attributes are sorted alphabetically by ClassificationAttriubte's code")
				.containsExactly(attribute1, attribute2, attribute3);
	}

	@Test
	public void shouldSortAttributesFirstAtPositionThenByName() throws ObjectNotFoundException
	{
		//given
		final Attribute attribute1 = createAttribute("attribute1", null, "attributeA");
		final Attribute attribute2 = createAttribute("attribute2", null, "attributeB");
		final Attribute attribute3 = createAttribute("attribute3", 3, null);
		final List<Attribute> attributeList = List.of(attribute3, attribute2, attribute1);

		//when
		final Map<ClassificationClassModel, List<Attribute>> result = renderer.groupAttributesByClassificationClass(attributeList);

		//then
		assertThat(result.get(classificationClass)).as(
				"If more than one attribute has not fulfilled position then such attributes are sorted alphabetically by ClassificationAttriubte's code")
				.containsExactly(attribute3, attribute1, attribute2);
	}

	@Test
	public void shouldSortAttributesByPositionThenByNameIfPositionsAreTheSame() throws ObjectNotFoundException
	{
		//given
		final Attribute attribute1 = createAttribute("attribute1", 0, "attributeA");
		final Attribute attribute2 = createAttribute("attribute2", 0, "attributeB");
		final Attribute attribute3 = createAttribute("attribute3", null, "aaaFirstInAlphabet");
		final List<Attribute> attributeList = List.of(attribute3, attribute2, attribute1);

		//when
		final Map<ClassificationClassModel, List<Attribute>> result = renderer.groupAttributesByClassificationClass(attributeList);

		//then
		assertThat(result.get(classificationClass)).as(
				"If more than one attribute has the same position then such attributes are sorted alphabetically by ClassificationAttriubte's code")
				.containsExactly(attribute1, attribute2, attribute3);
	}

	@Test
	public void shouldRenderClearSwitchForNonMandatoryAttribute() throws ObjectNotFoundException
	{
		//given
		final String qualifier = "code";
		final Attribute attribute = mock(Attribute.class);
		when(attribute.getQualifier()).thenReturn(qualifier);

		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		when(classAttributeAssignment.getMandatory()).thenReturn(false);

		final ClassificationBulkEditForm classificationBulkEditForm = new ClassificationBulkEditForm();
		classificationBulkEditForm.getClassificationAttributesForm().setChosenAttributes(Sets.newHashSet(attribute));

		when(objectFacade.load(qualifier)).thenReturn(classAttributeAssignment);

		//when
		final Optional<Checkbox> clearAttributeSwitch = renderer.createClearAttributeSwitch(null, attribute, classificationBulkEditForm);

		//then
		assertThat(clearAttributeSwitch).isNotEmpty();
	}

	@Test
	public void shouldNotRenderClearSwitchForMandatoryAttribute() throws ObjectNotFoundException
	{
		//given
		final String qualifier = "code";
		final Attribute attribute = mock(Attribute.class);
		when(attribute.getQualifier()).thenReturn(qualifier);

		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		when(classAttributeAssignment.getMandatory()).thenReturn(true);

		final ClassificationBulkEditForm classificationBulkEditForm = new ClassificationBulkEditForm();
		classificationBulkEditForm.getClassificationAttributesForm().setChosenAttributes(Sets.newHashSet(attribute));

		when(objectFacade.load(qualifier)).thenReturn(classAttributeAssignment);

		//when
		final Optional<Checkbox> clearAttributeSwitch = renderer.createClearAttributeSwitch(null, attribute, classificationBulkEditForm);

		//then
		assertThat(clearAttributeSwitch).isEmpty();
	}

	private Attribute createAttribute(final String qualifier, final Integer position, final String displayName)
			throws ObjectNotFoundException
	{
		final Attribute attribute = mock(Attribute.class);
		final ClassAttributeAssignmentModel attributeModel = mock(ClassAttributeAssignmentModel.class);

		when(attribute.getQualifier()).thenReturn(qualifier);
		when(attribute.getDisplayName()).thenReturn(displayName);
		when(attributeModel.getClassificationClass()).thenReturn(classificationClass);
		when(attributeModel.getPosition()).thenReturn(position);
		when(objectFacade.load(qualifier)).thenReturn(attributeModel);

		return attribute;
	}
}
