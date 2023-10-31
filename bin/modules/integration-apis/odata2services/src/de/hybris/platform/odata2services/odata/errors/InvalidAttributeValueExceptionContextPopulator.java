/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.integrationservices.exception.InvalidAttributeValueException;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} for handling {@link InvalidAttributeValueException}.
 */
public final class InvalidAttributeValueExceptionContextPopulator implements ErrorContextPopulator
{
	@Override
	public void populate(final ODataErrorContext context)
	{
		final var contextException = context.getException();
		if (contextException instanceof InvalidAttributeValueException)
		{
			final var exception = (InvalidAttributeValueException) contextException;
			context.setMessage(exception.getMessage());
			context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
			context.setLocale(Locale.ENGLISH);
			context.setErrorCode("invalid_parameter_value");
		}
	}

	@Override
	public Class<InvalidAttributeValueException> getExceptionClass()
	{
		return InvalidAttributeValueException.class;
	}
}
