/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.facades.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderservices.services.CentralOrderService;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;

/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderFacadeTest
{


	DefaultCentralOrderFacade defaultCentralOrderFacade = new DefaultCentralOrderFacade();


	CoConfigurationService configurationService = mock(CoConfigurationService.class);


	BaseStoreService baseStoreService = mock(BaseStoreService.class);

	BaseStoreModel baseStoreModel = new BaseStoreModel();


	CentralOrderService centralOrderService = mock(CentralOrderService.class);


	UserService userService = mock(UserService.class);


	Populator centralOrderListPopulator = mock(Populator.class);

	Map<String, String> orderListSortMap = new HashMap();

	String sourceSystemId = "sourceSystemId";

	@Test
	public void getPagedOrderHistoryForStatusesTest()
	{
		defaultCentralOrderFacade.setBaseStoreService(baseStoreService);
		defaultCentralOrderFacade.setCentralOrderListPopulator(centralOrderListPopulator);
		defaultCentralOrderFacade.setCentralOrderService(centralOrderService);
		defaultCentralOrderFacade.setConfigurationService(configurationService);
		defaultCentralOrderFacade.setUserService(userService);

		final CustomerModel customerModel = new CustomerModel();

		final PageableData pageableData = new PageableData();
		pageableData.setSort("firstkey");
		pageableData.setCurrentPage(2);
		pageableData.setPageSize(5);

		final OrderStatus status = OrderStatus.CREATED;

		final OrderStatus[] orderStatus = new OrderStatus[1];
		orderStatus[0] = status;

		final CentralOrderListResponse response = new CentralOrderListResponse();

		final CentralOrderListResponse[] centralOrderList = new CentralOrderListResponse[1];
		centralOrderList[0] = response;

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(SapcentralorderfacadesConstants.ORDER_LIST_COUNT_HEADER, "5");

		final ResponseEntity<CentralOrderListResponse[]> centralOrderListResponse = new ResponseEntity(centralOrderList,
				httpHeaders, HttpStatus.OK);

		when(configurationService.isCoActive()).thenReturn(true);
		when(configurationService.getCoSourceSystemId()).thenReturn(sourceSystemId);
		when(userService.getCurrentUser()).thenReturn(customerModel);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(centralOrderService.getCentalOrderList(customerModel, baseStoreModel, orderStatus, pageableData, sourceSystemId))
				.thenReturn(centralOrderListResponse);

		orderListSortMap.put("firstkey", "firstvalue");
		orderListSortMap.put("secondkey", "secondvalue");

		defaultCentralOrderFacade.setOrderListSortMap(orderListSortMap);

		final SearchPageData<OrderHistoryData> output = defaultCentralOrderFacade.getPagedOrderHistoryForStatuses(pageableData,
				status);

		Assert.assertEquals(1, output.getPagination().getNumberOfPages());
		Assert.assertTrue(output.getSorts().get(0).isSelected());
		Assert.assertFalse(output.getSorts().get(1).isSelected());
		Assert.assertNotNull(output.getResults().get(0));
	}

}
