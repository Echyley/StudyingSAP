/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.config.impl;

import de.hybris.platform.integrationservices.config.BaseIntegrationServicesConfiguration;
import de.hybris.platform.webhookservices.config.WebhookServicesConfiguration;

/**
 * Default implementation for {@link WebhookServicesConfiguration}.
 */
public class DefaultWebhookServicesConfiguration extends BaseIntegrationServicesConfiguration
		implements WebhookServicesConfiguration
{
	private static final String EVENT_CONFIGURATION_ENABLED_KEY = "webhookservices.event.consolidation.enabled";
	private static final boolean EVENT_CONFIGURATION_ENABLED_KEY_FALLBACK = false;

	@Override
	public boolean isEventConsolidationEnabled()
	{
		return getBooleanProperty(EVENT_CONFIGURATION_ENABLED_KEY, EVENT_CONFIGURATION_ENABLED_KEY_FALLBACK);
	}
}
