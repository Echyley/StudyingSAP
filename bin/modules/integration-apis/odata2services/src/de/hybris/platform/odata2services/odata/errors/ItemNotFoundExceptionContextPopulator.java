/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.integrationservices.search.ItemNotFoundException;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} for handling {@link ItemNotFoundException}s.
 */
public final class ItemNotFoundExceptionContextPopulator implements ErrorContextPopulator
{
	@Override
	public void populate(@NotNull final ODataErrorContext context)
	{
		final var contextException = context.getException();
		if (getExceptionClass().isInstance(contextException))
		{
			final var exception = getExceptionClass().cast(contextException);
			context.setHttpStatus(HttpStatusCodes.NOT_FOUND);
			context.setErrorCode("not_found");
			context.setMessage(exception.getMessage());
			context.setLocale(Locale.ENGLISH);
			context.setInnerError(exception.getIntegrationKey());
		}
	}

	@Override
	public Class<ItemNotFoundException> getExceptionClass()
	{
		return ItemNotFoundException.class;
	}
}
