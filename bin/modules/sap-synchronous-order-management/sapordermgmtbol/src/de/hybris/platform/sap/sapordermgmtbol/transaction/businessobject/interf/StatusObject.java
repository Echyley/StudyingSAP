/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the Status object. <br>
 * 
 */
public interface StatusObject
{

	/**
	 * Set the shipping status for this object. <br>
	 * 
	 * @param state
	 *           Shipping Status
	 */
	void setShippingStatus(ShippingStatus state);

	/**
	 * Set the billing status for this object. <br>
	 * 
	 * @param state
	 *           Billing Status
	 */
	void setBillingStatus(BillingStatus state);

	/**
	 * Set the overall status for this object. <br>
	 * 
	 * @param state
	 *           Overall Status
	 */
	void setOverallStatus(OverallStatus state);

}
