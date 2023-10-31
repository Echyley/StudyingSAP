/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceservices.services.impl;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoiceservices.services.InvoiceService;
import de.hybris.platform.sap.sapinvoiceservices.services.SapInvoiceBOFactory;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class InvoiceServiceImpl implements InvoiceService
{

	private SapInvoiceBOFactory sapInvoiceBOFactory;

	@Override
	public byte[] getPDFData(final String billDocumentNumber) throws BackendException
	{
		return getSapInvoiceBOFactory().getSapInvoiceBO().getPDF(billDocumentNumber);
	}

	/**
	 * @return the sapInvoiceBOFactory
	 */
	public SapInvoiceBOFactory getSapInvoiceBOFactory()
	{
		return sapInvoiceBOFactory;
	}

	/**
	 * @param sapInvoiceBOFactory
	 *           the sapInvoiceBOFactory to set
	 */
	@Required
	public void setSapInvoiceBOFactory(final SapInvoiceBOFactory sapInvoiceBOFactory)
	{
		this.sapInvoiceBOFactory = sapInvoiceBOFactory;
	}

}
