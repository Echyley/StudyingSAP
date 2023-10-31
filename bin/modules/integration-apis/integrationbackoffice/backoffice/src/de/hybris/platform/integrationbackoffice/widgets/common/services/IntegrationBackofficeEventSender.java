/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.services;

import org.zkoss.zk.ui.Component;

/**
 * A service for sending UI events.
 */
public interface IntegrationBackofficeEventSender
{
	/**
	 * Sends a UI event applied to a component.
	 *
	 * @param event     Type of event to send
	 * @param component Component on which event is triggered
	 * @param data      Data which event state contains
	 * @return If event was successfully sent.
	 */
	boolean sendEvent(final String event, final Component component, final Object data);
}
