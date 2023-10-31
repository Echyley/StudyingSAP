/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicebol.backend;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 *
 */
public interface SapInvoiceBackend extends BackendBusinessObject
{
	/**
	 * get the invoice in byte format
	 *
	 * @param billingDocNumber
	 * @return byte array
	 * @throws BackendException
	 */
	public byte[] getInvoiceInByte(final String billingDocNumber) throws BackendException;
}
