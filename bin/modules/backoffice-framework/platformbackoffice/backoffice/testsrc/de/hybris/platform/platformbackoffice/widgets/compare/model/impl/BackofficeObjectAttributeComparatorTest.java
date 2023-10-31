/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.model.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.FeatureValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.util.Range;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeObjectAttributeComparatorTest
{

	private BackofficeObjectAttributeComparator backofficeObjectAttributeComparator = new BackofficeObjectAttributeComparator();


	@Mock
	private FeatureValue featureValue1;

	@Mock
	private FeatureValue featureValue2;

	@Mock
	private FeatureValue start1;

	@Mock
	private FeatureValue end1;

	@Mock
	private FeatureValue start2;

	@Mock
	private FeatureValue end2;

	@Mock
	private Range range1;

	@Mock
	private Range range2;

	@Mock
	private Object value1;

	@Mock
	private Object value2;

	@Mock
	private Object value3;

	@Mock
	private Object value4;


	@Mock
	private ClassificationAttributeUnitModel unit1;

	@Mock
	private ClassificationAttributeUnitModel unit2;


	@Test
	public void shouldReturnFalseWhenIfComparedFeatureValuesHaveNotEqualValues()
	{
		//given
		mockFeatures();

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(featureValue1, featureValue2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenComparedFeatureValuesHaveEqualValuesButNotEqualUnits()
	{
		//given
		mockFeatures();
		when(featureValue2.getValue()).thenReturn(value1);

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(featureValue1, featureValue2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenComparedFeatureValuesHaveEqualValuesAndEqualUnits()
	{
		//given
		mockFeatures();
		when(featureValue2.getValue()).thenReturn(value1);
		when(featureValue2.getUnit()).thenReturn(unit1);

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(featureValue1, featureValue2);

		//then
		assertThat(isEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenRangesStartsAreNotEqualAndEndsAreEqual()
	{
		//given
		mockRanges();

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(range1, range2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenRangesEndsAreNotEqualAndStartsAreEqual()
	{
		//given
		mockRanges();
		when(range2.getStart()).thenReturn(start1);

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(range1, range2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenRangesStartsAreEqualAndEndsAreEqual()
	{
		//given
		mockRanges();
		when(range2.getStart()).thenReturn(start1);
		when(range2.getEnd()).thenReturn(end1);

		//when
		final boolean isEqual = backofficeObjectAttributeComparator.isEqual(range1, range2);

		//then
		assertThat(isEqual).isTrue();
	}

	private void mockFeatures()
	{
		when(featureValue1.getValue()).thenReturn(value1);
		when(featureValue1.getUnit()).thenReturn(unit1);

		when(featureValue2.getValue()).thenReturn(value2);
		when(featureValue2.getUnit()).thenReturn(unit2);
	}

	private void mockRanges()
	{
		when(start1.getValue()).thenReturn(value1);
		when(start1.getUnit()).thenReturn(unit1);

		when(start2.getValue()).thenReturn(value2);

		when(end1.getValue()).thenReturn(value3);
		when(end1.getUnit()).thenReturn(unit1);

		when(end2.getValue()).thenReturn(value4);

		when(range1.getStart()).thenReturn(start1);
		when(range1.getEnd()).thenReturn(end1);

		when(range2.getStart()).thenReturn(start2);
		when(range2.getEnd()).thenReturn(end2);
	}

}