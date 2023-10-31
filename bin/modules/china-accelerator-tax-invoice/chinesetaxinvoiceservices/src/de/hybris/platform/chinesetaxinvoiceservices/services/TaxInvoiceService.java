/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesetaxinvoiceservices.services;

import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;


/**
 * Tax invoice facade interface. Its main purpose is to retrieve chinese tax invoice related DTOs using existing
 * services.
 */
public interface TaxInvoiceService
{

	/**
	 * Query TaxInvoiceModel for PK.
	 *
	 * @param code
	 *           PK of tax invoice
	 * @return tax invoice model
	 */
	TaxInvoiceModel getTaxInvoiceForCode(String code);

	/**
	 * To create a TaxInvoiceModel use a TaxInvoiceData
	 *
	 * @param taxInvoiceId
	 *           the identifier of this tax invoice.
	 * @return tax invoice model
	 */
	TaxInvoiceModel createTaxInvoice(String taxInvoiceId);
}
