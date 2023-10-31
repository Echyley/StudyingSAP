/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.config.impl;

import de.hybris.platform.commerceservices.config.SiteConfigService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;

/**
 * Default implementation of site config service
 */
public class DefaultSiteConfigService implements SiteConfigService
{
	private ConfigurationService configurationService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Override
	public String getProperty(final String siteUid, final String property)
	{
		final Configuration configuration = getConfigurationService().getConfiguration();
		final String currentBaseSiteUid = "." + siteUid;

		// Try the site UID on its own
		// Fallback to the property key only
		return configuration.getString(property + currentBaseSiteUid, configuration.getString(property, null));
	}
}
