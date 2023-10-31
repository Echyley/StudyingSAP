/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.exception;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


public class PunchOutCartMissingException extends WebserviceException
{
	private static final String TYPE = "PunchOutCartMissingError";

	public PunchOutCartMissingException(String message, String reason, String subject, Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	public PunchOutCartMissingException(String message, String reason, String subject)
	{
		super(message, reason, subject);
	}

	public PunchOutCartMissingException(String message, String reason, Throwable cause)
	{
		super(message, reason, cause);
	}

	public PunchOutCartMissingException(String message, String reason)
	{
		super(message, reason);
	}

	public PunchOutCartMissingException(String message)
	{
		super(message);
	}

	public String getType()
	{
		return TYPE;
	}

	public String getSubjectType()
	{
		return null;
	}
}
