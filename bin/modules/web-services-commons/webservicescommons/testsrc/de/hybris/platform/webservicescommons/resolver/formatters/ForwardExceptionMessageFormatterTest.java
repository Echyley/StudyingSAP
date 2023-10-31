/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webservicescommons.resolver.formatters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@link ForwardExceptionMessageFormatter}
 */
@UnitTest
public class ForwardExceptionMessageFormatterTest
{
	protected static final String TEST_ERROR_MESSAGE = "Test error message";
	protected static final String EMPTY_MESSAGE = "";
	protected static final String EXTENSION_NAME = "extensionName";

	private final ForwardExceptionMessageFormatter forwardExceptionMessageFormatter = new ForwardExceptionMessageFormatter();

	@Test
	public void shouldSetOriginalMessages()
	{
		final ErrorWsDTO errorWsDTO1 = new ErrorWsDTO();
		errorWsDTO1.setMessage(TEST_ERROR_MESSAGE);
		final ErrorWsDTO errorWsDTO2 = new ErrorWsDTO();
		errorWsDTO2.setMessage(EMPTY_MESSAGE);
		final ErrorWsDTO errorWsDTO3 = new ErrorWsDTO();
		errorWsDTO3.setMessage(null);
		final ErrorListWsDTO errorListWsDTO = new ErrorListWsDTO();
		errorListWsDTO.setErrors(Arrays.asList(errorWsDTO1, errorWsDTO2, errorWsDTO3));

		forwardExceptionMessageFormatter.setMessages(EXTENSION_NAME, new RuntimeException(), errorListWsDTO);

		assertEquals(3, errorListWsDTO.getErrors().size());
		assertEquals(TEST_ERROR_MESSAGE, errorListWsDTO.getErrors().get(0).getMessage());
		assertEquals(EMPTY_MESSAGE, errorListWsDTO.getErrors().get(1).getMessage());
		assertEquals(null, errorListWsDTO.getErrors().get(2).getMessage());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullExtensionName()
	{
		final ErrorListWsDTO errorListWsDTO = new ErrorListWsDTO();
		forwardExceptionMessageFormatter.setMessages(null, new RuntimeException(), errorListWsDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullExceptionObject()
	{
		final ErrorListWsDTO errorListWsDTO = new ErrorListWsDTO();
		forwardExceptionMessageFormatter.setMessages(EXTENSION_NAME, null, errorListWsDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullErrorListWsDTO()
	{
		forwardExceptionMessageFormatter.setMessages(EXTENSION_NAME, new RuntimeException(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullErrorListInsideErrorListWsDTO()
	{
		final ErrorListWsDTO errorListWsDTO = new ErrorListWsDTO();
		errorListWsDTO.setErrors(null);

		forwardExceptionMessageFormatter.setMessages(EXTENSION_NAME, new RuntimeException(), errorListWsDTO);
	}

	@Test
	public void shouldReturnTheSameErrorMessage()
	{
		final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
		errorWsDTO.setMessage(TEST_ERROR_MESSAGE);

		final String formattedMessage = forwardExceptionMessageFormatter
				.formatMessage(EXTENSION_NAME, new RuntimeException(), errorWsDTO);

		assertEquals(TEST_ERROR_MESSAGE, formattedMessage);
	}

	@Test
	public void shouldReturnNullErrorMessage()
	{
		final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
		errorWsDTO.setMessage(null);

		final String formattedMessage = forwardExceptionMessageFormatter
				.formatMessage(EXTENSION_NAME, new RuntimeException(), errorWsDTO);

		assertEquals(null, formattedMessage);
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException()
	{
		forwardExceptionMessageFormatter.formatMessage(EXTENSION_NAME, new RuntimeException(), null);
	}
}
