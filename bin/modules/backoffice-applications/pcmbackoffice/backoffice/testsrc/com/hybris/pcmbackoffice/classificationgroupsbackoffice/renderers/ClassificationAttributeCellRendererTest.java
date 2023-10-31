/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationAttributeCellRendererTest
{
	@Mock
	private ModelService modelService;
	@InjectMocks
	private ClassificationAttributeCellRenderer renderer = new ClassificationAttributeCellRenderer();

	@Test
	public void shouldFindMatchGroupAssignment()
	{
		// given
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final ClassificationClassModel classificationClass = mockClassificationClass(wim);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		final ClassFeatureGroupAssignmentModel assignmentGroup = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel attribute = mock(ClassificationAttributeModel.class);
		given(classificationClass.getClassFeatureGroups()).willReturn(List.of(group));
		given(group.getClassFeatureGroupAssignments()).willReturn(List.of(assignmentGroup));
		given(assignmentGroup.getClassAttributeAssignment()).willReturn(assignment);
		given(assignment.getClassificationAttribute()).willReturn(attribute);

		// when
		final Optional<ClassFeatureGroupAssignmentModel> output = renderer.findMatchingGroupAssignment(attribute, wim);

		// then
		then(modelService).should().refresh(classificationClass);
		assertThat(output).isPresent();
	}

	@Test
	public void shouldReturnEmptyOptionalWhenFeatureIsNotAssignedToGroup()
	{
		// given
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final ClassificationClassModel classificationClass = mockClassificationClass(wim);
		given(classificationClass.getClassFeatureGroups()).willReturn(List.of());

		// when
		final Optional<ClassFeatureGroupAssignmentModel> output = renderer
				.findMatchingGroupAssignment(mock(ClassificationAttributeModel.class), wim);

		// then
		then(modelService).should().refresh(classificationClass);
		assertThat(output).isEmpty();
	}

	private ClassificationClassModel mockClassificationClass(final WidgetInstanceManager wim)
	{
		final WidgetModel widgetModel = mock(WidgetModel.class);
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		given(wim.getModel()).willReturn(widgetModel);
		given(widgetModel.getValue(any(), any())).willReturn(classificationClass);
		return classificationClass;
	}

}
