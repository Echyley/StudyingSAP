/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.event.listeners;

import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_PROP;

import de.hybris.platform.apiregistryservices.event.EventExportDisabledEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.util.Config;

/**
 * Service-layer listener of @{@link EventExportDisabledEvent}
 * Switches event exporting parameter to false
 */
public class EventExportDisabledEventListener extends AbstractEventListener<EventExportDisabledEvent>
{
	@Override
	protected void onEvent(final EventExportDisabledEvent eventExportEvent)
	{
		Config.setParameter(EXPORTING_PROP, String.valueOf(false));
	}
}
