/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;


@RunWith(MockitoJUnitRunner.class)
public class GroupIndexGroupAssignmentCellValueProviderTest
{

	private final GroupIndexGroupAssignmentCellValueProvider provider = new GroupIndexGroupAssignmentCellValueProvider();

	@Test
	public void shouldIndexBeRetrievedFromModel()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment.getClassFeatureGroup()).willReturn(group);
		final int index = 7;
		given(group.getIndex()).willReturn(index);

		// when
		final String output = provider.apply(groupAssignment);

		// then
		assertThat(output).isEqualTo(String.valueOf(index));
	}

	@Test
	public void shouldReturnEmptyValueWhenIndexNotFound()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment.getClassFeatureGroup()).willReturn(group);
		given(group.getIndex()).willReturn(-1);

		// when
		final String output = provider.apply(groupAssignment);

		// then
		assertThat(output).isEmpty();
	}

}
