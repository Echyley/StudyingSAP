/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services;

import java.util.List;
import java.util.Map;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;


/**
 * A hook strategy to run custom code after populate order filter attributes
 */
public interface SapS4OMOrderFilterBuilderHook
{
	/**
	 * Get the list of filter for order history search
	 *
	 * @param filters
	 * @param customerModel   the CustomerModel
	 * @param status
	 *           One or more OrderStatuses to include in the result
	 * @param pageableData
	 *           pagination information
	 * @param sortType 
	 */
	void  afterOrderHistoryFilter(Map<String,List<FilterData>> filters,CustomerModel customerModel,OrderStatus[] status,
			PageableData pageableData, String sortType);
	
	/**
	 * Get the list of filter for order details search
	 *
	 * @param filters
	 */
	void  afterOrderDetailsFilter(Map<String,List<FilterData>> filters);

}
