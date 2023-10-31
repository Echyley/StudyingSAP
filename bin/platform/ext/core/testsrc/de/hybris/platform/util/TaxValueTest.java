/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class TaxValueTest
{
	private static final String CODE = "Code";
	private static final String ISO_CODE = "USD";
	private static final String ISO_CODE2 = "EUR";
	private static final double VALUE1 = 24.94;
	private static final double VALUE2 = 27.32;

	@Test
	public void shouldReturnTrueForEqualTaxValues()
	{
		//given
		final TaxValue taxValue1 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE);
		final TaxValue taxValue2 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE);

		//when
		final boolean areEqual = taxValue1.equals(taxValue2);

		//then
		assertThat(areEqual).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenIsoCodeIsNotEqual()
	{
		//given
		final TaxValue taxValue1 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE);
		final TaxValue taxValue2 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE2);

		//when
		final boolean areEqual = taxValue1.equals(taxValue2);

		//then
		assertThat(areEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenValueIsNotEqual()
	{
		//given
		final TaxValue taxValue1 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE);
		final TaxValue taxValue2 = new TaxValue(CODE, VALUE2, true, VALUE2, ISO_CODE);

		//when
		final boolean areEqual = taxValue1.equals(taxValue2);

		//then
		assertThat(areEqual).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenAppliedValueIsNotEqual()
	{
		//given
		final TaxValue taxValue1 = new TaxValue(CODE, VALUE1, true, VALUE2, ISO_CODE);
		final TaxValue taxValue2 = new TaxValue(CODE, VALUE1, true, VALUE1, ISO_CODE);

		//when
		final boolean areEqual = taxValue1.equals(taxValue2);

		//then
		assertThat(areEqual).isFalse();
	}
}
