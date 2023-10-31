/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;

import java.util.Map;


/**
 * Implementation for integration testing, that implements {@link SwitchableEntryNumberHook} behavior.
 */
public class SwitchableProductConfigIntegrationCloneAbstractOrderHook extends DefaultProductConfigCloneAbstractOrderHook
		implements SwitchableEntryNumberHook
{

	public SwitchableProductConfigIntegrationCloneAbstractOrderHook(final ConfigurationService cpqService,
			final ConfigurationServiceLayerHelper serviceLayerHelper, final int itemsStart, final int itemsIncrement)
	{
		super(cpqService, serviceLayerHelper, itemsStart, itemsIncrement);
	}

	private boolean isEntryNumberSpacingActive = false;

	@Override
	public void adjustEntryNumbers(final Map<AbstractOrderEntryModel, Integer> entryNumberMappings)
	{
		if (isEntryNumberSpacingActive)
		{
			super.adjustEntryNumbers(entryNumberMappings);
		}
	}

	@Override
	public void activateEntryNumberSpacing()
	{
		isEntryNumberSpacingActive = true;
	}

	@Override
	public void deActivateEntryNumberSpacing()
	{
		isEntryNumberSpacingActive = false;
	}

}
