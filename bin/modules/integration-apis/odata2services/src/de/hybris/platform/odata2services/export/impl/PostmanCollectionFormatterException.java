/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

/**
 * An exception that could be thrown while formatting a Postman collection.
 */
public class PostmanCollectionFormatterException extends RuntimeException
{
	private static final String ERROR_MESSAGE = "An error occurs while formatting a Postman collection. %s";

	/**
	 * Instantiates a Postman collection formatter exception.
	 *
	 * @param cause the root cause wrapped exception
	 */
	public PostmanCollectionFormatterException(final Throwable cause)
	{
		super(String.format(ERROR_MESSAGE, cause.getMessage()), cause);
	}

}
