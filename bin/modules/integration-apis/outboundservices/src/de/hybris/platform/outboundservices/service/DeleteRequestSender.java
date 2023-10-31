/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service;

import de.hybris.platform.outboundservices.facade.SyncParameters;

/**
 * A service for sending item deletions to the external systems.
 */
public interface DeleteRequestSender
{
	/**
	 * Sends item deletion to an external system.
	 *
	 * @param params parameters for the change notification.
	 */
	void send(SyncParameters params);
}
