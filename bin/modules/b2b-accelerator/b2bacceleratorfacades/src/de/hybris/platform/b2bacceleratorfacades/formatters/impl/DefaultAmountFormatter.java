/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.formatters.impl;

import de.hybris.platform.b2bacceleratorfacades.formatters.AmountFormatter;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class DefaultAmountFormatter implements AmountFormatter
{
	@Override
	public String formatAmount(final BigDecimal value, final CurrencyModel currency, final Locale locale)
	{
		final NumberFormat currencyFormat = createCurrencyFormat(locale, currency);
		return currencyFormat.format(value);
	}

	protected static NumberFormat createCurrencyFormat(final Locale locale, final CurrencyModel currency)
	{
		final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
		adjustDigits((DecimalFormat) currencyFormat, currency);
		adjustSymbol((DecimalFormat) currencyFormat, currency);
		return currencyFormat;
	}

	/**
	 * Adjusts {@link DecimalFormat}'s fraction digits according to given {@link CurrencyModel}.
	 */
	protected static DecimalFormat adjustDigits(final DecimalFormat format, final CurrencyModel currencyModel)
	{
		final int tempDigits = currencyModel.getDigits() == null ? 0 : currencyModel.getDigits().intValue();
		final int digits = Math.max(0, tempDigits);
		format.setMaximumFractionDigits(digits);
		format.setMinimumFractionDigits(digits);

		if (digits == 0)
		{
			format.setDecimalSeparatorAlwaysShown(false);
		}
		return format;
	}

	/**
	 * Adjusts {@link DecimalFormat}'s symbol according to given {@link CurrencyModel}.
	 */
	protected static DecimalFormat adjustSymbol(final DecimalFormat format, final CurrencyModel currencyModel)
	{
		final String symbol = currencyModel.getSymbol();
		if (symbol != null)
		{
			final DecimalFormatSymbols symbols = format.getDecimalFormatSymbols(); // does cloning
			final String iso = currencyModel.getIsocode();
			boolean changed = false;
			if (!iso.equalsIgnoreCase(symbols.getInternationalCurrencySymbol()))
			{
				symbols.setInternationalCurrencySymbol(iso);
				changed = true;
			}
			if (!symbol.equals(symbols.getCurrencySymbol()))
			{
				symbols.setCurrencySymbol(symbol);
				changed = true;
			}
			if (changed)
			{
				format.setDecimalFormatSymbols(symbols);
			}
		}
		return format;
	}
}
