/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import java.util.Map;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;

public interface ResponsePayloadModifierHook {
	
	void modifyPayloadForOrder(OrderModel order, SAPS4OMData responseData);

	void modifyPayloadForOrderSimulation(ItemModel itemModel, SAPS4OMData salesOrderSimulationData,	Map<String, Object> itemInfoMap);
	
}
