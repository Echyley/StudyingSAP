/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;


@RunWith(MockitoJUnitRunner.class)
public class IndexGroupAssignmentCellValueProviderTest
{

	private final IndexGroupAssignmentCellValueProvider provider = new IndexGroupAssignmentCellValueProvider();

	@Test
	public void shouldReturnCorrectIndex()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment02 = mock(ClassFeatureGroupAssignmentModel.class);

		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment02.getClassFeatureGroup()).willReturn(group);

		final ClassFeatureGroupAssignmentModel groupAssignment01 = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupAssignmentModel groupAssignment03 = mock(ClassFeatureGroupAssignmentModel.class);
		given(group.getClassFeatureGroupAssignments()).willReturn(List.of(groupAssignment01, groupAssignment02, groupAssignment03));

		// when
		final String output = provider.apply(groupAssignment02);

		// then
		assertThat(output).isEqualTo("2");
	}

	@Test
	public void shouldReturnEmptyValueWhenFeatureIsNotAssignedToAnyGroup()
	{
		// given
		final ClassFeatureGroupAssignmentModel model = mock(ClassFeatureGroupAssignmentModel.class);
		given(model.getClassFeatureGroup()).willReturn(null);

		// when
		final String output = provider.apply(model);

		// then
		assertThat(output).isEmpty();
	}

	@Test
	public void shouldReturnEmptyValueWhenFeatureIsAssignedToGroupButNotYetSaved()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment02 = mock(ClassFeatureGroupAssignmentModel.class);

		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment02.getClassFeatureGroup()).willReturn(group);

		final ClassFeatureGroupAssignmentModel groupAssignment01 = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupAssignmentModel groupAssignment03 = mock(ClassFeatureGroupAssignmentModel.class);
		given(group.getClassFeatureGroupAssignments()).willReturn(List.of(groupAssignment01, groupAssignment03));

		// when
		final String output = provider.apply(groupAssignment02);

		// then
		assertThat(output).isEmpty();
	}

}
