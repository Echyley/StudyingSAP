/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicefacades.facade;

import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;




/**
 * InvoiceFacade interface
 */
public interface InvoiceFacade
{

	/**
	 * Method to generate Pdf
	 * @param billDocumentNumber number of bill in string format
	 * @return byte array of PDF document
	 * @throws UnableToRetrieveInvoiceException Exception
	 */
	byte[] generatePdf(String billDocumentNumber) throws UnableToRetrieveInvoiceException;
}
