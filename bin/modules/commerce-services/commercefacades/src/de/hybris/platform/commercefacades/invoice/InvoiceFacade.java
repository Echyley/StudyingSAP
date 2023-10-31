/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;


/**
 * Invoice facade interface. An Invoice Facade should provide access to a user's order invoice.
 */
public interface InvoiceFacade
{
	/**
	 * The invoice list of the order with matching code
	 *
	 * @param orderCode
	 *           the order code
	 * @param searchPageDataInput
	 *           paging and sorting information
	 * @param addPaginationField
	 *           field level
	 * @return The invoice list of the order with matching code
	 */
	SearchPageData<SAPInvoiceData> getInvoices(String orderCode, SearchPageData<SAPInvoiceData> searchPageDataInput,
			String addPaginationField);


	/**
	 * The invoice byte array of an invoice
	 *
	 * @param orderCode
	 *           the order code
	 * @param invoiceId
	 *           invoiceId
	 * @param externalSystemId
	 *           externalSystemId represents the external system where the invoice can be retrieved
	 * @return The invoice byte array of an invoice
	 */
	byte[] getInvoiceBinary(final String orderCode, final String invoiceId, final String externalSystemId);

}
