/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.dataimportcommons.facades.impl.converter;

import de.hybris.platform.dataimportcommons.facades.DataImportError;
import de.hybris.platform.dataimportcommons.facades.DataItemImportResult;
import de.hybris.platform.dataimportcommons.facades.ErrorCode;
import de.hybris.platform.dataimportcommons.facades.impl.ImportResultConverter;
import de.hybris.platform.servicelayer.impex.ImpExError;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultImportErrorsConverter<T extends DataItemImportResult> implements ImportResultConverter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImportErrorsConverter.class);
	private static final String UNCLASSIFIED_IMPEX_ERROR = "UNCLASSIFIED_IMPEX_ERROR";
	public static final String GENERAL_ERROR = "ImpEx job has failed execution";
	private ImportService importService;
	private int errorLimit;

	@Override
	public T convert(final ImportResult importRes)
	{
		return importRes == null ? null : toItemImportResult(importRes);
	}

	protected T toItemImportResult(final ImportResult importRes)
	{
		final T result = importRes.isSuccessful()
				? createDataItemImportResult()
				: createDataItemImportResult(GENERAL_ERROR);
		if (importRes.isError())
		{
			result.addErrors(extractErrorsFrom(importRes));
		}
		return result;
	}

	protected T createDataItemImportResult()
	{
		return (T) new DataItemImportResult();
	}

	protected T createDataItemImportResult(final String msg)
	{
		return (T) new DataItemImportResult(msg);
	}

	protected Collection<DataImportError> extractErrorsFrom(final ImportResult importRes)
	{
		final Stream<? extends ImpExError> impExErrorStream;

		if (isLimited())
		{
			final int streamLimit = getErrorLimit() + 1;
			impExErrorStream = getImportService().collectImportErrors(importRes).limit(streamLimit);
		}
		else
		{
			impExErrorStream = getImportService().collectImportErrors(importRes);
		}

		final Collection<DataImportError> importErrors = impExErrorStream
				.map(this::toError)
				.collect(Collectors.toList());

		if (isLimited() && importErrors.size() > getErrorLimit())
		{
			throw new ErrorLimitExceededException();
		}

		return importErrors;
	}

	protected DataImportError toError(final ImpExError impExError)
	{
		String message;
		String errorCode;

		try
		{
			message = impExError.getErrorMessage();
			errorCode = ErrorCode.classify(message).toString();
		}
		catch (final RuntimeException e)
		{
			message = e.getMessage();
			errorCode = UNCLASSIFIED_IMPEX_ERROR;
			if (LOGGER.isWarnEnabled())
			{
				LOGGER.warn(e.getMessage(), e);
			}
		}

		return new DataImportError(message, errorCode);
	}

	/**
	 * Injects <code>ImportService</code> implementation to be used by this facade.
	 *
	 * @param impl implementation of the <code>ImportService</code> to use.
	 */
	@Required
	public void setImportService(final ImportService impl)
	{
		importService = impl;
	}

	/**
	 * Getter for importService.
	 *
	 * @return importService
	 */
	protected ImportService getImportService()
	{
		return importService;
	}

	/**
	 * The maximum number of errors to return before failing the entire publication
	 * @param errorLimit the error limit
	 */
	@Required
	public void setErrorLimit(final int errorLimit)
	{
		this.errorLimit = errorLimit;
	}

	/**
	 * Getter for error limit.
	 *
	 * @return error limit.
	 */
	protected int getErrorLimit()
	{
		return errorLimit;
	}

	/**
	 * Return the error result with limit if errorLimit set to >=0.
	 *
	 * @return is limited error results
	 */
	protected boolean isLimited()
	{
		return errorLimit >= 0;
	}
}
