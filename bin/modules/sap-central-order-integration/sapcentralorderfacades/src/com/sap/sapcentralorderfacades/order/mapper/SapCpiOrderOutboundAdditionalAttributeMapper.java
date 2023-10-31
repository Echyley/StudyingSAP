/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;


/**
 * SapCpiOrderOutboundAdditionalAttributeMapper
 */
public interface SapCpiOrderOutboundAdditionalAttributeMapper
{

	/**
	 * Maps additional attributes to SAPCpiOutboundOrderModel
	 *
	 * @param orderModel
	 * @param sapCpiOutboundOrderModel
	 */
	void mapAdditionalAttributes(OrderModel orderModel, SAPCpiOutboundOrderModel sapCpiOutboundOrderModel);
}
