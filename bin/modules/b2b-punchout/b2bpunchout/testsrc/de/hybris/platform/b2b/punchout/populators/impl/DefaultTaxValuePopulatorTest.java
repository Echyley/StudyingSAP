/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.cxml.Money;
import org.cxml.Tax;
import org.cxml.TaxAmount;
import org.cxml.TaxDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTaxValuePopulatorTest
{
	private final Collection<TaxValue> taxValues = new ArrayList<>();
	private static final String PRICE_VALUE = "10.00";
	private static final String CATEGORY = "DE";
	private static final String CURRENCY_CODE = "EUR";

	@Mock
	private Tax taxSource;
	@Mock
	private TaxDetail taxDetail;
	@Mock
	private TaxAmount taxAmount;

	@InjectMocks
	private DefaultTaxValuePopulator taxValuePopulator;

	@Before
	public void setUp()
	{
		final Money money = new Money();
		money.setvalue(PRICE_VALUE);
		money.setCurrency(CURRENCY_CODE);

		when(taxSource.getTaxDetail()).thenReturn(Collections.singletonList(taxDetail));
		when(taxDetail.getTaxAmount()).thenReturn(taxAmount);
		when(taxAmount.getMoney()).thenReturn(money);
		when(taxDetail.getCategory()).thenReturn(CATEGORY);
	}

	@Test
	public void normalPopulation()
	{
		taxValuePopulator.populate(taxSource, taxValues);

		assertThat(taxValues).isNotEmpty()
							 .element(0)
							 .isNotNull()
							 .hasFieldOrPropertyWithValue("code", CATEGORY)
							 .hasFieldOrPropertyWithValue("currencyIsoCode", CURRENCY_CODE)
							 .hasFieldOrPropertyWithValue("absolute", true)
							 .hasFieldOrPropertyWithValue("value", Double.parseDouble(taxAmount.getMoney().getvalue()));
	}

	@Test
	public void nullPopulation()
	{
		taxValuePopulator.populate(null, taxValues);
		assertThat(taxValues).isEmpty();
	}
}
