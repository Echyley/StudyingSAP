/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.populator;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commerceservices.enums.ExternalSystemId;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class ExternalBillingToInvoiceDataPopulator implements Populator<ExternalSystemBillingDocumentData, SAPInvoiceData>
{
	@Override
	public void populate(final ExternalSystemBillingDocumentData source, final SAPInvoiceData target) throws ConversionException
	{
		target.setInvoiceId(source.getBillingDocumentId());
		target.setInvoiceDate(source.getBillingInvoiceDate());
		target.setExternalSystemId(ExternalSystemId.S4SALES.getCode());
		target.setTotalAmount(source.getTotalPrice());
		target.setNetAmount(source.getNetAmount());

	}

}
