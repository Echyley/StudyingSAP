/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.config;

import de.hybris.platform.webhookservices.service.impl.WebhookEventAggregator;

/**
 * Provides access methods to configurations related to the webhookservices module.
 */
public interface WebhookServicesConfiguration
{

	/**
	 * Retrieves the flag that determines whether the event consolidation is enabled or not.
	 * See {@link WebhookEventAggregator#aggregate} for more information on event consolidation.
	 *
	 * @return {@code true}, if event consolidation is enabled; {@code false} if it's disabled.
	 */
	boolean isEventConsolidationEnabled();
}
