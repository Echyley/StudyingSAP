/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout;

import org.springframework.http.HttpStatus;
/**
 *
 */
public class PunchOutSessionNotFoundException extends PunchOutException
{

	/**
	 * Constructor.
	 *
	 * @param message
	 *           the error message
	 */
	public PunchOutSessionNotFoundException(final String message)
	{
		super(HttpStatus.NOT_FOUND, message);
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *           the error message
	 * @param cause
	 *           the cause
	 */
	public PunchOutSessionNotFoundException(final String message, final Throwable cause)
	{
		super(HttpStatus.NOT_FOUND, message, cause);
	}

}
