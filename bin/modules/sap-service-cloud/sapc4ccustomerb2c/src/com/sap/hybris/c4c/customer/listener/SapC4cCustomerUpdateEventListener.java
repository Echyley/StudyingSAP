/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.listener;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;


/**
 * Catch the customer update event and publish to SCPI
 */
public class SapC4cCustomerUpdateEventListener extends AbstractEventListener<SapC4cCustomerUpdateEvent>
{

	private SapC4cCustomerPublicationService c4cCustomerPublicationService;
	private static final Logger LOGGER = LogManager.getLogger(SapC4cCustomerUpdateEventListener.class);


	@Override
	protected void onEvent(final SapC4cCustomerUpdateEvent event)
	{

		final C4CCustomerData customerData = event.getCustomerData();
		try
		{
			getC4cCustomerPublicationService().publishCustomerToCloudPlatformIntegration(customerData);
		}
		catch (final IOException e)
		{
			LOGGER.error("Failed to replicate customer " + customerData.getCustomerId(), e);
		}
	}


	/**
	 * @return the c4cCustomerPublicationService
	 */
	public SapC4cCustomerPublicationService getC4cCustomerPublicationService()
	{
		return c4cCustomerPublicationService;
	}


	/**
	 * @param c4cCustomerPublicationService the c4cCustomerPublicationService to set
	 */
	public void setC4cCustomerPublicationService(final SapC4cCustomerPublicationService c4cCustomerPublicationService)
	{
		this.c4cCustomerPublicationService = c4cCustomerPublicationService;
	}

}
