/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.errors.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class ConvertException extends WebserviceException
{
	private static final String TYPE = "ConvertError";
	private static final String SUBJECT_TYPE = "entry";

	public ConvertException(final String message)
	{
		super(message);
	}

	public ConvertException(final String message, final String reason)
	{
		super(message, reason);
	}

	public ConvertException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public ConvertException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public ConvertException(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}
}
