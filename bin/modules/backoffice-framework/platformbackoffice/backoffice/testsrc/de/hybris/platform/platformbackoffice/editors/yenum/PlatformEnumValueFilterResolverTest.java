/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.yenum;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.core.HybrisEnumValue;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hybris.cockpitng.editor.defaultenum.EnumValueFilterResolver;


public class PlatformEnumValueFilterResolverTest
{
	private final EnumValueFilterResolver enumValueFilterResolver = new PlatformEnumValueFilterResolver();

	@Test
	public void shouldFilterEnumValues()
	{
		// given
		final List<TestEnum> list = Collections.singletonList(TestEnum.VALUE_ONE);

		// when
		final List<TestEnum> filteredList = enumValueFilterResolver.filterEnumValues(list, "value");

		// then
		assertThat(filteredList).isNotEmpty();
	}

	@Test
	public void shouldHandleUndefinedTypes()
	{
		// given
		final List<UndefinedValue> list = Collections.singletonList(UndefinedValue.create());

		// when
		final List<UndefinedValue> filteredList = enumValueFilterResolver.filterEnumValues(list, "value");

		// then
		assertThat(filteredList).isNotEmpty();
	}

	@Test
	public void shouldNotFilterEnumValuesWhichAreNotPartOfTheQuery()
	{
		// given
		final List<TestEnum> list = Collections.singletonList(TestEnum.VALUE_ONE);

		// when
		final List<TestEnum> filteredList = enumValueFilterResolver.filterEnumValues(list, "notPartOfTheQuery");

		// then
		assertThat(filteredList).isEmpty();
	}

	@Test
	public void shouldFilterHybrisEnumValues()
	{
		// given
		final List<TestHybrisEnumValue> list = Collections.singletonList(TestHybrisEnumValue.create());

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, "test");

		// then
		assertThat(filteredList).isNotEmpty();
	}

	@Test
	public void shouldNotFilterHybrisEnumValuesWhichAreNotPartOfTheQuery()
	{
		// given
		final List<TestHybrisEnumValue> list = Collections.singletonList(TestHybrisEnumValue.create());

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, "unknownValue");

		// then
		assertThat(filteredList).isEmpty();
	}

	@Test
	public void shouldReturnNonEmptyListWhenQueryIsEmpty()
	{
		// given
		final List<TestHybrisEnumValue> list = Collections.singletonList(TestHybrisEnumValue.create());

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, StringUtils.EMPTY);

		// then
		assertThat(filteredList).isNotEmpty();
	}

	@Test
	public void shouldReturnNonEmptyListWhenQueryIsNull()
	{
		// given
		final List<TestHybrisEnumValue> list = Collections.singletonList(TestHybrisEnumValue.create());

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, null);

		// then
		assertThat(filteredList).isNotEmpty();
	}

	@Test
	public void shouldReturnAnEmptyListWhenInputListIsEmpty()
	{
		// given
		final List<TestHybrisEnumValue> list = Collections.emptyList();

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, "test");

		// then
		assertThat(filteredList).isEmpty();
	}

	@Test
	public void shouldReturnAnEmptyListWhenInputListIsNull()
	{
		// given
		final List<TestHybrisEnumValue> list = null;

		// when
		final List<TestHybrisEnumValue> filteredList = enumValueFilterResolver.filterEnumValues(list, "test");

		// then
		assertThat(filteredList).isEmpty();
	}

	private enum TestEnum
	{
		VALUE_ONE
	}

	private static class TestHybrisEnumValue implements HybrisEnumValue
	{
		private TestHybrisEnumValue()
		{
		}

		public static TestHybrisEnumValue create()
		{
			return new TestHybrisEnumValue();
		}

		@Override
		public String getType()
		{
			return "testType";
		}

		@Override
		public String getCode()
		{
			return "testCode";
		}
	}

	private static class UndefinedValue
	{
		static UndefinedValue create()
		{
			return new UndefinedValue();
		}
	}
}
