/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;


@RunWith(MockitoJUnitRunner.class)
public class ClassAttributeAssignmentCellRendererTest
{

	private ClassAttributeAssignmentCellRenderer renderer = new ClassAttributeAssignmentCellRenderer();

	@Test
	public void shouldFindMatchGroupAssignment()
	{
		// given
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		final ClassFeatureGroupAssignmentModel assignmentGroup = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(classificationClass.getClassFeatureGroups()).willReturn(List.of(group));
		given(group.getClassFeatureGroupAssignments()).willReturn(List.of(assignmentGroup));
		given(assignmentGroup.getClassAttributeAssignment()).willReturn(assignment);
		given(assignment.getClassificationClass()).willReturn(classificationClass);

		// when
		final Optional<ClassFeatureGroupAssignmentModel> output = renderer.findMatchingGroupAssignment(assignment);

		// then
		assertThat(output).isPresent();
	}

	@Test
	public void shouldReturnEmptyOptionalWhenFeatureIsNotAssignedToGroup()
	{
		// given
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		given(assignment.getClassificationClass()).willReturn(classificationClass);
		given(classificationClass.getClassFeatureGroups()).willReturn(List.of());

		// when
		final Optional<ClassFeatureGroupAssignmentModel> output = renderer.findMatchingGroupAssignment(assignment);

		// then
		assertThat(output).isEmpty();
	}
}
