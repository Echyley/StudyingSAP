/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.store.BaseStoreModel;

import org.springframework.http.ResponseEntity;


/**
 * Service for CentralOrder
 */
public interface CentralOrderService
{

	/**
	 * gets the list of orders from centralorder.
	 *
	 * @param customerModel
	 *           contains the customer details
	 * @param store
	 *           contains the store details
	 * @param status
	 *           order status
	 * @param pageableData
	 *           the pageable data
	 * @param sourceSystemId
	 *           Commerce System Id
	 * @return {@link ResponseEntity<CentralOrderListResponse[]>}
	 */
	ResponseEntity<CentralOrderListResponse[]> getCentalOrderList(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderStatus[] status, final PageableData pageableData, String sourceSystemId);


	/**
	 * gets order detail from centralorder.
	 *
	 * @param customerModel
	 *           contains the customer details
	 * @param orderCode
	 *           order Code
	 * @param sourceSystemId
	 *           Commerce System Id
	 *
	 * @return {@link ResponseEntity<CentralOrderDetailsResponse[]>}
	 */
	ResponseEntity<CentralOrderDetailsResponse> getCentalOrderDetailsForCode(CustomerModel customerModel, String orderCode,
			String sourceSystemId);


	/**
	 * gets order detail from centralorder.
	 *
	 * @param customerModel
	 *           contains the customer details
	 * @param guid
	 *           unique guid
	 * @param sourceSystemId
	 *           Commerce System Id
	 *
	 * @return {@link ResponseEntity<CentralOrderDetailsResponse>}
	 */
	ResponseEntity<CentralOrderDetailsResponse> getCentalOrderDetailsForGuid(CustomerModel customerModel, String guid,
			String sourceSystemId);
}
