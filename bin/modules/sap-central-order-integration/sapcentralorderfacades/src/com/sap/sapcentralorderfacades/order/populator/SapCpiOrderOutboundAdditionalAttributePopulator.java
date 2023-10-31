/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.populator;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;


/**
 * SapCpiOrderOutboundAdditionalAttributePopulator
 */
public interface SapCpiOrderOutboundAdditionalAttributePopulator
{

	/**
	 * Adds additional attributes to SAPCpiOutboundOrderModel
	 *
	 * @param orderModel
	 * @param sapCpiOutboundOrderModel
	 */
	void addAdditionalAttributesToSapCpiOrder(OrderModel orderModel,
			SAPCpiOutboundOrderModel sapCpiOutboundOrderModel);

}
