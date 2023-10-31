/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.monitoring;

/**
 * A service to create InboundRequests. An InboundRequest relates to an invocation
 * into the integration API.
 */
public interface InboundRequestService
{
	/**
	 * Collects entities contained in the incoming request and the corresponding response, turns them into {@code InboundRequest}
	 * and persists them.
	 * 
	 * @param param information necessary to create an InboundRequest
	 */
	void register(InboundRequestServiceParameter param);
}
