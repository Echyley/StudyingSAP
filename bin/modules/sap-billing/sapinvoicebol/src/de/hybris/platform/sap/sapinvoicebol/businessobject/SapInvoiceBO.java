/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicebol.businessobject;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;




/**
 *
 */
public interface SapInvoiceBO
{
	/**
	 * get byte for PDF for billingDOCNumber
	 *
	 * @param billingDocNumber
	 * @return byte of PDF for billingDOCNumber
	 */
	abstract byte[] getPDF(final String billingDocNumber) throws BackendException;
}
