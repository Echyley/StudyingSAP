/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.core.search;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.fulltextsearch.FullTextSearchStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeSearchFilterValidationStrategyTest
{
	private static final String TYPE_CODE = "typeCode";
	private static final String FILTER_NAME = "filterName";
	private static final String LOCALIZED_FILTER_NAME = "localizedFilterName";

	@Spy
	@InjectMocks
	private BackofficeSearchFilterValidationStrategy testSubject;

	@Mock
	private FullTextSearchStrategy searchStrategy;

	@Mock
	private TypeFacade typeFacade;

	@Mock
	private Object value;


	@Before
	public void setUp()
	{
		when(typeFacade.getType(value)).thenReturn(Object.class.getName());
		doReturn(true).when(testSubject).isLocalizedProperty(TYPE_CODE, LOCALIZED_FILTER_NAME);
	}

	@Test
	public void shouldReturnInvalidWhenThereIsNoIndexedProperties()
	{
		// given
		when(searchStrategy.getFieldType(TYPE_CODE, FILTER_NAME)).thenReturn(null);

		// when
		final boolean isValid = testSubject.isValid(TYPE_CODE, FILTER_NAME, value);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	public void shouldReturnInvalidWhenFilterTypeDoesNotMatchValueType()
	{
		// given
		when(searchStrategy.getFieldType(TYPE_CODE, FILTER_NAME)).thenReturn("SomeOtherType");

		// when
		final boolean isValid = testSubject.isValid(TYPE_CODE, FILTER_NAME, value);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	public void shouldReturnValidWhenFilterTypeMatchesValueType()
	{
		// given
		when(searchStrategy.getFieldType(TYPE_CODE, FILTER_NAME)).thenReturn(Object.class.getName());

		// when
		final boolean isValid = testSubject.isValid(TYPE_CODE, FILTER_NAME, value);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	public void shouldReturnInvalidWhenFilterTypeIsLocalizedAndFilterValueIsNotLocalized()
	{
		// given
		Mockito.lenient().when(searchStrategy.getFieldType(TYPE_CODE, FILTER_NAME)).thenReturn(Object.class.getName());
		when(searchStrategy.isLocalized(TYPE_CODE, FILTER_NAME)).thenReturn(true);

		// when
		final boolean isValid = testSubject.isValid(TYPE_CODE, FILTER_NAME, value);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	public void shouldReturnInvalidWhenFilterTypeIsNotLocalizedAndFilterValueIsLocalized()
	{
		// given
		Mockito.lenient().when(searchStrategy.getFieldType(TYPE_CODE, FILTER_NAME)).thenReturn(Object.class.getName());
		when(searchStrategy.isLocalized(TYPE_CODE, FILTER_NAME)).thenReturn(false);

		// when
		final boolean isValid = testSubject.isValid(TYPE_CODE, FILTER_NAME, provideLocalizedValue());

		// then
		assertThat(isValid).isFalse();
	}

	@Test

	public void shouldReturnTrueForNonLocalizedFieldWithNull()
	{
		//when
		final boolean valid = testSubject.isValid(TYPE_CODE, FILTER_NAME, null, ValueComparisonOperator.IS_EMPTY);

		//then
		assertThat(valid).isTrue();
	}

	@Test
	public void shouldReturnTrueForLocalizedFieldWithEmptyMap()
	{
		//given

		//when
		final boolean valid = testSubject.isValid(TYPE_CODE, LOCALIZED_FILTER_NAME, Collections.emptyMap(),
				ValueComparisonOperator.IS_EMPTY);

		//then
		assertThat(valid).isTrue();
	}

	@Test
	public void shouldReturnTrueForLocalizedFieldWithNull()
	{
		//given

		//when
		final boolean valid = testSubject.isValid(TYPE_CODE, LOCALIZED_FILTER_NAME, null,
				ValueComparisonOperator.IS_EMPTY);

		//then
		assertThat(valid).isTrue();
	}

	private Object provideLocalizedValue()
	{
		return mock(Map.class);
	}
}
