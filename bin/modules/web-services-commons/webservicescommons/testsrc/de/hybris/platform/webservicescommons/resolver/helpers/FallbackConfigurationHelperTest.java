/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webservicescommons.resolver.helpers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link FallbackConfigurationHelper}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FallbackConfigurationHelperTest
{
	private static final String DEFAULT_MESSAGE = "Default message.";
	private static final String RUNTIME_EXCEPTION_SIMPLE_NAME = "RuntimeException";
	private static final String TEST_EXTENSION = "testExtension";
	private static final String GROUP_MESSAGE = "Group message.";
	private static final String EXTENSION_MESSAGE = "Extension message.";

	private final List<String> propertyFormats = new ArrayList<>(Arrays.asList( //
			"test.{{extension}}.{{group}}.{{attribute}}", //
			"test.{{group}}.{{attribute}}", //
			"test.default.{{attribute}}"));

	@Mock
	private Configuration configuration;

	@Mock
	private ConfigurationService configurationService;

	private FallbackConfigurationHelper fallbackConfigurationHelper;

	@Before
	public void initMocks()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(anyString(), any())).thenReturn(null);
		fallbackConfigurationHelper = new FallbackConfigurationHelper(propertyFormats, configurationService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullExtension()
	{
		fallbackConfigurationHelper.getFirstValue(null, RUNTIME_EXCEPTION_SIMPLE_NAME, "message");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullExceptionName()
	{
		fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, null, "message");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullAttribute()
	{
		fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullValueMapper()
	{
		fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "message", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullSubstitutions()
	{
		fallbackConfigurationHelper.getFirstValue(null, Integer::valueOf);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullValueMapperWhenSubstitutionsNotNull()
	{
		fallbackConfigurationHelper.getFirstValue(new HashMap<>(), null);
	}

	@Test
	public void shouldReturnEmptyOptional()
	{
		final Optional<String> message = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "message");
		assertEquals(Optional.empty(), message);
	}

	@Test
	public void shouldReturnDefaultPropertyMessage()
	{
		when(configuration.getString("test.default.message", null)).thenReturn(DEFAULT_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "message");
		assertEquals(DEFAULT_MESSAGE, message.get());
	}

	@Test
	public void shouldReturnDefaultStatus()
	{
		final Optional<Integer> status = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "status", Integer::valueOf);
		assertEquals(Optional.empty(), status);
	}

	@Test
	public void shouldReturnGroupMessage()
	{
		when(configuration.getString("test.RuntimeException.message", null)).thenReturn(GROUP_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "message");
		assertEquals(GROUP_MESSAGE, message.get());
	}

	@Test
	public void shouldReturnGroupMessageWhenDefaultOneIsPresent()
	{
		when(configuration.getString("test.RuntimeException.message", null)).thenReturn(GROUP_MESSAGE);
		lenient().when(configuration.getString("test.default.message", null)).thenReturn(DEFAULT_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "message");
		assertEquals(GROUP_MESSAGE, message.get());
	}

	@Test
	public void shouldReturnExtensionMessage()
	{
		when(configuration.getString("test.testExtension.RuntimeException.message", null)).thenReturn(EXTENSION_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, "RuntimeException", "message");
		assertEquals(EXTENSION_MESSAGE, message.get());
	}

	@Test
	public void shouldReturnExtensionMessageWhenGroupOneIsPresent()
	{
		lenient().when(configuration.getString("test.default.message", null)).thenReturn(DEFAULT_MESSAGE);
		lenient().when(configuration.getString("test.RuntimeException.message", null)).thenReturn(GROUP_MESSAGE);
		when(configuration.getString("test.testExtension.RuntimeException.message", null)).thenReturn(EXTENSION_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, "RuntimeException", "message");
		assertEquals(EXTENSION_MESSAGE, message.get());
	}

	@Test
	public void shouldReturnExtensionMessageWhenGroupAndDefaultIsPresent()
	{
		lenient().when(configuration.getString("test.RuntimeException.message", null)).thenReturn(GROUP_MESSAGE);
		when(configuration.getString("test.testExtension.RuntimeException.message", null)).thenReturn(EXTENSION_MESSAGE);
		final Optional<String> message = fallbackConfigurationHelper.getFirstValue(TEST_EXTENSION, "RuntimeException", "message");
		assertEquals(EXTENSION_MESSAGE, message.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenInvalidValueForMapper()
	{
		when(configuration.getString("test.default.status", null)).thenReturn("400abc");
		final Optional<Integer> status = fallbackConfigurationHelper
				.getFirstValue(TEST_EXTENSION, RUNTIME_EXCEPTION_SIMPLE_NAME, "status", Integer::valueOf);
	}
}
