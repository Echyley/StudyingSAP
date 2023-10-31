/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.dao;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

/**
 * Data Access Object for looking up items related to SAP Order
 *
 */
public interface SapBillingInvoiceDao {

	/**
	 * Gets Service Order By SAP Order Code
	 *
	 * @param serviceOrderCode
	 *           Service Order Code of SAPOrder
	 *
	 *    
	 * @deprecated since 2108, method name is misleading as this method is just retrieving SAP Order by SAP order code. Please use method getSapOrderBySapOrderCode() for same functionality
	 */
	@Deprecated(since = "2108", forRemoval = true)
	SAPOrderModel getServiceOrderBySapOrderCode(String serviceOrderCode);
	
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

}
