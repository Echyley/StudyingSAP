/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcarintegration.services;

import java.util.Date;
import java.util.List;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;


/**
 * Service to provide Order information for a given Customer's activity
 */
public interface CarOrderHistoryService
{

	/**
	 * read paginated order history for a customer
	 * 
	 * @param customerNumber
	 * @param paginationData
	 * 
	 * @return {@link List<CarOrderHistoryData>}
	 */
	abstract List<CarOrderHistoryData> readOrdersForCustomer(String customerNumber, PaginationData paginationData);


	/**
	 * return order history data with header, store and item data filled
	 * 
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @param customerNumber
	 * @return
	 */
	abstract CarOrderHistoryData readOrderDetails(String businessDayDate, String storeId, Integer transactionIndex,
			String customerNumber);


}
