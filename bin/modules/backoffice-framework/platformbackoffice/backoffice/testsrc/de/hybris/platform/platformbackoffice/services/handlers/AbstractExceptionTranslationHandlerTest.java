/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.rmi.RemoteException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;


@RunWith(MockitoJUnitRunner.class)
public class AbstractExceptionTranslationHandlerTest
{
	@Spy
	private AbstractExceptionTranslationHandler handler;

	@Before
	public void setUp()
	{
		doReturn(IOException.class).when(handler).getExceptionClass();
	}

	@Test
	public void cnaHandleShouldReturnFalse()
	{
		assertThat(handler.canHandle(new NoClassDefFoundError())).isFalse();
	}

	@Test
	public void cnaHandleShouldReturnTrue()
	{
		assertThat(handler.canHandle(new RemoteException())).isTrue();
	}
}
