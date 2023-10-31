/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice.strategies;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;


/**
 * An interface representing a strategy for getting invoices.
 */
public interface InvoiceStrategy
{
	/**
	 * @param orderModel
	 * @return a {@link List} of {@link SAPInvoiceData} containing list of invoice of an order
	 */
	List<SAPInvoiceData> getInvoices(final OrderModel orderModel);

	/**
	 * @param orderModel
	 * @param invoiceId
	 * @return byteArray
	 */
	byte[] getInvoiceBinary(final OrderModel orderModel, final String invoiceId);

}
