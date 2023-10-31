/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.event;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;

/**
 * Interface to filter Kyma events before sending them to Kyma.
 */
public interface KymaEventFilterService
{
	/**
	 * Filters the event that will be sent to Kyma.
	 *
	 * @param eventSourceData Contains Kyma event and its configuration
	 * @return A boolean flag indicates if the event will be sent to Kyma or not.
	 * If the flag is true, the event will be sent; otherwise the event will not be sent.
	 */
	boolean filterKymaEvent(final EventSourceData eventSourceData);
}
