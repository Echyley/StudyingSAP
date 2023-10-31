/*
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

import de.hybris.platform.orderprocessing.events.SendDeliveryMessageEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;




public class SendDeliveryMessageAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendDeliveryMessageAction.class);

	private EventService eventService;

	@Override
	public void executeAction(final ConsignmentProcessModel process)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: " + process.getCode() + " in step " + getClass());
		}
		getEventService().publishEvent(getEvent(process));
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Autowired
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected SendDeliveryMessageEvent getEvent(final ConsignmentProcessModel process)
	{
		return new SendDeliveryMessageEvent(process);
	}
}
