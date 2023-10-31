/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationInfoTest
{
	@Mock
	private ClassAttributeAssignmentModel assignment;

	@Mock
	private ClassificationAttributeUnitModel classificationAttributeUnitModel;

	@Mock
	private Object value;

	@Spy
	@InjectMocks
	private ClassificationInfo classificationInfo;

	@Before
	public void setUp()
	{
		this.classificationInfo = new ClassificationInfo(assignment, value);
	}

	@Test
	public void shouldHaveRange()
	{
		// given
		when(assignment.getRange()).thenReturn(Boolean.TRUE);

		// when
		final boolean hasRange = classificationInfo.hasRange();

		// then
		assertThat(hasRange).isTrue();
	}

	@Test
	public void shouldBeLocalized()
	{
		// given
		when(assignment.getLocalized()).thenReturn(Boolean.TRUE);

		// when
		final boolean isLocalized = classificationInfo.isLocalized();

		// then
		assertThat(isLocalized).isTrue();
	}

	@Test
	public void shouldHaveUnit()
	{
		// given
		when(assignment.getUnit()).thenReturn(classificationAttributeUnitModel);

		// when
		final boolean isLocalized = classificationInfo.hasUnit();

		// then
		assertThat(isLocalized).isTrue();
	}

	@Test
	public void shouldDisplayUnit()
	{
		// given
		when(assignment.getUnit()).thenReturn(classificationAttributeUnitModel);

		// when
		final boolean isUnitDisplayed = classificationInfo.isUnitDisplayed();

		assertThat(isUnitDisplayed).isTrue();
	}

	@Test
	public void shouldBeMultiValue()
	{
		// given
		when(assignment.getMultiValued()).thenReturn(Boolean.TRUE);

		// when
		final boolean isMultiValue = classificationInfo.isMultiValue();

		// then
		assertThat(isMultiValue).isTrue();
	}
}
