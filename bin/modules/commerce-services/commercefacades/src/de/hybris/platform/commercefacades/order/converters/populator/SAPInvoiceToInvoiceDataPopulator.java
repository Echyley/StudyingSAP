/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.model.SAPInvoiceModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;


/**
 * Converter implementation for {@link de.hybris.platform.commerceservices.model.SAPInvoiceModel} as source and
 * {@link de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData} as target type.
 */
public class SAPInvoiceToInvoiceDataPopulator implements Populator<SAPInvoiceModel, SAPInvoiceData>
{
	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(final SAPInvoiceModel source, final SAPInvoiceData target) throws ConversionException
	{
		target.setInvoiceId(source.getId());
		target.setInvoiceDate(source.getCreationtime());
		if (source.getNetPrice() != null)
		{
			target.setNetAmount(createPrice(source, source.getNetPrice()));
		}
		if (source.getTotalPrice() != null)
		{
			target.setTotalAmount(createPrice(source, source.getTotalPrice()));
		}
		if (source.getExternalSystemId() != null)
		{
			target.setExternalSystemId(source.getExternalSystemId().getCode());
		}

	}

	/**
	 * @param source
	 * @param netPrice
	 * @return
	 */

	protected PriceData createPrice(final SAPInvoiceModel source, final BigDecimal val)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source invoice must not be null");
		}

		final CurrencyModel currency = source.getCurrency();
		if (currency == null)
		{
			throw new IllegalArgumentException("source invoice currency must not be null");
		}

		return getPriceDataFactory().create(PriceDataType.BUY, val, currency);
	}


	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}


	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

}
