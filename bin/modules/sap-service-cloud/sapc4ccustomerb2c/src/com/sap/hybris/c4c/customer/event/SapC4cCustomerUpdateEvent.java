/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 * Definition for Customer Update Event
 */
public class SapC4cCustomerUpdateEvent extends AbstractEvent implements ClusterAwareEvent
{

	private C4CCustomerData customerData;

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return sourceNodeId == targetNodeId;
	}

	/**
	 * @return the customerData
	 */
	public C4CCustomerData getCustomerData()
	{
		return customerData;
	}

	/**
	 * @param customerData
	 *           the customerData to set
	 */
	public void setCustomerData(final C4CCustomerData customerData)
	{
		this.customerData = customerData;
	}

}
