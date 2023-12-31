/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services;

/**
 *	Accessing sales area data for SAP back end communication
 */
public interface SalesAreaService {
	
	/**
	 * @return Current sales organization. 
	 * No bundling available, it is the same one for order creation or master data maintenance.
	 */
	public String getSalesOrganization();
	
	/**
	 * @return Current distribution channel for order creation. The channel maintained in SAP Configuration
	 */
	public String getDistributionChannel();
	
	/**
	 * @return Current channel for condition maintenance
	 */
	public String getDistributionChannelForConditions();
	
	/**
	 * @return Current channel for customer or material master access.
	 */
	public String getDistributionChannelForCustomerMaterial();
	
	/**
	 * @return Current division for order creation. The division maintained in SAP Configuration
	 */
	public String getDivision();
	
	/**
	 * @return Current division for condition maintenance
	 */
	public String getDivisionForConditions();
	
	/**
	 * @return Current division for customer or material master access
	 */
	public String getDivisionForCustomerMaterial();

}
