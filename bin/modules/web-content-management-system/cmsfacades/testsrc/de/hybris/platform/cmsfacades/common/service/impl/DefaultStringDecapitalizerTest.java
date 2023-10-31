/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.service.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@UnitTest
@RunWith(Parameterized.class)
public class DefaultStringDecapitalizerTest
{
	class TESTItemData
	{
	}

	private final DefaultStringDecapitalizer decapitalizer = new DefaultStringDecapitalizer();


	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ "", "" },
				{ "abc", "abc" },
				{ "abC", "abC" },
				{ "Abc", "abc" },
				{ "ABc", "aBc" },
				{ "ABC", "abc" },
				{ "AbC", "abC" },
				{ "AbC", "abC" },
				{ "ABcD", "aBcD" },
				{ "A2BcD", "a2BcD" },
				{ "ABCD2BcD", "abcd2BcD" },
				{ "123ABCdEfGh", "123ABCdEfGh" },
				{ "123ABCdEfGh", "123ABCdEfGh" },
				{ "123ABCdEfGh", "123ABCdEfGh" },
		});
	}

	@Parameter
	public String input;

	@Parameter(1)
	public String expected;

	@Test
	public void testDecapitalizeNullClassShouldReturnOptionalEmpty()
	{
		final Optional<String> result = decapitalizer.decapitalize((Class<?>) null);
		Assert.assertThat(result, Matchers.is(Optional.empty()));
	}

	@Test
	public void testDecapitalizeClassShouldReturnCorrectOptionalString()
	{
		final Optional<String> result = decapitalizer.decapitalize(TESTItemData.class);
		Assert.assertThat(result, Matchers.is(Optional.of("testItemData")));
	}



	@Test
	public void testStringDecapitalizer()
	{
		//when
		final String actual = decapitalizer.decapitalizeString(input);

		//then
		assertThat(actual, equalTo(expected));
	}
}
