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
 * 
 * Order filter populator service
 *
 */
public interface SapS4OMOrderFilterBuilder {
	
	/**
	 * Get the list of filter for order history search
	 *
	 * @param customerModel   the CustomerModel
	 * @param status
	 *           One or more OrderStatuses to include in the result
	 * @param pageableData
	 *           pagination information
	 * @param sortType 
	 * @return Map<String,List<FilterData>>
	 * 			 the list of filter information
	 */
	Map<String,List<FilterData>> getOrderHistoryFilters(CustomerModel customerModel,OrderStatus[] status,
			PageableData pageableData, String sortType);
	
	/**
	 * Get the list of filter for order details search
	 * 
	 * @return Map<String,List<FilterData>>
	 * 			 the list of filter information
	 */
	Map<String,List<FilterData>> getOrderDetailFilters();
	
	/**
	 * Get the  filter for billing document search
	 * 
	 * @return Map<String,List<FilterData>>
	 * 			 the list of filter information
	 */
	
	Map<String,List<FilterData>> getBillingDetailFilters(String orderCode);
	
	/**
	 * Get the  filter for billing PDF search
	 * 
	 * @return Map<String,List<FilterData>>
	 * 			 the list of filter information
	 */
	
	Map<String,List<FilterData>> getBillingPDFFilters(String billingDocumentID);

}
