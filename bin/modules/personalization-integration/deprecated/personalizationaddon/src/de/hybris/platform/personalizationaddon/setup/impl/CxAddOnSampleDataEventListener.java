/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationaddon.setup.impl;

import de.hybris.platform.addonsupport.setup.impl.GenericAddOnSampleDataEventListener;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.util.Config;


public class CxAddOnSampleDataEventListener extends GenericAddOnSampleDataEventListener
{
	@Override
	protected void onEvent(final AbstractEvent event)
	{
		if (Config.getBoolean("personalizationaddon.import.active", false))
		{
			super.onEvent(event);
		}
	}
}
