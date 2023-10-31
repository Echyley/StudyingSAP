/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.sapcentralorderservices.clients.CentralOrderApiClient;
import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;
import com.sap.sapcentralorderservices.exception.SapCentralOrderException;

/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderServiceTest
{


	DefaultCentralOrderService defaultCentralOrderService = new DefaultCentralOrderService();

	String orderCode = "orderCode";

	String sourceSystemId = "sourceSystemId";

	String uriString = SapcentralorderservicesConstants.EMPTY_STRING;


	CentralOrderApiClient centralOrderApiClient = mock(CentralOrderApiClient.class);

	@Test
	public void getCentralOrderDetailsTest() throws SapCentralOrderException
	{
		defaultCentralOrderService.setCentralOrderApiClient(centralOrderApiClient);
		final CustomerModel customerModel = new CustomerModel();

		final CentralOrderDetailsResponse centralOrderDetailsResponse = new CentralOrderDetailsResponse();
		centralOrderDetailsResponse.setId("id");

		final CentralOrderDetailsResponse[] centralOrderDetails = new CentralOrderDetailsResponse[1];
		centralOrderDetails[0] = centralOrderDetailsResponse;

		final ResponseEntity<CentralOrderDetailsResponse[]> responseEntity = new ResponseEntity<>(centralOrderDetails,
				HttpStatus.OK);

		final UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
				.queryParam(SapcentralorderservicesConstants.DOCUMENT_NUMBER, orderCode)
				.queryParam(SapcentralorderservicesConstants.SOURCE_SYSTEM_ID, sourceSystemId).build();

		when(centralOrderApiClient.getEntity(eq(uriComponents), eq(CentralOrderDetailsResponse[].class)))
				.thenReturn(responseEntity);

		final UriComponents uriComponentsforGuid = UriComponentsBuilder.fromUriString(uriString)
				.path(SapcentralorderservicesConstants.SLASH + "" + "id")
				.queryParam(SapcentralorderservicesConstants.SOURCE_SYSTEM_ID, sourceSystemId).build();

		final ResponseEntity<CentralOrderDetailsResponse> responseEntityWithGuid = new ResponseEntity<>(centralOrderDetailsResponse,
				HttpStatus.OK);

		when(centralOrderApiClient.getEntity(eq(uriComponentsforGuid), eq(CentralOrderDetailsResponse.class)))
				.thenReturn(responseEntityWithGuid);

		//execute
		final ResponseEntity<CentralOrderDetailsResponse> output = defaultCentralOrderService
				.getCentalOrderDetailsForCode(customerModel, orderCode, sourceSystemId);

		//verify
		Assert.assertNotNull(output);

	}

	@Test
	public void getCentalOrderListTest() throws SapCentralOrderException
	{
		defaultCentralOrderService.setCentralOrderApiClient(centralOrderApiClient);
		final PageableData pageableData = new PageableData();
		pageableData.setSort("sort");
		pageableData.setCurrentPage(3);
		pageableData.setPageSize(5);
		final BaseStoreModel store = new BaseStoreModel();
		final CustomerModel customerModel = new CustomerModel();
		final OrderStatus[] status = new OrderStatus[1];
		customerModel.setSapConsumerID("DummyCustomerID");
		final CentralOrderListResponse centralOrderResponse = new CentralOrderListResponse();

		final CentralOrderListResponse[] centralOrderListResponse = new CentralOrderListResponse[1];
		centralOrderListResponse[0] = centralOrderResponse;

		final ResponseEntity<CentralOrderListResponse[]> response = new ResponseEntity<>(centralOrderListResponse, HttpStatus.OK);

		when(centralOrderApiClient.getEntity(any(UriComponents.class), eq(CentralOrderListResponse[].class))).thenReturn(response);

		//execute
		final ResponseEntity<CentralOrderListResponse[]> output = defaultCentralOrderService.getCentalOrderList(customerModel,
				store, status, pageableData, sourceSystemId);

		//verify
		Assert.assertNotNull(output);
	}
}
