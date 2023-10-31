/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.model.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.compare.ObjectAttributeComparator;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeMapComparatorTest
{

	@Spy
	@InjectMocks
	private BackofficeMapComparator backofficeMapComparator;

	@Mock
	private ObjectAttributeComparator<Object> objectAttributeComparator;

	@Mock
	private FeatureValue featureValue1;

	@Mock
	private FeatureValue featureValue2;

	@Mock
	private Object value1;

	@Mock
	private Object value2;

	@Mock
	private ClassificationAttributeUnitModel unit1;

	@Mock
	private ClassificationAttributeUnitModel unit2;

	private final PK pk1 = PK.fromLong(1);

	private final PK pk2 = PK.fromLong(2);

	private Map<Locale, FeatureValue> valuesMap1;

	private Map<Locale, FeatureValue> valuesMap2;

	private final Locale locale1 = new Locale("en");

	private final Locale locale2 = new Locale("fr");

	private static final String DESCRIPTION_1 = "description1";

	private static final String DESCRIPTION_2 = "description2";


	@Before
	public void setUp()
	{
		when(featureValue1.getValue()).thenReturn(value1);
		when(featureValue1.getUnit()).thenReturn(unit1);


		when(featureValue2.getValue()).thenReturn(value2);
		when(featureValue2.getUnit()).thenReturn(unit2);


		valuesMap1 = new HashMap<>();
		valuesMap2 = new HashMap<>();
	}


	@Test
	public void shouldReturnTrueWhenEqualFeatureValuesMapCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(true);
		when(objectAttributeComparator.isEqual(unit1, unit2)).thenReturn(true);
		valuesMap1.put(locale1, featureValue1);
		valuesMap2.put(locale1, featureValue2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentFeatureValuesCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(false);
		valuesMap1.put(locale1, featureValue1);
		valuesMap2.put(locale1, featureValue2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentFeatureUnitsCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(true);
		when(objectAttributeComparator.isEqual(unit1, unit2)).thenReturn(false);
		valuesMap1.put(locale1, featureValue1);
		valuesMap2.put(locale1, featureValue2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenFeatureValuesMapWithSameFeatureValuesListCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(true);
		when(objectAttributeComparator.isEqual(unit1, unit2)).thenReturn(true);
		final List<FeatureValue> featureValueList1 = new ArrayList(Collections.singleton(featureValue1));
		final List<FeatureValue> featureValueList2 = new ArrayList(Collections.singleton(featureValue2));
		final Map<Locale, List> valuesMap1 = new HashMap<>();
		final Map<Locale, List> valuesMap2 = new HashMap<>();
		valuesMap1.put(locale1, featureValueList1);
		valuesMap2.put(locale1, featureValueList2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentFeatureValueUnitsListCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(true);
		when(objectAttributeComparator.isEqual(unit1, unit2)).thenReturn(false);
		final List<FeatureValue> featureValueList1 = new ArrayList(Collections.singleton(featureValue1));
		final List<FeatureValue> featureValueList2 = new ArrayList(Collections.singleton(featureValue2));
		final Map<Locale, List> valuesMap1 = new HashMap<>();
		final Map<Locale, List> valuesMap2 = new HashMap<>();
		valuesMap1.put(locale1, featureValueList1);
		valuesMap2.put(locale1, featureValueList2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentFeatureValuesListCompared()
	{
		//given
		when(objectAttributeComparator.isEqual(value1, value2)).thenReturn(false);
		final List<FeatureValue> featureValueList1 = new ArrayList(Collections.singleton(featureValue1));
		final List<FeatureValue> featureValueList2 = new ArrayList(Collections.singleton(featureValue2));
		final Map<Locale, List> valuesMap1 = new HashMap<>();
		final Map<Locale, List> valuesMap2 = new HashMap<>();
		valuesMap1.put(locale1, featureValueList1);
		valuesMap2.put(locale1, featureValueList2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentFeatureValuesListSizeCompared()
	{
		//given
		final List<FeatureValue> featureValueList1 = new ArrayList(Collections.singleton(featureValue1));
		final List<FeatureValue> featureValueList2 = Collections.emptyList();
		final Map<Locale, List> valuesMap1 = new HashMap<>();
		final Map<Locale, List> valuesMap2 = new HashMap<>();
		valuesMap1.put(locale1, featureValueList1);
		valuesMap2.put(locale1, featureValueList2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentTypeListCompared()
	{
		//given
		final List<FeatureValue> featureValueList1 = new ArrayList(Collections.singleton(featureValue1));
		final List<FeatureValue> featureValueList2 = new ArrayList(Collections.singleton("test"));
		final Map<Locale, List> valuesMap1 = new HashMap<>();
		final Map<Locale, List> valuesMap2 = new HashMap<>();
		valuesMap1.put(locale1, featureValueList1);
		valuesMap2.put(locale1, featureValueList2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFeatureValuesMapWithDifferentLocalesCompared()
	{
		//given
		valuesMap1.put(locale1, featureValue1);
		valuesMap2.put(locale2, featureValue2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(valuesMap1, valuesMap2);

		//then
		assertThat(isEqual).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenTwoEqualNonFeatureValuesCompared()
	{
		//given
		final Map<Locale, String> stringMap1 = new HashMap<>();
		stringMap1.put(locale1, DESCRIPTION_1);

		final Map<Locale, String> stringMap2 = new HashMap<>();
		stringMap2.put(locale1, DESCRIPTION_1);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(stringMap1, stringMap2);

		//then
		assertThat(isEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenTwoNonEqualNonFeatureValuesCompared()
	{
		//given
		final Map<Locale, String> stringMap1 = new HashMap<>();
		stringMap1.put(locale1, DESCRIPTION_1);

		final Map<Locale, String> stringMap2 = new HashMap<>();
		stringMap2.put(locale1, DESCRIPTION_2);

		//when
		final boolean isEqual = backofficeMapComparator.isEqual(stringMap1, stringMap2);

		//then
		assertThat(isEqual).isFalse();
	}


}
