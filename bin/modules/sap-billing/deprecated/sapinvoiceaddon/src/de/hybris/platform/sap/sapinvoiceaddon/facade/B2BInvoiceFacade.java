/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.facade;

import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;



/**
 * B2BInvoiceFacade interface
 */
public interface B2BInvoiceFacade
{

	/**
	 * Gets order for invoiceDocumentNumber
	 * @param invoiceDocumentNumber invoice Document Number
	 * @return byte array of PDF document
	 * @throws SapInvoiceException exception
	 */
	public abstract SapB2BDocumentModel getOrderForCode(String invoiceDocumentNumber) throws SapInvoiceException;

	/**
	 * Convert Invoice Data
     * @param invoice SapB2BDocumentModel
     * @return B2BDocumentData for given invoice
     * @throws SapInvoiceException exception
     */
	public abstract B2BDocumentData convertInvoiceData(SapB2BDocumentModel invoice) throws SapInvoiceException;


}
