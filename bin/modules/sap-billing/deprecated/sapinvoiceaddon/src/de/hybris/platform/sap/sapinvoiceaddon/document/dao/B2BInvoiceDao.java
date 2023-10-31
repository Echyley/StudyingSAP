/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.document.dao;

import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;


/**
 *
 */
public interface B2BInvoiceDao
{

	/**
	 * Retrieves SapB2BDocumentModel for given invoiceDocumentNumber.
	 *
	 * @param invoiceDocumentNumber
	 *           the invoice number
	 * @return SapB2BDocumentModel object if found, null otherwise
	 */
	public abstract SapB2BDocumentModel findInvoiceByDocumentNumber(String invoiceDocumentNumber);
}