/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.SapS4OMProductAvailabilityCache;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMRequestPayloadCreator;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMResponseProcessor;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.CreditData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.PriceValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4SalesOrderSimulationServiceTest {
	
	
	@Spy
	@InjectMocks
	DefaultSapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	
	@Mock
	SapS4OMRequestPayloadCreator sapS4OMRequestPayloadCreator;
	
	@Mock
	SapS4OMOutboundService sapS4OMOutboundService;
	
	@Mock
	SapS4OMResponseProcessor sapS4OMResponseProcessor;
	
	@Mock
	UserService userService;
	
	@Mock
	BaseStoreService baseStoreService;
	
	@Mock
	SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache;

	@Before
	public void init() {
		sapS4SalesOrderSimulationService.setSapS4OMRequestPayloadCreator(sapS4OMRequestPayloadCreator);
		sapS4SalesOrderSimulationService.setSapS4OMOutboundService(sapS4OMOutboundService);
		sapS4SalesOrderSimulationService.setSapS4OMResponseProcessor(sapS4OMResponseProcessor);
		sapS4SalesOrderSimulationService.setUserService(userService);
		sapS4SalesOrderSimulationService.setBaseStoreService(baseStoreService);
	}

	@Test
	public void testgetPriceDetailsForProduct() {
		ProductModel productModel = spy(ProductModel.class);
		WarehouseModel warehouseModel = spy(WarehouseModel.class);
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		final SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);

		when(productModel.getSapPlant()).thenReturn(warehouseModel);
		when(productModel.getCode()).thenReturn("MATERIAL101");
		when(productModel.getSapPlant().getCode()).thenReturn("defaultPlant");

		List<ProductModel> productModelList = new ArrayList<>();
		
		productModelList.add(productModel);
		
		SAPS4OMRequestData requestData = spy(SAPS4OMRequestData.class);
		SAPS4OMData responseData = spy(SAPS4OMData.class);
		
		PriceValue priceValue = new PriceValue("USD", 10, false);
		PriceInformation priceInformation = new PriceInformation(priceValue);
		
		List<PriceInformation> priceInfoList = new ArrayList<>();
		priceInfoList.add(priceInformation);
		
		Map<String, List<PriceInformation>> productPriceMap = new HashMap<>();
		productPriceMap.put("MATERIAL101", priceInfoList);
		SapS4OMProductAvailability sapS4OMProductAvailability = spy(SapS4OMProductAvailability.class);
		Map<String, SapS4OMProductAvailability> stockLevelMap = new HashMap<>();
		stockLevelMap.put("plant", sapS4OMProductAvailability);
		
		Map<String, Object> simulationDetailsMp = new HashMap<>();
		simulationDetailsMp.put("priceInfoMap", productPriceMap);
		simulationDetailsMp.put("sapStockAvailabilityMap", stockLevelMap);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(productModelList, false)).thenReturn(requestData);
		when(sapS4OMOutboundService.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData)).thenReturn(responseData);	
		when(sapS4OMResponseProcessor.processOrderSimulationResponse(responseData, productModel)).thenReturn(simulationDetailsMp);

		List<PriceInformation> priceResult = sapS4SalesOrderSimulationService.getPriceDetailsForProduct(productModel);
		
		assertEquals(1, priceResult.size());
		
	}
	
	@Test
	public void testgetPriceDetailsForMultipleProducts() {
		ProductModel productModel = spy(ProductModel.class);

		List<ProductModel> productModelList = new ArrayList<>();
		
		productModelList.add(productModel);
		
		SAPS4OMRequestData requestData = spy(SAPS4OMRequestData.class);
		SAPS4OMData responseData = spy(SAPS4OMData.class);
		
		PriceValue priceValue = new PriceValue("USD", 10, false);
		PriceInformation priceInformation = new PriceInformation(priceValue);
		
		List<PriceInformation> priceInfoList = new ArrayList<>();
		priceInfoList.add(priceInformation);
		
		Map<String, List<PriceInformation>> productPriceMap = new HashMap<>();
		productPriceMap.put("MATERIAL101", priceInfoList);
		
		Map<String, Object> simulationDetailsMp = new HashMap<>();
		simulationDetailsMp.put("priceInfoMap", productPriceMap);
		
		when(sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(productModelList, false)).thenReturn(requestData);
		when(sapS4OMOutboundService.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData)).thenReturn(responseData);	
		when(sapS4OMResponseProcessor.processOrderSimulationResponse(responseData, null)).thenReturn(simulationDetailsMp);

		Map<String, List<PriceInformation>> priceResultMultipleProducts = sapS4SalesOrderSimulationService.getPriceDetailsForProducts(productModelList);
		assertEquals(1, priceResultMultipleProducts.size());
	}
	
	@Test
	public void testSetCartDetails() {
		
		AbstractOrderModel abstractOrderModel = spy(AbstractOrderModel.class);
		AbstractOrderEntryModel abstractOrderEntryModel = spy(AbstractOrderEntryModel.class);
		List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
		listAbstractOrderEntryModel.add(abstractOrderEntryModel);
		
		when(abstractOrderModel.getEntries()).thenReturn(listAbstractOrderEntryModel);
		
		SAPS4OMRequestData requestData = spy(SAPS4OMRequestData.class);
		SAPS4OMData responseData = spy(SAPS4OMData.class);

		when(sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(abstractOrderModel)).thenReturn(requestData);
		when(sapS4OMOutboundService.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData)).thenReturn(responseData);	
		sapS4SalesOrderSimulationService.setCartDetails(abstractOrderModel);
		
		assertEquals(1, abstractOrderModel.getEntries().size());
	}
	
	@Test
	public void testCheckCreditLimitExceeded() {
		
		UserModel userModel = spy(UserModel.class);
		AbstractOrderModel itemModel = spy(AbstractOrderModel.class);
		SAPS4OMRequestData requestData = spy(SAPS4OMRequestData.class);
		
		SAPS4OMData responseData = spy(SAPS4OMData.class);
		SAPS4OMResponseData sapS4OMResponseData = spy(SAPS4OMResponseData.class);
		CreditData creditData = spy(CreditData.class);
		
		when(sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(itemModel)).thenReturn(requestData);

		when(sapS4OMOutboundService.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData)).thenReturn(responseData);	
		when(responseData.getResult()).thenReturn(sapS4OMResponseData);
		when(responseData.getResult().getCreditDetails()).thenReturn(creditData);
		when(responseData.getResult().getCreditDetails().getCreditCheckStatus()).thenReturn("B");
		boolean result = sapS4SalesOrderSimulationService.checkCreditLimitExceeded(itemModel, userModel);
	
		assertTrue(result);
	}
	
	@Test
	public void testGetStockAvailability() {
		ProductModel productModel = spy(ProductModel.class);
		WarehouseModel warehouseModel = spy(WarehouseModel.class);
		when(productModel.getSapPlant()).thenReturn(warehouseModel);
		when(productModel.getSapPlant().getCode()).thenReturn("plant");
		
		List<ProductModel> productModelList = new ArrayList<>();
		productModelList.add(productModel);
		
		SAPS4OMRequestData requestData = spy(SAPS4OMRequestData.class);
		SAPS4OMData responseData = spy(SAPS4OMData.class);
		Map<String, SapS4OMProductAvailability> stockLevelMap = new HashMap<>();
		
		SapS4OMProductAvailability sapS4OMProductAvailability = spy(SapS4OMProductAvailability.class);
		stockLevelMap.put("plant", sapS4OMProductAvailability);
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("sapStockAvailabilityMap", stockLevelMap);
		
		when(sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(productModelList, true)).thenReturn(requestData);
		when(sapS4OMOutboundService.simulateOrder(SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION, SapS4OrderUtil.S4_ORDER_SIMULATE_DESTINATION_TARGET, requestData)).thenReturn(responseData);	
		when(sapS4OMResponseProcessor.processOrderSimulationResponse(responseData, productModel)).thenReturn(responseMap);

		SapS4OMProductAvailability result = sapS4SalesOrderSimulationService.getStockAvailability(productModel, null);
		
		assertNotNull(result);
		
	}
		

}