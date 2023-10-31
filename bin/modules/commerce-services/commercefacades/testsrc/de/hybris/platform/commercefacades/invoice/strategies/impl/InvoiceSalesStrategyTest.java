/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice.strategies.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commerceservices.model.SAPInvoiceMediaModel;
import de.hybris.platform.commerceservices.model.SAPInvoiceModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link InvoiceSalesStrategy}
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceSalesStrategyTest
{
	private static final String INVOICE_ID = "123";

	@Mock
	private Converter<SAPInvoiceModel, SAPInvoiceData> sapInvoiceToSapInvoiceDataConverter;

	@InjectMocks
	private InvoiceSalesStrategy invoiceSalesStrategy;

	@Mock
	private MediaService mediaService;

	private OrderModel orderModel;

	@Test
	public void testGetInvoices()
	{
		orderModel = new OrderModel();
		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		final SAPInvoiceModel sapInvoiceModel = new SAPInvoiceModel();
		sapInvoiceModel.setId(INVOICE_ID);

		consignmentEntryModel.setSapInvoice(sapInvoiceModel);
		orderEntryModel.setConsignmentEntries(Collections.singleton(consignmentEntryModel));
		orderModel.setEntries(Collections.singletonList(orderEntryModel));

		final Set<SAPInvoiceModel> expectedInvoices = new HashSet<>();
		expectedInvoices.add(sapInvoiceModel);

		lenient().when(sapInvoiceToSapInvoiceDataConverter.convertAll(Collections.singletonList(sapInvoiceModel)))
				.thenReturn(Collections.singletonList(new SAPInvoiceData()));

		final List<SAPInvoiceData> invoices = invoiceSalesStrategy.getInvoices(orderModel);

		verify(sapInvoiceToSapInvoiceDataConverter).convertAll(expectedInvoices.stream().toList());
		assertEquals(1, invoices.size());
	}

	@Test(expected = NullPointerException.class)
	public void testGetInvoiceBinaryWithNullOrderModel()
	{
		invoiceSalesStrategy.getInvoiceBinary(null, INVOICE_ID);
	}

	@Test
	public void testGetInvoiceBinaryWithNoSAPInvoice()
	{
		orderModel = mock(OrderModel.class);
		final AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel entry2 = mock(AbstractOrderEntryModel.class);
		when(orderModel.getEntries()).thenReturn(Arrays.asList(entry1, entry2));

		final byte[] result = invoiceSalesStrategy.getInvoiceBinary(orderModel, INVOICE_ID);

		verify(orderModel).getEntries();
		assertEquals(0, result.length);
	}

	@Test
	public void testGetInvoiceBinaryWithMatchingSAPInvoice()
	{
		orderModel = mock(OrderModel.class);
		final AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
		final ConsignmentEntryModel consignmentEntry1 = mock(ConsignmentEntryModel.class);
		final SAPInvoiceModel sapInvoice1 = mock(SAPInvoiceModel.class);
		final SAPInvoiceMediaModel invoiceMedia1 = mock(SAPInvoiceMediaModel.class);
		when(orderModel.getEntries()).thenReturn(Arrays.asList(entry1));
		when(sapInvoice1.getId()).thenReturn(INVOICE_ID);
		when(sapInvoice1.getInvoiceMedia()).thenReturn(invoiceMedia1);
		when(consignmentEntry1.getSapInvoice()).thenReturn(sapInvoice1);
		when(entry1.getConsignmentEntries()).thenReturn(Collections.singleton(consignmentEntry1));

		final byte[] expected = new byte[]
		{ 1, 2, 3 };
		when(mediaService.getDataFromMedia(invoiceMedia1)).thenReturn(expected);

		final byte[] result = invoiceSalesStrategy.getInvoiceBinary(orderModel, INVOICE_ID);

		verify(orderModel).getEntries();
		assertArrayEquals(expected, result);
	}

	@Test
	public void testGetInvoiceBinaryWithNoMedia()
	{
		orderModel = mock(OrderModel.class);
		final AbstractOrderEntryModel entry1 = mock(AbstractOrderEntryModel.class);
		final ConsignmentEntryModel consignmentEntry1 = mock(ConsignmentEntryModel.class);
		final SAPInvoiceModel sapInvoice1 = mock(SAPInvoiceModel.class);
		when(orderModel.getEntries()).thenReturn(Arrays.asList(entry1));
		when(sapInvoice1.getId()).thenReturn(INVOICE_ID);
		when(sapInvoice1.getInvoiceMedia()).thenReturn(null);
		when(consignmentEntry1.getSapInvoice()).thenReturn(sapInvoice1);
		when(entry1.getConsignmentEntries()).thenReturn(Collections.singleton(consignmentEntry1));

		final byte[] result = invoiceSalesStrategy.getInvoiceBinary(orderModel, INVOICE_ID);

		verify(orderModel).getEntries();
		assertEquals(0, result.length);
	}
}

