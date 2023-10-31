/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
/**
 * Business exception representing PunchOut issues
 */
public class PunchOutException extends RuntimeException
{
	private static final Logger LOG = LoggerFactory.getLogger(PunchOutException.class);
	public static final String PUNCHOUT_EXCEPTION_MESSAGE = "PunchOut Exception";

	private final String errorCode;

	public PunchOutException(final String errorCode, final String message)
	{
		this(errorCode, message, null);
	}

	public PunchOutException(HttpStatus httpStatus, final String message)
	{
		this(httpStatus, message, null);
	}

	public PunchOutException(HttpStatus httpStatus, final String message, final Throwable cause)
	{
		super(message, cause);
		if (null == httpStatus)
		{
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			LOG.warn("HttpStatus can not be null");
		}
		this.errorCode = Integer.toString(httpStatus.value());
	}

	public PunchOutException(final String errorCode, final String message, final Throwable cause)
	{
		super(message, cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode()
	{
		return errorCode;
	}
}
