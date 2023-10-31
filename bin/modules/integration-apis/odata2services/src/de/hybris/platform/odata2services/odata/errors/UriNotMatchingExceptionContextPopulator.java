/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.uri.UriNotMatchingException;

/**
 * An error context populator for {@link UriNotMatchingException}
 */
public class UriNotMatchingExceptionContextPopulator implements ErrorContextPopulator
{
	@Override
	public void populate(@NotNull final ODataErrorContext context)
	{
		final var contextException = context.getException();
		if (canHandle(contextException))
		{
			final var exception = (UriNotMatchingException) contextException;
			context.setHttpStatus(HttpStatusCodes.NOT_FOUND);
			context.setErrorCode("missing_attribute");
			context.setMessage(exception.getMessage());
			context.setLocale(Locale.ENGLISH);
		}
	}

	private boolean canHandle(final Exception contextException)
	{
		return contextException instanceof UriNotMatchingException &&
				UriNotMatchingException.PROPERTYNOTFOUND.equals(
						((UriNotMatchingException) contextException).getMessageReference());
	}

	@Override
	public Class<? extends Exception> getExceptionClass()
	{
		return UriNotMatchingException.class;
	}
}

