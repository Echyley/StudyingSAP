/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;

/**
 * 
 * Partner roles for sales order processing as defined in SAP ERP. Corresponds to SAP defined entries of table TPAR
 * 
 */
public enum PartnerRoles
{

	
	SOLD_TO("AG"), 
	CONTACT("AP"), 
	SHIP_TO("WE"), 
	BILL_TO("RE"),  
	PLACED_BY("VE");

	private String code;

	private PartnerRoles(final String code)
	{
		this.code = code;
	}

	
	public String getCode()
	{
		return code;
	}
}
