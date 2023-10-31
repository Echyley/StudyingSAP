/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.event;

import de.hybris.platform.personalizationyprofile.segment.ConsumptionLayerUserSegmentsProvider;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InvalidateConsumptionLayerUserSegmentsProviderCacheEventHandler
		extends AbstractEventListener<InvalidateConsumptionLayerUserSegmentsProviderCacheEvent>
{
	private static final Logger LOG = LoggerFactory
			.getLogger(InvalidateConsumptionLayerUserSegmentsProviderCacheEventHandler.class);
	private ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider;

	@Override
	protected void onEvent(final InvalidateConsumptionLayerUserSegmentsProviderCacheEvent event)
	{
		consumptionLayerUserSegmentsProvider.resetFields();
		LOG.debug("ConsumptionLayerUserSegmentsProvider - profileFields cache has been invalidated");
	}

	public void setConsumptionLayerUserSegmentsProvider(
			final ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider)
	{
		this.consumptionLayerUserSegmentsProvider = consumptionLayerUserSegmentsProvider;
	}
}
