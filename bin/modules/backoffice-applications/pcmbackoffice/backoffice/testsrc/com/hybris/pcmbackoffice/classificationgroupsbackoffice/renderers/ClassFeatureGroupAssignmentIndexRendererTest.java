/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;


@RunWith(MockitoJUnitRunner.class)
public class ClassFeatureGroupAssignmentIndexRendererTest
{
	@InjectMocks
	private ClassFeatureGroupAssignmentIndexRenderer renderer;

	@Test
	public void shouldReturnEmptyValueIfIndexIsNull()
	{
		//given
		final ClassFeatureGroupAssignmentModel classFeatureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		given(classFeatureGroupAssignment.getIndex()).willReturn(null);

		//when
		final String value = renderer.getValue(classFeatureGroupAssignment, mock(WidgetInstanceManager.class));

		//then
		assertThat(value).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	public void shouldReturnEmptyValueIfIndexHasNegativeValue()
	{
		//given
		final ClassFeatureGroupAssignmentModel classFeatureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		given(classFeatureGroupAssignment.getIndex()).willReturn(-1);

		//when
		final String value = renderer.getValue(classFeatureGroupAssignment, mock(WidgetInstanceManager.class));

		//then
		assertThat(value).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	public void shouldReturnIndex()
	{
		//given
		final ClassFeatureGroupAssignmentModel classFeatureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		given(classFeatureGroupAssignment.getIndex()).willReturn(null);

		//when
		final String value = renderer.getValue(classFeatureGroupAssignment, mock(WidgetInstanceManager.class));

		//then
		assertThat(value).isEqualTo(StringUtils.EMPTY);
	}
}
