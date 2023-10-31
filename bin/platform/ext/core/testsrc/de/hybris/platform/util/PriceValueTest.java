/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class PriceValueTest
{
	private static final String CURRENCY_ISO = "USD";
	private static final double PRICE_1 = 24.94;
	private static final double PRICE_2 = 27.32;

	@Test
	public void shouldReturnTrueForEqualPriceValues()
	{
		//given
		final PriceValue priceValue1 = new PriceValue(CURRENCY_ISO, PRICE_1, true);
		final PriceValue priceValue2 = new PriceValue(CURRENCY_ISO, PRICE_1, true);

		//when
		final boolean areEqual = priceValue1.equals(priceValue2);

		//then
		assertThat(areEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseForNotEqualPriceValues()
	{
		//given
		final PriceValue priceValue1 = new PriceValue(CURRENCY_ISO, PRICE_1, true);
		final PriceValue priceValue2 = new PriceValue(CURRENCY_ISO, PRICE_2, true);

		//when
		final boolean areEqual = priceValue1.equals(priceValue2);

		//then
		assertThat(areEqual).isFalse();
	}
}
