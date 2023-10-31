/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilder;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.impl.DefaultSapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrderResult;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrders;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOrderServiceTest {
	
	private static final String ORDER_CODE = "123";
	@Spy
	@InjectMocks
	private DefaultSapS4OMOrderService defaultSapS4OMOrderService;
	@Mock
	private DefaultSapS4OMOutboundService defaultSapS4OMOutboundService;
	@Mock
	private Converter<SAPS4OMResponseData, OrderModel> sapS4OrderConverter;
	@Mock
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	@Mock
	private CustomerAccountDao customerAccountDao;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder;
	@Mock
	private SapS4OrderUtil sapS4OrderUtil;
	@Mock
	private ModelService modelService;
	
	private static final String DIVISION = "00";
	private static final String DIST_CHANNEL = "10";
	private static final String SALES_ORGANIZATION = "1710";
	private static final String SOLD_TO_PARTY = "17100005";
	private static final String SOLD_TO_PARTY_INCORRECT = "17100003";
	private static final String DETAULT_SORT= "byDate";
	private static final String DETAULT_SORT_S4OM= "SalesOrderDate";
	private PageableData pageableData;
	private SAPConfigurationModel sapConfigModel;
	
	@Before
	public void setUp() {
		pageableData = new PageableData();
		pageableData.setSort(DETAULT_SORT);
		pageableData.setPageSize(1);
		final Map<String, String> orderListSortMap = new HashMap<>();
		orderListSortMap.put(DETAULT_SORT,DETAULT_SORT_S4OM);
		defaultSapS4OMOrderService.setOrderListSortMap(orderListSortMap);
		
		sapConfigModel = new SAPConfigurationModel();
		sapConfigModel.setSapcommon_salesOrganization(SALES_ORGANIZATION);
		sapConfigModel.setSapcommon_distributionChannel(DIST_CHANNEL);
		sapConfigModel.setSapcommon_division(DIVISION);
	}
	
	@Test
	public void testGetOrderForCodeWithCustomerSuperCall() {
		final OrderModel orderModelTest = spy(OrderModel.class);
		orderModel.setCode(null);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);
		when(customerAccountDao.findOrderByCustomerAndCodeAndStore(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModelTest);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderForCodeWithCustomerSyncOrderEnabled() {
		final SAPS4OMData sapS4OMData = createSAPS4OMData();
		
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenReturn(sapS4OMData);
		given(modelService.create(OrderModel.class)).willReturn(orderModel);
		when(sapS4OrderConverter.convert(sapS4OMData.getResult(),orderModel)).thenReturn(orderModel);
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigModel);
		when(sapS4OrderUtil.getSoldToParty(customerModel)).thenReturn(SOLD_TO_PARTY);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderForCodeWithCustomerSyncOrderHistoryEnabled() {
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenReturn(createSAPS4OMData());
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigModel);
		when(sapS4OrderUtil.getSoldToParty(customerModel)).thenReturn(SOLD_TO_PARTY_INCORRECT);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		assertNull(result);
	}
	
	@Test
	public void testGetOrderForCodeSuperCall() {
		final OrderModel orderModels = spy(OrderModel.class);
		orderModel.setCode(null);
		final String code = "123";
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);
		when(customerAccountDao.findOrderByCodeAndStore(code, baseStoreModel)).thenReturn(orderModels);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(code, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderForCodeSyncOrderEnabled() {
		final SAPS4OMData sapS4OMDataTest = new SAPS4OMData();
		final SAPS4OMResponseData responseData = new SAPS4OMResponseData();
		sapS4OMDataTest.setResult(responseData);
		final String code = "123";
		given(modelService.create(OrderModel.class)).willReturn(orderModel);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenReturn(sapS4OMDataTest);
		when(sapS4OrderConverter.convert(responseData, orderModel)).thenReturn(orderModel);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(code, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderForCodeSyncOrderHistoryEnabled() {
		final SAPS4OMData sapS4OMDataTest = new SAPS4OMData();
		final SAPS4OMResponseData responseData = new SAPS4OMResponseData();
		sapS4OMDataTest.setResult(responseData);
		final String code = "123";
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenReturn(sapS4OMDataTest);
		given(modelService.create(OrderModel.class)).willReturn(orderModel);
		when(sapS4OrderConverter.convert(responseData, orderModel)).thenReturn(orderModel);
		
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(code, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderList() {
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		lenient().when(defaultSapS4OMOutboundService.fetchOrders(anyString(), anyString(),any())).thenReturn(createSAPS4OMOrders());
		given(modelService.create(OrderModel.class)).willReturn(new OrderModel());
		final SearchPageData<OrderModel> result = defaultSapS4OMOrderService.getOrderList(customerModel, baseStoreModel, null,pageableData);
		assertFalse(result.getResults().isEmpty());
		assertEquals(1,result.getPagination().getPageSize());
		assertEquals(DETAULT_SORT,result.getPagination().getSort());
	}
	
	@Test
	public void testGetOrderListWithoutSort() {
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		given(modelService.create(OrderModel.class)).willReturn(new OrderModel());
		lenient().when(defaultSapS4OMOutboundService.fetchOrders(anyString(), anyString(),any())).thenReturn(createSAPS4OMOrders());
		pageableData.setSort(null);
		final SearchPageData<OrderModel> result = defaultSapS4OMOrderService.getOrderList(customerModel, baseStoreModel, null,pageableData);
		assertFalse(result.getResults().isEmpty());
	}
	
	@Test
	public void testGetOrderListSuperCall() {
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(false);
		given(customerAccountDao.findOrdersByCustomerAndStore(customerModel, baseStoreModel, null, pageableData)).willReturn(null);
		final SearchPageData<OrderModel> result = defaultSapS4OMOrderService.getOrderList(customerModel,baseStoreModel,null,pageableData);
		assertNull(result);
	}
	
	@Test
	public void testGetEmptyOrderList() {
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		final SearchPageData<OrderModel> result = defaultSapS4OMOrderService.getOrderList(customerModel, baseStoreModel, null,pageableData);
		assertTrue(result.getResults().isEmpty());
	}
	
	@Test
	public void testVerifyCustomerOrder() {
		final String code = "123";
		given(baseStoreModel.getSAPConfiguration()).willReturn(sapConfigModel);
		given(sapS4OrderUtil.getSoldToParty(customerModel)).willReturn(SOLD_TO_PARTY);
		
		final boolean result = defaultSapS4OMOrderService.verifyCustomerOrder(customerModel, baseStoreModel, createSAPS4OMData().getResult(), code);
		assertTrue(result);
	}
	
	@Test
	public void testVerifyCustomerOrderIsFalse() {
		final SAPS4OMResponseData responseData = new SAPS4OMResponseData();
		responseData.setSoldToParty(null);
		responseData.setSalesOrganization(null);
		responseData.setDistributionChannel(null);
		responseData.setDivision(null);
		final String code = "123";
		given(baseStoreModel.getSAPConfiguration()).willReturn(sapConfigModel);
		given(sapS4OrderUtil.getSoldToParty(customerModel)).willReturn(SOLD_TO_PARTY);
		
		final boolean result = defaultSapS4OMOrderService.verifyCustomerOrder(customerModel, baseStoreModel, responseData, code);
		assertFalse(result);
	}
	
	@Test
	public void testFetchOrderDetailsFromBackend() {
		final String code = "123";
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenReturn(createSAPS4OMData());
		
		final SAPS4OMData result = defaultSapS4OMOrderService.fetchOrderDetailsFromBackend(code);
		assertNotNull(result);
	}
	
	@Test
	public void testFetchOrderDetailsFromSuperWhenSynchronousOrderIsDisabled() {
		final OrderModel orderModelTest = spy(OrderModel.class);
		orderModel.setCode(null);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(customerAccountDao.findOrderByCustomerAndCodeAndStore(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModelTest);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenThrow(OutboundServiceException.class);
		final OrderModel result = defaultSapS4OMOrderService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);			
		assertNotNull(result);
	}
	
	@Test
	public void testFetchOrderDetailsFromBackendHandleException() {
	
		orderModel.setCode(null);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(defaultSapS4OMOutboundService.fetchOrderDetails(anyString(), anyString(), anyString(), anyMap())).thenThrow(new OutboundServiceException("error"));
		try {
			 defaultSapS4OMOrderService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);			
		} catch (UnknownIdentifierException e) {
			assertNotNull(e.getMessage());
		}
	}
	
	private SAPS4OMOrders createSAPS4OMOrders() {
		final SAPS4OMOrders sapS4OMOrders = new SAPS4OMOrders();
		final SAPS4OMOrderResult  sapS4OMOrderResult = new SAPS4OMOrderResult();
		sapS4OMOrderResult.setOrderCount("1");
		SAPS4OMResponseData sapS4OMResponseData = new SAPS4OMResponseData();
		sapS4OMOrderResult.setResults(Arrays.asList(sapS4OMResponseData)); 
		sapS4OMOrders.setSapS4OMOrderResult(sapS4OMOrderResult);
		return sapS4OMOrders;
	}
	
	private SAPS4OMData createSAPS4OMData() {
		final SAPS4OMData sapS4OMData = new SAPS4OMData();
		final SAPS4OMResponseData responseData = new SAPS4OMResponseData();
		responseData.setSoldToParty(SOLD_TO_PARTY);
		responseData.setSalesOrganization(SALES_ORGANIZATION);
		responseData.setDistributionChannel(DIST_CHANNEL);
		responseData.setDivision(DIVISION);
		sapS4OMData.setResult(responseData);
		return sapS4OMData;
	}

}
