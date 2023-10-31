/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.localization.impl;

import de.hybris.platform.integrationbackoffice.localization.LocalizationService;

import org.zkoss.util.resource.Labels;

/**
 * Localization service used by the integration backoffice that uses {@link org.zkoss.util.resource.Labels} class for accessing
 * the resource bundle.
 */
public class DefaultIntegrationBackofficeLocalizationService implements LocalizationService
{
	@Override
	public String getLocalizedString(final String key)
	{
		final String value = Labels.getLabel(key);
		return value != null ? value : key;
	}

	@Override
	public String getLocalizedString(final String key, final Object[] params)
	{
		final String value = Labels.getLabel(key, params);
		return value != null ? value : key;
	}
}
