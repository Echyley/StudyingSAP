/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.enums.ExternalSystemId;
import de.hybris.platform.commerceservices.model.SAPInvoiceModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SAPInvoiceToInvoiceDataPopulatorTest
{
	private static final BigDecimal PRICE = new BigDecimal(10);
	private static final String INVOICE_ID = "123";
	private static final String CURRENCY = "USD";

	@Mock
	private PriceDataFactory priceDataFactory;

	@Mock
	private Date createDate;

	@InjectMocks
	private SAPInvoiceToInvoiceDataPopulator populator;

	@Test
	public void testPopulate()
	{
		final SAPInvoiceModel source = new SAPInvoiceModel();
		source.setId(INVOICE_ID);
		source.setCreationtime(createDate);
		source.setNetPrice(PRICE);
		source.setTotalPrice(PRICE);
		source.setExternalSystemId(ExternalSystemId.S4SALES);
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode(CURRENCY);
		source.setCurrency(currency);

		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso(INVOICE_ID);
		priceData.setValue(PRICE);

		final SAPInvoiceData target = new SAPInvoiceData();
		when(priceDataFactory.create(PriceDataType.BUY, PRICE, currency)).thenReturn(priceData);

		populator.populate(source, target);

		assertEquals(INVOICE_ID, target.getInvoiceId());
		assertNotNull(target.getInvoiceDate());
		assertEquals(PRICE, target.getNetAmount().getValue());
		assertEquals(PRICE, target.getTotalAmount().getValue());
		assertEquals(ExternalSystemId.S4SALES.getCode(), target.getExternalSystemId());
		assertSame(priceData, target.getNetAmount());
		assertSame(priceData, target.getTotalAmount());
	}

	@Test
	public void testPopulateNullPriceExtSystem()
	{
		final SAPInvoiceModel source = new SAPInvoiceModel();
		source.setId(INVOICE_ID);
		source.setCreationtime(createDate);
		final SAPInvoiceData target = new SAPInvoiceData();

		populator.populate(source, target);

		assertEquals(INVOICE_ID, target.getInvoiceId());
		assertNotNull(target.getInvoiceDate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceWithNullSource()
	{
		populator.createPrice(null, PRICE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceWithNullCurrency()
	{
		final SAPInvoiceModel source = new SAPInvoiceModel();
		populator.createPrice(source, PRICE);

	}
}

