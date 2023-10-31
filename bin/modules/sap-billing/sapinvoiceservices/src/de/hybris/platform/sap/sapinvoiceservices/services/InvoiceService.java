/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceservices.services;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * InvoiceService interface
 */
public interface InvoiceService
{
        /**
         * Method to get PDF data
         * @param billDocumentNumber DocumentNumber of bill in string format
         * @return byte array of PDF document
         * @throws BackendException Backend Exception
         */
	public byte[] getPDFData(final String billDocumentNumber) throws BackendException;
}
