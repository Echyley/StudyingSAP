/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpireturnsexchange.service;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;

/**
 * SapCpiReturnOutboundConversionService
 */
public interface SapCpiReturnOutboundConversionService {
	/**
	 * convertReturnOrderToSapCpiOutboundReturnOrder
	 * @param returnRequest ReturnRequestModel
	 * @return SAPCpiOutboundReturnOrderModel
	 */
    SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel returnRequest);

}
