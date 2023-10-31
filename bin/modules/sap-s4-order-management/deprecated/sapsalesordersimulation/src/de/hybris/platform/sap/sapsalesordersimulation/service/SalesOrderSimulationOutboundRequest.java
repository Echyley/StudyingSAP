/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service;

import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationRequestData;

public interface SalesOrderSimulationOutboundRequest {
	/**
	 * Get the response from the API passing the requestData.
	 *
	 * @param requestData
	 * @return salesOrderSimulationData.
	 */
	 public SalesOrderSimulationData getResponseFromSalesOrderSimulation(SalesOrderSimulationRequestData requestData);
}
