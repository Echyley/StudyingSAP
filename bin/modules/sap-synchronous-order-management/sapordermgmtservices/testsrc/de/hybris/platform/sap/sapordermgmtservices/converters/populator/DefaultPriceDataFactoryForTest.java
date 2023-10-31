/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;


/**
 *
 */
@SuppressWarnings("squid:S2187")
public class DefaultPriceDataFactoryForTest implements PriceDataFactory
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.product.PriceDataFactory#create(de.hybris.platform.commercefacades.product.
	 * data.PriceDataType, java.math.BigDecimal, java.lang.String)
	 */
	@Override
	public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso)
	{
		return formatPrice(value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.product.PriceDataFactory#create(de.hybris.platform.commercefacades.product.
	 * data.PriceDataType, java.math.BigDecimal, de.hybris.platform.core.model.c2l.CurrencyModel)
	 */
	@Override
	public PriceData create(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
	{
		return formatPrice(value);
	}

	private PriceData formatPrice(final BigDecimal value)
	{
		final PriceData newPrice = new PriceData();
		newPrice.setValue(value);
		return newPrice;
	}
}
