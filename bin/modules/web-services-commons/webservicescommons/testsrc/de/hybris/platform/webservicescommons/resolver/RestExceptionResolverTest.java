/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webservicescommons.resolver;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.errors.factory.WebserviceErrorFactory;
import de.hybris.platform.webservicescommons.resolver.formatters.AbstractExceptionMessageFormatter;
import de.hybris.platform.webservicescommons.resolver.helpers.FallbackConfigurationHelper;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;

import static de.hybris.platform.webservicescommons.resolver.RestExceptionResolver.UNDEFINED_ERROR_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link RestExceptionResolver}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RestExceptionResolverTest
{
	private static final String TEST_EXTENSION = "testExtension";
	private static final String RUNTIME_EXCEPTION_SIMPLE_NAME = "RuntimeException";

	private final HttpMessageConverter<?>[] messageConverters = {};
	private final RestExceptionResolver restExceptionResolver = new RestExceptionResolver();
	@Mock
	private WebserviceErrorFactory webserviceErrorFactory;
	@Mock
	private FallbackConfigurationHelper fallbackConfigurationHelper;
	@Mock
	private Map<ExceptionMessageFormatterType, AbstractExceptionMessageFormatter> exceptionMapping;

	@Before
	public void initMocks()
	{
		restExceptionResolver.setMessageConverters(messageConverters);
		restExceptionResolver.setWebserviceErrorFactory(webserviceErrorFactory);
		restExceptionResolver.setFallbackConfigurationHelper(fallbackConfigurationHelper);
		restExceptionResolver.setExceptionMapping(exceptionMapping);
		restExceptionResolver.setExtensionName(TEST_EXTENSION);
	}

	@Test
	public void shouldReturnStatusForException()
	{
		when(fallbackConfigurationHelper.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("status"), any()))
				.thenReturn(Optional.of(Integer.valueOf(UNDEFINED_ERROR_STATUS)));
		final int status = restExceptionResolver.calculateStatusFromException(new RuntimeException());
		assertEquals(Integer.valueOf(UNDEFINED_ERROR_STATUS), Integer.valueOf(status));
	}

	@Test
	public void shouldReturnDefaultStatusForException()
	{
		when(fallbackConfigurationHelper.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("status"), any()))
				.thenReturn(Optional.empty());
		final int status = restExceptionResolver.calculateStatusFromException(new RuntimeException());
		assertEquals(Integer.valueOf(UNDEFINED_ERROR_STATUS), Integer.valueOf(status));
	}

	@Test
	public void shouldNotDisplayStack()
	{
		when(fallbackConfigurationHelper
				.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("logstack"), any()))
				.thenReturn(Optional.of(Boolean.FALSE));
		final boolean displayStack = restExceptionResolver.shouldDisplayStack(new RuntimeException());
		assertFalse(displayStack);
	}

	@Test
	public void shouldDisplayStack()
	{
		when(fallbackConfigurationHelper
				.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("logstack"), any()))
				.thenReturn(Optional.empty());
		final boolean displayStack = restExceptionResolver.shouldDisplayStack(new RuntimeException());
		assertTrue(displayStack);
	}

	@Test
	public void shouldReturnGenericExceptionMessageFormatter()
	{
		when(fallbackConfigurationHelper
				.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("messageFormatterType"), any()))
				.thenReturn(Optional.of(ExceptionMessageFormatterType.GENERIC));
		final ExceptionMessageFormatterType formatType = restExceptionResolver
				.calculateExceptionMessageFormatter(new RuntimeException());
		assertEquals(ExceptionMessageFormatterType.GENERIC, formatType);
	}

	@Test
	public void shouldReturnForwardExceptionMessageFormatter()
	{
		when(fallbackConfigurationHelper
				.getFirstValue(eq(TEST_EXTENSION), eq(RUNTIME_EXCEPTION_SIMPLE_NAME), eq("messageFormatterType"), any()))
				.thenReturn(Optional.empty());
		final ExceptionMessageFormatterType formatType = restExceptionResolver
				.calculateExceptionMessageFormatter(new RuntimeException());
		assertEquals(ExceptionMessageFormatterType.FORWARD, formatType);
	}

	@Test
	public void shouldReturnExtensionName()
	{
		assertEquals(TEST_EXTENSION, restExceptionResolver.getExtensionName());
	}
}
