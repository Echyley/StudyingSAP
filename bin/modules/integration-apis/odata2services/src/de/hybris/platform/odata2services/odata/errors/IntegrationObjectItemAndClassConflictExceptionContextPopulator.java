/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} that provides error values for {@link IntegrationObjectItemAndClassConflictException}s.
 */
public final class IntegrationObjectItemAndClassConflictExceptionContextPopulator implements ErrorContextPopulator
{
	private static final String ERROR_CODE = "conflicting-schema";

	@Override
	public void populate(final ODataErrorContext context)
	{
		final var ex = context.getException();
		context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
		context.setMessage(ex.getMessage());
		context.setErrorCode(ERROR_CODE);
	}

	@Override
	public Class<IntegrationObjectItemAndClassConflictException> getExceptionClass()
	{
		return IntegrationObjectItemAndClassConflictException.class;
	}
}
