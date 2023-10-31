/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.facade;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;
import java.util.List;

import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;


/**
 * Facade for interacting with the SapBillingInvoiceService.
 */
public interface SapBillingInvoiceFacade {


	/**
	 * Gets Service Order from SAP Order Code
	 *
	 * @param sapOrderCode
	 *           Sap Order code from SAP Order Data
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
	 * Gets Business Document from S/4 Cloud for service order code
	 *
	 * @param serviceOrderData
	 *           SAPOrder for commerce order
	 *           
	 * @return Map object of all the business document from s4   
	 *    
	 * @deprecated since 2108, Unused method
	 */
	@Deprecated(since = "2108", forRemoval = true)
	Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(SAPOrderModel serviceOrderData);

	/**
	 * Gets PDF Data
	 *
	 * @param sapOrderCode
	 *           Sap Order code of SAPOrder
	 *
	 * @param billingDocId
	 *           Billing Document ID of target System
	 *           
	 * @throws SapBillingInvoiceUserException when pdf is accessed by different USER
	 * 
	 * @return PDF Byte array 
	 *
	 */
	public byte[] getPDFData(final String sapOrderCode, final String billingDocId)
			throws SapBillingInvoiceUserException;
	
	/**
     * Gets Billing Documents Data for a order
     *
     * @param orderCode
     *           order code
     *           
     * @return ExternalSystemBillingDocumentData      
     *
     */
    List<ExternalSystemBillingDocumentData> getBillingDocumentsForOrder(String orderCode);


}


