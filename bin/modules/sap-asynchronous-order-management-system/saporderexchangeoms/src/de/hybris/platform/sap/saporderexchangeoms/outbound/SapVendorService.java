/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.outbound;

/**
 * Interface to provide access to Vendor and Item information for OMS configurations
 */
public interface SapVendorService {

	/**
	 * Returns whether or not the Vendor is external given a vendor code
	 * @param vendorCode
	 * 			Code for the vendor
	 * @return Boolean value
	 */
	public boolean isVendorExternal(String vendorCode);

	/**
	 * Returns the String representation of the VendorItemCategory of the desired instance
	 * @return VendorItemCategory String
	 */
	public String getVendorItemCategory();
	
}
