/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.service;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;

import java.util.Set;


/**
 * SapCpiOmsReturnsOutboundConversionService
 */
public interface SapCpiOmsReturnsOutboundConversionService
{
	/**
	 * convertReturnOrderToSapCpiOutboundReturnOrder
	 *
	 * @param clonedReturnModel ReturnRequestModel
	 * @param consignments          Set<ConsignmentEntryModel>
	 * @return SAPCpiOutboundReturnOrderModel
	 */
	SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel clonedReturnModel,
			Set<ConsignmentEntryModel> consignments);

	/**
	 * convertCancelReturnOrderToSapCpiOutboundReturnOrder
	 *
	 * @param clonedReturnModel ReturnRequestModel
	 * @param consignments          Set<ConsignmentEntryModel>
	 * @return SAPCpiOutboundReturnOrderModel
	 */
	SAPCpiOutboundReturnOrderModel convertCancelReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel clonedReturnModel,
			Set<ConsignmentEntryModel> consignments);

}
