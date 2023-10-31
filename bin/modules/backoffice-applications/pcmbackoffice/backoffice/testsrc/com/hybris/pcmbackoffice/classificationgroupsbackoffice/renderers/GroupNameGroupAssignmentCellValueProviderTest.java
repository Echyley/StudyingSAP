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
public class GroupNameGroupAssignmentCellValueProviderTest
{

	private final GroupNameGroupAssignmentCellValueProvider provider = new GroupNameGroupAssignmentCellValueProvider();

	@Test
	public void shouldReturnCodeWhenNameIsEmpty()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment.getClassFeatureGroup()).willReturn(group);
		final String code = "group01";
		given(group.getName()).willReturn(null);
		given(group.getCode()).willReturn(code);

		// when
		final String output = provider.apply(groupAssignment);

		// then
		assertThat(output).isEqualTo(code);
	}

	@Test
	public void shouldReturnNameWhenItIsNotEmpty()
	{
		// given
		final ClassFeatureGroupAssignmentModel groupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassFeatureGroupModel group = mock(ClassFeatureGroupModel.class);
		given(groupAssignment.getClassFeatureGroup()).willReturn(group);
		final String name = "group 01";
		final String code = "group01";
		given(group.getName()).willReturn(name);

		// when
		final String output = provider.apply(groupAssignment);

		// then
		assertThat(output).isEqualTo(name);
	}

}
