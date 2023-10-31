/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcarcommercefacades.order;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;

import java.util.Date;


/**
 * Interface to provide Order information pertaining to a given CAR instance as well as overall order history
 */
public interface CarOrderFacade
{

	/**
	 * Returns the detail of an Order corresponding to unique key (date, store, transaction index).
	 *
	 * @param transactionDate
	 * @param storeId
	 * @param transactionIndex
	 * @return
	 */
	CarOrderHistoryData getOrderDetails(String transactionDate, String storeId, Integer transactionIndex);


	/**
	 * Returns the order history of the current user for given customer Id.
	 *
	 * @param pageableData
	 * @return The order history made in store for current user.
	 */
	SearchPageData<CarOrderHistoryData> getPagedOrderHistoryForCustomer(PageableData pageableData);


	/**
	 * @param pageableData
	 * @return
	 */
	SearchPageData<CarMultichannelOrderHistoryData> getPagedMultichannelOrderHistoryForCustomer(PageableData pageableData);


	/**
	 * @param customerNumber
	 * @param TransactionNumber
	 * @return
	 */
	CarMultichannelOrderHistoryData getSalesDocumentDetails(String TransactionNumber);

}
