/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.PublishEventContext;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class InvalidateConsumptionLayerUserSegmentsProviderCacheEvent extends AbstractEvent implements ClusterAwareEvent
{
	@Override
	public boolean canPublish(final PublishEventContext publishEventContext)
	{
		return true;
	}
}
