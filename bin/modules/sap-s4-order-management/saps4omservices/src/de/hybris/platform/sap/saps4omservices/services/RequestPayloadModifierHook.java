/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import java.util.List;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;

public interface RequestPayloadModifierHook {
	
	void modifyPayloadForOrder(OrderModel order, SAPS4OMRequestData requestData);
	
	void modifyPayloadForOrderSimulation(AbstractOrderModel order, SAPS4OMRequestData requestData);
	
	void modifyPayloadForOrderSimulation(List<ProductModel> products, SAPS4OMRequestData requestData);

}
