/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator;

import de.hybris.platform.outboundservices.facade.SyncParameters;

import java.util.Map;

import org.springframework.http.HttpEntity;

/**
 * Service that encapsulates the decoration logic for outbound sync requests.
 */
public interface RequestDecoratorService
{
	/**
	 * Service that returns a decorated {@link HttpEntity} based on the context provided
	 *
	 * @param params containing the information needed to create the decorated {@link HttpEntity}
	 * @return a decorated {@link HttpEntity}
	 */
	HttpEntity<Map<String, Object>> createHttpEntity(SyncParameters params);
}
