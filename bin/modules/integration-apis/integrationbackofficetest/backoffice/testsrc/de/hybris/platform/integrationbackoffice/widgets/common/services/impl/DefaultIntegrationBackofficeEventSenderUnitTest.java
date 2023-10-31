/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.services.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;


@UnitTest
public class DefaultIntegrationBackofficeEventSenderUnitTest
{
	private final DefaultIntegrationBackofficeEventSender backofficeEventSender = new DefaultIntegrationBackofficeEventSender();

	private final Combobox c = new Combobox("Test");
	private final Object o = new Object();

	@Test
	public void sendEventComponentIsNullDoesNotSendTest()
	{
		assertFalse(backofficeEventSender.sendEvent(Events.ON_CHANGE, null, o));
	}

	@Test
	public void sendEventDataIsNullDoesNotSendTest()
	{
		assertFalse(backofficeEventSender.sendEvent(Events.ON_CHANGE, c, null));
	}
}
