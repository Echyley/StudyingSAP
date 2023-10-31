/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultConfigurationLifecycleStrategyImplTest
{
	private static final String CONFIG_ID = "config id";
	private DefaultConfigurationLifecycleStrategyImpl classUnderTest;

	@Mock
	private ProviderFactory providerFactory;

	@Mock
	private ConfigurationProvider configurationProvider;

	@Mock
	private ProductConfigurationCacheAccessService productConfigurationCacheAccessService;

	@Mock
	private SessionService sessionService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultConfigurationLifecycleStrategyImpl();
		classUnderTest.setProviderFactory(providerFactory);
		classUnderTest.setProductConfigurationCacheAccessService(productConfigurationCacheAccessService);
		classUnderTest.setSessionService(sessionService);
	}

	@Test
	public void testIsConfigInSession()
	{
		assertTrue(classUnderTest.isConfigForCurrentUser(CONFIG_ID));
	}

	@Test
	public void testIsConfigKnown()
	{
		assertTrue(classUnderTest.isConfigKnown(CONFIG_ID));
	}

	@Test
	public void testReleaseExpiredSessions()
	{
		final ProductConfigSessionAttributeContainer container = new ProductConfigSessionAttributeContainer();
		container.getProductConfigurations().put("1", "1");
		container.getCartEntryConfigurations().put("2", "2");
		container.getCartEntryDraftConfigurations().put("3", "3");

		when(sessionService.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER)).thenReturn(container);
		when(providerFactory.getConfigurationProvider()).thenReturn(configurationProvider);

		classUnderTest.releaseExpiredSessions("s111");

		verify(configurationProvider, times(3)).releaseSession(anyString());
		verify(productConfigurationCacheAccessService, times(3)).removeConfigAttributeState(anyString());
	}

}
