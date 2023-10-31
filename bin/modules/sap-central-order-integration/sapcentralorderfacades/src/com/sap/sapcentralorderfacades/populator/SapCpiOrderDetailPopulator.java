/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;


/**
 * SapCpiOrderDetailPopulator
 */
public interface SapCpiOrderDetailPopulator
{

	/**
	 * @param source
	 * @param target
	 */
	public void populate(final CentralOrderDetailsResponse source, final OrderData target);
}
