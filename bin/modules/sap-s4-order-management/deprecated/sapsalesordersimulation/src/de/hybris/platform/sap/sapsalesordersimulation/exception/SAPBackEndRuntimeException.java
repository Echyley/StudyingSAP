/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.exception;

public class SAPBackEndRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public SAPBackEndRuntimeException(Exception e)
	{
		super(e);
	}
}
