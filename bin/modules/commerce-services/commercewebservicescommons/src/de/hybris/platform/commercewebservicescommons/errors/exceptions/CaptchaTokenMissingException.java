/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.errors.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


public class CaptchaTokenMissingException extends WebserviceException
{
	private static final String TYPE = "CaptchaTokenMissingError";
	private static final String SUBJECT_TYPE = "captcha";
	private static final String DEFAULT_ERROR_MESSAGE = "The captcha response token is required but not provided.";

	public CaptchaTokenMissingException()
	{
		super(DEFAULT_ERROR_MESSAGE);
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
