/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistrybackoffice.handlers;

import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.util.Config;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

import com.hybris.cockpitng.service.ExceptionTranslationHandler;

public class ApiRegistryExceptionTranslationHandler implements ExceptionTranslationHandler, Ordered
{
	private static final String SUPPORTED_MODELS = "apiregistrybackoffice.detailed.error.for.models";
	private static final String DELIMITER = ",";
	private static final String EMPTY_MESSAGE = "";
	private static final int MESSAGE_LENGTH = 2;
	private static final Pattern MESSAGE_PLACEHOLDER_PATTERN = Pattern.compile("]:");

	@Override
	public boolean canHandle(final Throwable throwable)
	{
		return Objects.nonNull(throwable) &&
				Objects.nonNull(throwable.getMessage()) &&
				isExceptionTypeSupported(throwable) &&
				isModelExceptionTranslationSupported(extractExceptionMessage(throwable));
	}

	@Override
	public String toString(final Throwable throwable)
	{
		final Throwable realThrowable = this.extractExceptionCause(throwable);
		final String[] message = MESSAGE_PLACEHOLDER_PATTERN.split(
				Objects.toString(realThrowable.getLocalizedMessage(), EMPTY_MESSAGE));
		return message.length == MESSAGE_LENGTH ? message[1] : throwable.getLocalizedMessage();
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}

	private Throwable extractExceptionCause(final Throwable throwable)
	{
		return isExceptionTypeSupported(throwable.getCause()) ? throwable.getCause() : throwable;
	}

	private String extractExceptionMessage(final Throwable throwable)
	{
		return extractExceptionCause(throwable).getMessage();
	}

	protected boolean isExceptionTypeSupported(final Throwable throwable)
	{
		return isModelSaveException(throwable) || isModelRemoveException(throwable);
	}

	protected boolean isModelRemoveException(final Throwable throwable)
	{
		return throwable instanceof ModelRemovalException ||
				throwable.getCause() instanceof ModelRemovalException;
	}

	protected boolean isModelSaveException(final Throwable throwable)
	{
		return throwable instanceof ModelSavingException ||
				throwable.getCause() instanceof ModelSavingException;
	}

	protected boolean isModelExceptionTranslationSupported(final String exceptionMessage)
	{
		return readConfiguredModels().anyMatch(exceptionMessage::contains);
	}

	protected Stream<String> readConfiguredModels()
	{
		final String property = Config.getParameter(SUPPORTED_MODELS);
		if (StringUtils.isNotBlank(property))
		{
			return Stream.of(property.split(DELIMITER));
		}
		else
		{
			return Stream.empty();
		}
	}

}
