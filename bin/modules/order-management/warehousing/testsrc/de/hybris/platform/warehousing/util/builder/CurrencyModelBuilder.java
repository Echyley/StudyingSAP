/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Locale;


public class CurrencyModelBuilder
{
	private final CurrencyModel model;

	private CurrencyModelBuilder()
	{
		model = new CurrencyModel();
	}

	private CurrencyModel getModel()
	{
		return this.model;
	}

	public static CurrencyModelBuilder aModel()
	{
		return new CurrencyModelBuilder();
	}

	public CurrencyModel build()
	{
		return getModel();
	}

	public CurrencyModelBuilder withIsoCode(final String isoCode)
	{
		getModel().setIsocode(isoCode);
		return this;
	}

	public CurrencyModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public CurrencyModelBuilder withActive(final Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public CurrencyModelBuilder withConversion(final Double conversion)
	{
		getModel().setConversion(conversion);
		return this;
	}

	public CurrencyModelBuilder withDigits(final Integer digits)
	{
		getModel().setDigits(digits);
		return this;
	}

	public CurrencyModelBuilder withSymbol(final String symbol)
	{
		getModel().setSymbol(symbol);
		return this;
	}
}
