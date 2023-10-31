/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webservicescommons.resolver.formatters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.resolver.helpers.FallbackConfigurationHelper;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.webservicescommons.resolver.formatters.GenericExceptionMessageFormatter.DEFAULT_EXCEPTION_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GenericExceptionMessageFormatter}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenericExceptionMessageFormatterTest
{
	protected static final String GENERIC_EXCEPTION_MESSAGE = "Generic exception message";
	protected static final String EXTENSION_NAME = "extensionName";
	private static final String RUNTIME_EXCEPTION_NAME = "RuntimeException";

	@Mock
	private FallbackConfigurationHelper fallbackConfigurationHelper;

	@InjectMocks
	private GenericExceptionMessageFormatter genericExceptionMessageFormatter;

	@Test
	public void shouldReturnGenericExceptionMessage()
	{
		when(fallbackConfigurationHelper.getFirstValue(eq(EXTENSION_NAME), eq(RUNTIME_EXCEPTION_NAME), eq("message")))
				.thenReturn(Optional.of(GENERIC_EXCEPTION_MESSAGE));

		final String exceptionMessage = genericExceptionMessageFormatter
				.formatMessage(EXTENSION_NAME, new RuntimeException(), new ErrorWsDTO());

		assertEquals(GENERIC_EXCEPTION_MESSAGE, exceptionMessage);
	}

	@Test
	public void shouldReturnDefaultExceptionMessage()
	{
		when(fallbackConfigurationHelper.getFirstValue(eq(EXTENSION_NAME), eq(RUNTIME_EXCEPTION_NAME), eq("message")))
				.thenReturn(Optional.empty());

		final String exceptionMessage = genericExceptionMessageFormatter
				.formatMessage(EXTENSION_NAME, new RuntimeException(), new ErrorWsDTO());

		assertEquals(DEFAULT_EXCEPTION_MESSAGE, exceptionMessage);
	}
}
