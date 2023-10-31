/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;

import java.util.Locale;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} for handling {@link ModelRemovalExceptionContextPopulator}s.
 */
public class ModelRemovalExceptionContextPopulator implements ErrorContextPopulator
{
	private static final String ERROR_CODE = "deletion_failure";
	private static final String DEFAULT_MESSAGE = "Unexpected error during object removal";

	@Override
	public void populate(@NotNull final ODataErrorContext context)
	{
		if (context.getException() instanceof ModelRemovalException)
		{
			final var ex = (ModelRemovalException) context.getException();
			context.setMessage(translateMessage(ex.getMessage()));
			context.setErrorCode(ERROR_CODE);
			context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
			context.setLocale(Locale.ENGLISH);
		}
	}

	private String translateMessage(final String originalMessage)
	{
		return StringUtils.substringAfter(Objects.toString(originalMessage, DEFAULT_MESSAGE), "]:");
	}

	@Override
	public Class<? extends Exception> getExceptionClass()
	{
		return ModelRemovalException.class;
	}
}

