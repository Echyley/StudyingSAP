/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.test;

/**
 * Exception class for RFCDirectoryDestinationDataProvider.
 */
@SuppressWarnings("serial")
public class RFCDirectoryDestinationDataProviderException extends RuntimeException
{

	/**
	 * Constructs a new RFCDirectoryDestinationDataProvider exception with the specified detail message.
	 * 
	 * @param message
	 *           message
	 */
	public RFCDirectoryDestinationDataProviderException(final String message)
	{
		super(message);
	}

	/**
	 * Constructs a new RFCDirectoryDestinationDataProvider exception with the specified detail message and cause.
	 * 
	 * @param message
	 *           the detail message (which is saved for later retrieval by the Throwable.getMessage() method).
	 * @param cause
	 *           the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is
	 *           permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public RFCDirectoryDestinationDataProviderException(final String message, final Throwable cause)
	{
		super(message, cause);
	}





}
