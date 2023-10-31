/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.impl;

/**
 * An exception that may occur while parsing the body of a batch request or response.
 */
public class ODataBatchParsingException extends RuntimeException
{
	private static final String MESSAGE = "Error during parsing of a batch response or request.";

	/**
	 *  Constructor to create {@link ODataBatchParsingException}
	 */
	public ODataBatchParsingException()
	{
		super(MESSAGE);
	}
	/**
	 *  Constructor to create {@link ODataBatchParsingException}
	 */
	public ODataBatchParsingException(final Throwable cause)
	{
		super(MESSAGE, cause);
	}
}
