/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SSCSessionAccessServiceImplTest
{
	private final SSCProductConfigSessionAttributeContainer sessionContainer = new SSCProductConfigSessionAttributeContainer();

	@Mock
	private ConfigurationProvider configurationProvider;
	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;

	@InjectMocks
	private SSCSessionAccessServiceImpl classUnderTest;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(sessionContainer);
		classUnderTest.setSessionService(sessionService);
	}

	@Test
	public void testSetConfigurationProvider()
	{
		classUnderTest.setConfigurationProvider(configurationProvider);
		assertEquals(configurationProvider, sessionContainer.getConfigurationProvider());
	}

	@Test
	public void testSetConfigurationProviderNoSessionContainer()
	{
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(null);
		classUnderTest.setConfigurationProvider(configurationProvider);
		verify(sessionService).setAttribute(Mockito.anyString(), Mockito.any(SSCProductConfigSessionAttributeContainer.class));
	}

	@Test
	public void testGetConfigurationProvider()
	{
		sessionContainer.setConfigurationProvider(configurationProvider);
		assertEquals(configurationProvider, classUnderTest.getConfigurationProvider());
		verify(sessionService).getAttribute(SSCSessionAccessServiceImpl.SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
	}

	@Test
	public void testGetConfigurationProviderNotAvailable()
	{
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(null);
		assertNull(classUnderTest.getConfigurationProvider());
	}

}
