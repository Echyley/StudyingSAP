/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcreditcheck.service;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData; 


/**
 * SapCreditCheckService interface
 */
public interface SapCreditCheckService 
{
	/**
	 *check if credit limit is exceeded
         * @param order AbstractOrderData
         * @return true if the credit limit has been exceeded
	 */
	abstract boolean checkCreditLimitExceeded(AbstractOrderData order);
	
	
	/**
	 * Check if the order is blocked in ERP due to exceeding credit limit
         * @param orderCode String order code
	 * @return true if order is credit blocked
	 */
	abstract boolean checkOrderCreditBlocked(String orderCode);
	
}
