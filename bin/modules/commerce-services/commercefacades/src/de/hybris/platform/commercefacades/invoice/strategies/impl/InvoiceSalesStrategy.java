/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice.strategies.impl;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.invoice.strategies.InvoiceStrategy;
import de.hybris.platform.commerceservices.model.SAPInvoiceMediaModel;
import de.hybris.platform.commerceservices.model.SAPInvoiceModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Default implementation of {@link InvoiceStrategy}
 */
public class InvoiceSalesStrategy implements InvoiceStrategy
{
	private Converter<SAPInvoiceModel, SAPInvoiceData> sapInvoiceToSapInvoiceDataConverter;
	private MediaService mediaService;

	@Override
	public List<SAPInvoiceData> getInvoices(final OrderModel orderModel)
	{
		Objects.requireNonNull(orderModel, "orderModel is null");
		final Set<SAPInvoiceModel> invoices = new HashSet<>();

		for (final AbstractOrderEntryModel orderEntry : orderModel.getEntries())
		{
			for (final ConsignmentEntryModel consignmentEntry : orderEntry.getConsignmentEntries())
			{
				if (consignmentEntry.getSapInvoice() != null)
				{
					invoices.add(consignmentEntry.getSapInvoice());
				}

			}
		}

		return sapInvoiceToSapInvoiceDataConverter.convertAll(invoices.stream().toList());
	}

	@Override
	public byte[] getInvoiceBinary(final OrderModel orderModel, final String invoiceId)
	{
		Objects.requireNonNull(orderModel, "orderModel is null");
		for (final AbstractOrderEntryModel orderEntry : orderModel.getEntries())
		{
			for (final ConsignmentEntryModel consignmentEntry : orderEntry.getConsignmentEntries())
			{
				final SAPInvoiceModel sapInvoice = consignmentEntry.getSapInvoice();
				if (sapInvoice != null && sapInvoice.getId().equals(invoiceId))
				{

					return getInvoiceByteArray(sapInvoice.getInvoiceMedia());
				}

			}
		}
		return new byte[0];
	}

	/**
	 * @param invoiceMedia
	 * @return byte array
	 */
	private byte[] getInvoiceByteArray(final SAPInvoiceMediaModel invoiceMedia)
	{
		if (invoiceMedia != null)
		{
			return getMediaService().getDataFromMedia(invoiceMedia);
		}
		return new byte[0];
	}

	public Converter<SAPInvoiceModel, SAPInvoiceData> getSapInvoiceToSapInvoiceDataConverter()
	{
		return sapInvoiceToSapInvoiceDataConverter;
	}

	public void setSapInvoiceToSapInvoiceDataConverter(
			final Converter<SAPInvoiceModel, SAPInvoiceData> sapInvoiceToSapInvoiceDataConverter)
	{
		this.sapInvoiceToSapInvoiceDataConverter = sapInvoiceToSapInvoiceDataConverter;
	}

	public MediaService getMediaService()
	{
		return mediaService;
	}

	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

}
