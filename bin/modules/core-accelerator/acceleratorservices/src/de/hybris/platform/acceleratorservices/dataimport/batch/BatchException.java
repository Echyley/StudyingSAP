/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Batch Exception encapsulating the BatchHeader information.
 */
public class BatchException extends SystemException 
{
	private final BatchHeader header; 

	/**
	 * @param message
	 */
	public BatchException(final String message)
	{
		super(message);
		this.header = null;
	}

	/**
	 * @param message
	 * @param header
	 */
	public BatchException(final String message, final BatchHeader header)
	{
		super(message);
		this.header = header;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BatchException(final String message, final Throwable cause)
	{
		super(message, cause);
		this.header = null;
	}

	/**
	 * @param message
	 * @param header
	 * @param cause
	 */
	public BatchException(final String message, final BatchHeader header, final Throwable cause)
	{
		super(message, cause);
		this.header = header;
	}

	/**
	 * @param cause
	 */
	public BatchException(final Throwable cause)
	{
		super(cause);
		this.header = null;
	}

	/**
	 * @return the header
	 */
	public BatchHeader getHeader()
	{
		return header;
	}
}
