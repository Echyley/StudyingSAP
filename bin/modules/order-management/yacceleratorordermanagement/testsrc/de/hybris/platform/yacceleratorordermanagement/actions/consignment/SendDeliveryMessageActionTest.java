/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.events.SendDeliveryMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.event.EventService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.TestCase;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendDeliveryMessageActionTest extends TestCase
{
	@InjectMocks
	private final SendDeliveryMessageAction sendPaymentFailedNotification = new SendDeliveryMessageAction();

	@Mock
	private EventService eventService;

	/**
	 * Test method for
	 * {@link SendDeliveryMessageAction#executeAction(ConsignmentProcessModel)}
	 * .
	 */
	@Test
	public void testExecuteActionOrderProcessModel()
	{
		final ConsignmentProcessModel process = new ConsignmentProcessModel();
		sendPaymentFailedNotification.executeAction(process);

		final ArgumentMatcher<SendDeliveryMessageEvent> matcher = event -> event.getProcess().equals(process);
		Mockito.verify(eventService).publishEvent(Mockito.argThat(matcher));

	}
}
