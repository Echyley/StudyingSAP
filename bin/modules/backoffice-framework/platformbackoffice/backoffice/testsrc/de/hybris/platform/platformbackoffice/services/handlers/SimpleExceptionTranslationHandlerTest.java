/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SimpleExceptionTranslationHandlerTest
{
	private final static String LOCALIZED_MESSAGE = "localized message";
	private final static String LABEL_KEY = "label.key";

	@Spy
	private SimpleExceptionTranslationHandler handler;


	@Before
	public void setUp()
	{
		doReturn(LOCALIZED_MESSAGE).when(handler).getLocalizedMessage(any());
	}

	@Test
	public void toStringShouldReturnStaticLabel()
	{
		handler.setLabelKey(LABEL_KEY);

		assertThat(handler.toString(new Exception())).isSameAs(LOCALIZED_MESSAGE);
		verify(handler).getLocalizedMessage(LABEL_KEY);
	}

	@Test
	public void toStringShouldReturnNull()
	{
		handler.setLabelKey(null);
		assertThat(handler.toString(new Exception())).isNull();
		verify(handler, never()).getLocalizedMessage(LABEL_KEY);
	}
}
