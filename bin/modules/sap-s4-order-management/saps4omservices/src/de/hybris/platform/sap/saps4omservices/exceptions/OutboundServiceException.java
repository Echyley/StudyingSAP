/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.exceptions;

public class OutboundServiceException extends RuntimeException{

	/**
	 *  Custom Exception
	 */
	private static final long serialVersionUID = 1L;

	public OutboundServiceException(final String message)
	{
		super(message);
	}
	
	public OutboundServiceException(final String message, final Throwable t)
	{
		super(message, t);
	}
	
}
