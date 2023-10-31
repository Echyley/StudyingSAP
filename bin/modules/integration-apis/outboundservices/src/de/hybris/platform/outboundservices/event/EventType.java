/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.event;

import java.io.Serializable;

/**
 * The EventType stores the operation value for CloudEvent type
 */
public interface EventType extends Serializable
{
	/**
	 * Get the event type value
	 *
	 * @return string event type
	 */
	String getType();
}
