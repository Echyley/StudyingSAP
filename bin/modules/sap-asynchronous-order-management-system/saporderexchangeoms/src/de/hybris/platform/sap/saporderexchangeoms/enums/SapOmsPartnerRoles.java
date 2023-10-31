/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.enums;

public enum SapOmsPartnerRoles {
	

	
	VENDOR("LF");

	private String code;

	private SapOmsPartnerRoles(final String code)
	{
		this.code = code;
	}

	
	public String getCode()
	{
		return code;
	}

}
