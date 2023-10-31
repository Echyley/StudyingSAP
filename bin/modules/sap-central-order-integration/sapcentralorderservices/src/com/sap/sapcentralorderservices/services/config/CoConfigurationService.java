/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.config;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Configuration service for COS
 */
public interface CoConfigurationService
{

	/**
	 * @return {@link String}
	 */
	String getCoSourceSystemId();

	/**
	 * @return {@link Boolean}
	 */
	Boolean isCoActive();

	/**
	 * Returns if Central order is active
	 *
	 * @param orderModel
	 *           The orderModel containing the placed order
	 * @return {@link Boolean}
	 */
	Boolean isCoActiveFromBaseStore(OrderModel orderModel);

}
