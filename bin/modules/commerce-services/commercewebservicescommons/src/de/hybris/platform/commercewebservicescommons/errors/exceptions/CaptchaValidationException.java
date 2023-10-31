/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.errors.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


public class CaptchaValidationException extends WebserviceException
{
	private static final String TYPE = "CaptchaValidationError";
	private static final String SUBJECT_TYPE = "captcha";
	private static final String DEFAULT_ERROR_MESSAGE = "Invalid answer to captcha challenge.";

	public CaptchaValidationException(final String reason)
	{
		super(DEFAULT_ERROR_MESSAGE, reason);
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
