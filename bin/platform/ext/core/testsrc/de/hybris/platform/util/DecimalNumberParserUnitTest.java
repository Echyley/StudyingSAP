/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.util;

import static org.assertj.core.api.Assertions.*;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class DecimalNumberParserUnitTest
{
	private DecimalNumberParser parser;

	@Before
	public void setUp() throws Exception
	{
		parser = new DecimalNumberParser();
	}

	@Test
	public void shouldStripDecimalPartFromStrippableNumbers()
	{
		final String[][] pairsOfBeforeAndAfterDecimalStrip = {
				{ "123.456", "123" },
				{ "-10.001", "-10" },
				{ "99999.0", "99999" },
				{ "1234567", "1234567" },
				{ "0.01230", "0" },
				{ "-0.1", "0" },
				{ "0", "0" }
		};
		for (final var pair : pairsOfBeforeAndAfterDecimalStrip)
		{
			final String numberWithoutDecimalPart = parser.stripDecimalPart(pair[0]);
			assertThat(numberWithoutDecimalPart).isEqualTo(pair[1]);
		}
	}

	@Test
	public void shouldMatchNumbersWithZeroesAsDecimalPart()
	{
		final Stream<String> numbersWithZeroesAsDecimalPart = Stream.of("123.0", "-123.0000000000", "0.00");
		numbersWithZeroesAsDecimalPart.forEach(v -> assertThat(parser.hasOnlyZeroesAsDecimalPart(v)).isTrue());
	}

	@Test
	public void shouldNotMatchNumbersWithoutZeroesAsDecimalPart()
	{
		final Stream<String> numbersWithoutZeroesAsDecimalPart = Stream.of("123", "-123", "0", "0.01", "123.1000", "123.0001");
		numbersWithoutZeroesAsDecimalPart.forEach(v -> assertThat(parser.hasOnlyZeroesAsDecimalPart(v)).isFalse());
	}

	@Test
	public void shouldMatchValidNumbers()
	{
		final Stream<String> validNumbers = Stream.of("0", "-0", "-999", "123.0", "-123.456", "0.00");
		validNumbers.forEach(num -> assertThat(parser.isValidNumber(num)).isTrue());
	}

	@Test
	public void shouldNotMatchInvalidNumbers()
	{
		final Stream<String> invalidNumbers = Stream.of("0123", "10.", ".", "12 3", "12a3", ".5");
		invalidNumbers.forEach(num -> assertThat(parser.isValidNumber(num)).isFalse());
	}
}
