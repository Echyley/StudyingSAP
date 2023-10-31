/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.config.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSiteConfigServiceTest
{
	private static final BaseSiteModel BASE_SITE_MODEL = new BaseSiteModel();
	private static final String SITE_UID = "electronics";
	private static final String PROPERTY = "a";
	private static final String VALUE = "value";

	@InjectMocks
	private DefaultSiteConfigService siteConfigService;

	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		BASE_SITE_MODEL.setUid(SITE_UID);
	}

	@Test
	public void testGetProperty_site()
	{
		final Configuration configuration = new BaseConfiguration();
		when(this.configurationService.getConfiguration()).thenReturn(configuration);
		configuration.addProperty("a.electronics", VALUE);

		assertThat(this.siteConfigService.getProperty(SITE_UID, PROPERTY)).isEqualTo(VALUE);
	}

	@Test
	public void testGetProperty_global()
	{
		final Configuration configuration = new BaseConfiguration();
		when(this.configurationService.getConfiguration()).thenReturn(configuration);
		configuration.addProperty("a", VALUE);

		assertThat(this.siteConfigService.getProperty(SITE_UID, PROPERTY)).isEqualTo(VALUE);
	}
}
