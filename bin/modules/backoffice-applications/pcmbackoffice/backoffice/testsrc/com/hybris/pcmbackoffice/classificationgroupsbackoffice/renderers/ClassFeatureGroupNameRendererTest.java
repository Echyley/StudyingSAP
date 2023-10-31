/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;


@RunWith(MockitoJUnitRunner.class)
public class ClassFeatureGroupNameRendererTest
{
	@InjectMocks
	private ClassFeatureGroupNameRenderer renderer;

	@Test
	public void shouldReturnGroupIdIfNameIsNull()
	{
		//given
		final String groupCode = "group_code";
		final ClassFeatureGroupModel classFeatureGroup = mock(ClassFeatureGroupModel.class);
		given(classFeatureGroup.getName()).willReturn(null);
		given(classFeatureGroup.getCode()).willReturn(groupCode);

		//when
		final String value = renderer.getValue(classFeatureGroup, mock(WidgetInstanceManager.class));

		//then
		assertThat(value).isEqualTo(groupCode);
	}

	@Test
	public void shouldReturnGroupName()
	{
		//given
		final String groupName = "group_name";
		final ClassFeatureGroupModel classFeatureGroup = mock(ClassFeatureGroupModel.class);
		given(classFeatureGroup.getName()).willReturn(groupName);

		//when
		final String value = renderer.getValue(classFeatureGroup, mock(WidgetInstanceManager.class));

		//then
		assertThat(value).isEqualTo(groupName);
	}
}
