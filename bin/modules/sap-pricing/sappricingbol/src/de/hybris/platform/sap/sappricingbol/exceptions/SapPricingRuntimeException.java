/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.exceptions;

public class SapPricingRuntimeException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public SapPricingRuntimeException(Exception e)
	{
		super(e);
	}
}
