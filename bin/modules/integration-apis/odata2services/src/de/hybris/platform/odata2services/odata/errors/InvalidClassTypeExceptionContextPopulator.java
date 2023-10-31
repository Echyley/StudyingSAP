/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.inboundservices.persistence.populator.InvalidClassTypeException;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} for handling {@link InvalidClassTypeException}.
 */
public class InvalidClassTypeExceptionContextPopulator implements ErrorContextPopulator
{
	private static final String ERROR_CODE = "invalid_pojo_class";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final ODataErrorContext context)
	{
		final var contextException = context.getException();
		if (getExceptionClass().isInstance(contextException))
		{
			final var exception = getExceptionClass().cast(contextException);
			context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
			context.setErrorCode(ERROR_CODE);
			context.setMessage(exception.getMessage());
			context.setLocale(Locale.ENGLISH);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends Exception> getExceptionClass()
	{
		return InvalidClassTypeException.class;
	}
}
