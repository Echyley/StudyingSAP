/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.exceptions.ValidationViolationException;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;



@RunWith(MockitoJUnitRunner.class)
public class ValidationFailedExceptionHandlerTest
{
	private static final String VALIDATION_EXCEPTION_LOCALIZED_ERROR_MESSAGE = "validation exception localized message";

	@InjectMocks
	@Spy
	private ValidationFailedExceptionHandler handler;
	@Mock
	private ValidationViolationException validationViolationException;
	@Mock
	HybrisConstraintViolation hybrisConstraintViolation;

	@Before
	public void setUp()
	{
		when(validationViolationException.getHybrisConstraintViolations())
				.thenReturn(new HashSet(Collections.singletonList(hybrisConstraintViolation)));
		when(hybrisConstraintViolation.getLocalizedMessage()).thenReturn(VALIDATION_EXCEPTION_LOCALIZED_ERROR_MESSAGE);
	}


	@Test
	public void toStringShouldReturnStaticLabel()
	{
		assertThat(handler.toString(new Exception(validationViolationException)))
				.isEqualTo(VALIDATION_EXCEPTION_LOCALIZED_ERROR_MESSAGE + " ");
	}

	@Test
	public void toStringShouldReturnNothing()
	{
		assertThat(handler.toString(new Exception())).isNull();
	}

}
