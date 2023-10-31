/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.billlinginvoice.service;

import java.util.Map;

import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

/**
 *  Service layer interface to retrieve service order billing documents and invoices
 */
public interface SapServiceOrderBillingInvoiceService {

	/**
	 * Gets Billing Documents S/4 Cloud for sap order
	 * 
	 * @param sapOrder sap order
	 * @param targetSuffixUrl resource url
	 * @return map of attributes
	 */
	public Map<String, Object> callS4forBillingDocuments(final SAPOrderModel sapOrder, final String targetSuffixUrl);

	
	/**
	 * Gets PDF Data from S/4 Cloud for sap order data
	 *
	 * @param sapOrder
	 *           SAPOrder created from commerce order
	 *
	 * @param billingDocumentId
	 *           Billing Document ID of external system
	 * 
	 * @throws SapBillingInvoiceUserException when pdf is accessed by different USER
	 * 
	 * @return PDF Byte array 
	 */
	public byte[] getPDFData(final SAPOrderModel sapOrder, final String billingDocumentId)
			throws SapBillingInvoiceUserException;
}
