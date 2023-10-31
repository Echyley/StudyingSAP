/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.integration.service.SwitchableEntryNumberHook;

import java.util.Map;


/**
 * Implementation for integration testing, that implements {@link SwitchableEntryNumberHook} behavior.
 */
public class SwitchableProductConfigIntegrationCloneAbstractOrderHook
		extends DefaultProductConfigIntegrationCloneAbstractOrderHook implements SwitchableEntryNumberHook
{

	private boolean isEntryNumberSpacingActive = false;

	public SwitchableProductConfigIntegrationCloneAbstractOrderHook(final int itemsStart, final int itemsIncrement)
	{
		super(itemsStart, itemsIncrement);
	}

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