/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapomsreturnprocess.returns.strategy;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

import java.util.List;
import java.util.Map;


public interface ReturnOrderStartegy
{
	/**
	 * This method will make a map of order entry and list of consignment as per the splitting strategy
	 *
	 * @param orderEntryConsignmentMap
	 */
	void splitOrder(Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap);


}
