/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.service;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;

import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;


/**
 * Service for interacting with DAO and Client.
 *
 */
public interface SapBillingInvoiceService {

	/**
	 * Gets Service Order By SAP Order Code
	 *
	 * @param sapOrderCode
	 *           Service Order Code of SAPOrder
	 *
	 * @return SAPOrderModel SAP Order model
	 *
	 * @deprecated since 2108, method name is misleading as this method is just retrieving SAP Order by SAP order code. Please use method getSapOrderBySapOrderCode() for same functionality
	 */
	@Deprecated(since = "2108", forRemoval = true)
	SAPOrderModel getServiceOrderBySapOrderCode(String sapOrderCode);
	
	/**
	 * Gets SAP Order from SAP Order Code
	 *
	 * @param sapOrderCode
	 *           Sap Order code for SAP Order
	 *           
	 * @return SAPOrderModel SAP Order model    
	 *
	 */
	SAPOrderModel getSapOrderBySapOrderCode(String sapOrderCode);

	/**
	 * Gets All the business Document from S/4 Cloud for service order data
	 *
	 * @param sapOrder
	 *           SAPOrder (Service Order) created from commerce order
	 *
	 * @return Map object of all the business document from s4    
	 *
	 * @deprecated since 2108, this method will be removed as this method will become unused
	 */
	@Deprecated(since = "2108", forRemoval = true)
	Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(SAPOrderModel sapOrder);

	/**
	 * Gets All the business Document from S/4 Cloud for sap order data
	 *
	 * @param sapOrder
	 *           SAPOrder (Sales Order) created from commerce order
	 *
	 * @return Map object of all the business document from s4   
	 *  
	 * @deprecated since 2108, this method will be removed as this method will become unused
	 */
	@Deprecated(since = "2108", forRemoval = true)
	Map<String, Object> getBusinessDocumentFromS4SSapOrderCode(SAPOrderModel sapOrder);

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

	
	/**
	 * Gets Billing Documents S/4 Cloud for sap order
	 * 
	 * @param sapOrder sap order
	 * @param targetSuffixUrl resource url
	 * @return map of attributes
	 */
	public Map<String, Object> callS4forBillingDocuments(final SAPOrderModel sapOrder, final String targetSuffixUrl);
	
}
