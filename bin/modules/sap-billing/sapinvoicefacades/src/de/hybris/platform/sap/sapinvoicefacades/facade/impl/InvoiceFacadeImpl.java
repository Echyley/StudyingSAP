/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicefacades.facade.impl;


 
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;
import de.hybris.platform.sap.sapinvoicefacades.facade.InvoiceFacade;
import de.hybris.platform.sap.sapinvoiceservices.services.InvoiceService;
import org.apache.log4j.Logger;

/**
 *
 */
public class InvoiceFacadeImpl implements InvoiceFacade
{


	static final private Logger logger = Logger.getLogger(InvoiceFacadeImpl.class.getName());


	private InvoiceService invoiceService;

	/**
	 * @return the invoiceService
	 */
	public InvoiceService getInvoiceService()
	{
		return invoiceService;
	}

	/**
	 * @param invoiceService
	 *           the invoiceService to set
	 */
	public void setInvoiceService(final InvoiceService invoiceService)
	{
		this.invoiceService = invoiceService;
	}



	@Override
	public byte[] generatePdf(final String billDocumentNumber) throws UnableToRetrieveInvoiceException
	{
		byte[] invoicePdfByteArray = null;
		try
		{
			invoicePdfByteArray = getInvoiceService().getPDFData(billDocumentNumber);
		}
		catch (final BackendException e)
		{
			logger.error("Backend exception occured while trying to fetche invoice data from backend", e);
			throw new UnableToRetrieveInvoiceException("Unable to Retrieve the PDF from backend", e);

		}
		return invoicePdfByteArray;
	}

}
