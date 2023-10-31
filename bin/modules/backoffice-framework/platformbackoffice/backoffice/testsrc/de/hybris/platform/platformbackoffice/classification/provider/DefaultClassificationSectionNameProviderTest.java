/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class DefaultClassificationSectionNameProviderTest
{

	@InjectMocks
	private DefaultClassificationSectionNameProvider provider;

	@Mock
	private LabelService labelService;

	@Test
	public void shouldGenerateEmptyNameWhenClassificationClassIsNull()
	{
		// given

		// when
		final String sectionName = provider.provide(null);

		// then
		assertThat(sectionName).isEmpty();
	}

	@Test
	public void shouldGenerateSectionNameForNotEmptyClassificationClass()
	{
		// given
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		given(labelService.getObjectLabel(classificationClass)).willReturn("Best label");

		// when
		final String sectionName = provider.provide(classificationClass);

		// then
		verify(labelService).getObjectLabel(classificationClass);

		assertThat(sectionName).isEqualTo("Best label");
	}
}
