/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service;

import de.hybris.platform.outboundservices.event.EventType;

import org.springframework.http.HttpHeaders;

/**
 * A service that generate CloudEvent required headers.
 */
public interface CloudEventHeadersService
{
	/**
	 * Generate CloudEvent Headers
	 *
	 * @param  integrationObjectCode  the name of the integration object for CloudEvent type header
	 * @param  integrationKey         integrationKey of item for the CloudEvent subject header
	 * @param  eventType              value of {@link EventType}
	 * @param  sapPassport            value for CloudEvent sap passport header
	 * @return generated new headers instance only including CloudEvent headers
	 */
	HttpHeaders generateCloudEventHeaders(String integrationObjectCode, String integrationKey, EventType eventType, String sapPassport);
}
