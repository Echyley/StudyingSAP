/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.exceptions;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import javax.servlet.ServletException;


public class UnsupportedCurrencyException extends ServletException
{

	private final CurrencyModel currency;

	/**
	 * @param msg
	 */
	public UnsupportedCurrencyException(final String msg)
	{
		super(msg);
		currency = null;
	}

	public UnsupportedCurrencyException(final String msg, final Throwable rootCouse)
	{
		super(msg, rootCouse);
		currency = null;
	}

	/**
	 * @return the currency
	 */
	public CurrencyModel getCurrency()
	{
		return currency;
	}
}
