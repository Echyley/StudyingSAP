/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceservices.services;

import de.hybris.platform.sap.sapinvoicebol.businessobject.SapInvoiceBO;


/**
 *
 */
public interface SapInvoiceBOFactory
{
	/**
	 * @return SapInvoiceBO
	 */
	public SapInvoiceBO getSapInvoiceBO();
}
