/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.services.impl;

import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;

/**
 * Default implementation of the {@link IntegrationBackofficeEventSender}
 */
public class DefaultIntegrationBackofficeEventSender implements IntegrationBackofficeEventSender
{
	/**
	 * Default implementation of the event sender that acts as a wrapper for ZK's Events.sendEvent()
	 *
	 * @param event     Type of event to send
	 * @param component Component on which event is triggered
	 * @param data      Data which event state contains
	 * @return If component and data are not null.
	 */
	@Override
	public boolean sendEvent(final String event, final Component component, final Object data)
	{
		if (component != null && data != null)
		{
			Events.sendEvent(event, component, data);
			return true;
		}
		else
		{
			return false;
		}
	}
}
