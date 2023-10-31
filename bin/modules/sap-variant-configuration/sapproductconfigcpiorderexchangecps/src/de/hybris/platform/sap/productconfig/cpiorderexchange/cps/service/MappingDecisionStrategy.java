/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Deciding if we are going to create an S/4 Hana A2A service compliant representation of the configuration
 */
public interface MappingDecisionStrategy
{
	/**
	 * Checks if the A2A service mapping is active. This can be told from the SAPConfiguration that is attached to the
	 * order's base store
	 *
	 * @param order
	 * @return A2A service mapping active?
	 */
	boolean isA2AServiceMappingActive(AbstractOrderModel order);
}
