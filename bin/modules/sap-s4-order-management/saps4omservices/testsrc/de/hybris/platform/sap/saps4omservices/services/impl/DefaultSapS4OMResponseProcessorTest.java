/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.sap.saps4omservices.enums.S4ProceduresSubtotal;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.saps4omservices.dto.PricingElementData;
import de.hybris.platform.saps4omservices.dto.PricingElementsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.saps4omservices.dto.ScheduleLineData;
import de.hybris.platform.saps4omservices.dto.ScheduleLinesData;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMResponseProcessorTest {
	
	@Spy
	@InjectMocks
	DefaultSapS4OMResponseProcessor defaultSapS4OMResponseProcessor;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	SessionService sessionService;
	
	private static final String SALES_REQUESTED_DELIVERY_DATE = "/Date(1646611200000)/";
	
	SAPS4OMData response;
	
	@Before
	public void init() {
		
		SAPS4OMResponseData result = new SAPS4OMResponseData();
		result.setSalesOrder("1234");
		response = new SAPS4OMData();
		response.setResult(result);
	}
	
	@Test
	public void testProcessOrderCreationResponse() {	
		OrderModel orderModel = spy(OrderModel.class);
		when(orderModel.getEntries()).thenReturn(Collections.emptyList());
		defaultSapS4OMResponseProcessor.processOrderCreationResponse(response, orderModel);
		verify(orderModel).setConsignments(anySet());
		verify(orderModel).setSapOrders(anySet());

		assertEquals(orderModel.getCode(), response.getResult().getSalesOrder());
	}
	
	@Test
	public void testProcessOrderSimulationResponse() {
		SAPS4OMData response = spy(SAPS4OMData.class);
		ItemModel itemModel = spy(ItemModel.class);
		SAPS4OMResponseData sapS4OMResponseData = spy(SAPS4OMResponseData.class);
		SAPS4OMItemsData sapS4OMItemsData = spy(SAPS4OMItemsData.class);
		SAPS4OMItemData sapS4OMItemData = spy(SAPS4OMItemData.class);
		
		BaseStoreService baseStoreService = spy(BaseStoreService.class);
		BaseStoreModel baseStoreModel = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfigurationModel = spy(SAPConfigurationModel.class);		
		

		when(response.getResult()).thenReturn(sapS4OMResponseData);
		when(response.getResult().getItems()).thenReturn(sapS4OMItemsData);
		when(response.getResult().getItems().getSalesOrderItems()).thenReturn(Collections.singletonList(sapS4OMItemData));

		when(sapS4OMItemData.getRequestedQuantity()).thenReturn("1");
		when(sapS4OMItemData.getTransactionCurrency()).thenReturn("USD");
		when(response.getResult().getSalesOrganization()).thenReturn("1710");
		when(response.getResult().getDistributionChannel()).thenReturn("00");
		when(response.getResult().getDivision()).thenReturn("10");
		
		when(defaultSapS4OMResponseProcessor.getBaseStoreService()).thenReturn(baseStoreService);
		when(defaultSapS4OMResponseProcessor.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);
		
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		
		Map<String, Object> result = defaultSapS4OMResponseProcessor.processOrderSimulationResponse(response, itemModel);
		
		assertTrue(result.size() > 1);
		
		
	}
	
	@Test
	public void testProcessOrderSimulationResponseForCartModel() {
		SAPS4OMData response = spy(SAPS4OMData.class);
		AbstractOrderModel itemModel = spy(CartModel.class);
		SAPS4OMResponseData sapS4OMResponseData = spy(SAPS4OMResponseData.class);
		SAPS4OMItemsData sapS4OMItemsData = spy(SAPS4OMItemsData.class);
		SAPS4OMItemData sapS4OMItemData = spy(SAPS4OMItemData.class);
		ScheduleLinesData scheduleLinesData = spy(ScheduleLinesData.class);
		ScheduleLineData scheduleLineData = spy(ScheduleLineData.class);
		Session session = spy(Session.class);
		
		BaseStoreService baseStoreService = spy(BaseStoreService.class);
		BaseStoreModel baseStoreModel = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfigurationModel = spy(SAPConfigurationModel.class);		
		
		SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = spy(SAPPlantLogSysOrgModel.class);
		SAPSalesOrganizationModel sapSalesOrganization = spy(SAPSalesOrganizationModel.class);
		PricingElementsData pricingElementsData = spy(PricingElementsData.class);
		PricingElementData pricingElementData = spy(PricingElementData.class);
		AbstractOrderEntryModel entry = spy(AbstractOrderEntryModel.class);
		ProductModel productModel = spy(ProductModel.class);
		DeliveryModeModel deliveryModeModel = spy(DeliveryModeModel.class);
		
		when(response.getResult()).thenReturn(sapS4OMResponseData);
		when(response.getResult().getItems()).thenReturn(sapS4OMItemsData);
		when(response.getResult().getItems().getSalesOrderItems()).thenReturn(Collections.singletonList(sapS4OMItemData));
		when(sapS4OMResponseData.getRequestedDeliveryDate()).thenReturn(SALES_REQUESTED_DELIVERY_DATE);
		when(sapS4OMItemData.getScheduleLines()).thenReturn(scheduleLinesData);
		when(sapS4OMItemData.getRequestedQuantity()).thenReturn("1");
		when(sapS4OMItemData.getTransactionCurrency()).thenReturn("USD");
		when(sapS4OMItemData.getPricingElements()).thenReturn(pricingElementsData);
		when(response.getResult().getSalesOrganization()).thenReturn("1710");
		when(response.getResult().getDistributionChannel()).thenReturn("00");
		when(response.getResult().getDivision()).thenReturn("10");
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(baseStoreModel.getSAPConfiguration().getSaps4om_taxessub()).thenReturn(S4ProceduresSubtotal.TAXAMOUNT);
		when(baseStoreModel.getSAPConfiguration().getSaps4om_pricesub()).thenReturn(S4ProceduresSubtotal.NETAMOUNT);
		when(baseStoreModel.getSAPConfiguration().getSaps4om_paymentsub()).thenReturn(S4ProceduresSubtotal.SUBTOTAL1AMOUNT);
		when(baseStoreModel.getSAPConfiguration().getSaps4om_paymentsub()).thenReturn(S4ProceduresSubtotal.SUBTOTAL2AMOUNT);
		when(baseStoreModel.getSAPConfiguration().getSaps4om_deliverysub()).thenReturn(S4ProceduresSubtotal.SUBTOTAL5AMOUNT);
		when(itemModel.getStore()).thenReturn(baseStoreModel);
		when(itemModel.getStore().getSAPConfiguration().getSaps4itempriceconditiontype()).thenReturn("SP");
		when(itemModel.getDeliveryMode()).thenReturn(deliveryModeModel);
		
		when(pricingElementData.getConditionType()).thenReturn("PS");
		when(pricingElementData.getConditionAmount()).thenReturn("-1");
		when(pricingElementData.getConditionRateValue()).thenReturn("10");
		when(pricingElementData.getConditionCalculationType()).thenReturn("PS");
		when(defaultSapS4OMResponseProcessor.getBaseStoreService()).thenReturn(baseStoreService);
		when(defaultSapS4OMResponseProcessor.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(itemModel.getEntries()).thenReturn(Collections.singletonList(entry));
		when(entry.getEntryNumber()).thenReturn(1);
		when(entry.getProduct()).thenReturn(productModel);
		when(entry.getProduct().getCode()).thenReturn("TG11");
		
		when(scheduleLinesData.getSalesOrderScheduleLines()).thenReturn(Collections.singletonList(scheduleLineData));
        		when(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()).thenReturn("1");
        when(scheduleLineData.getConfirmedDeliveryDate()).thenReturn("30/05/2050 02:34:56");

        when(sapPlantLogSysOrgModel.getSalesOrg()).thenReturn(sapSalesOrganization);
        when(sapPlantLogSysOrgModel.getSalesOrg().getSalesOrganization()).thenReturn("1710");
        when(defaultSapS4OMResponseProcessor.getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()
        		.getSapPlantLogSysOrg()).thenReturn(Collections.singleton(sapPlantLogSysOrgModel));
        when(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(Mockito.any(BaseStoreModel.class))).thenReturn(true);
        when(sessionService.getCurrentSession()).thenReturn(session);

        when(pricingElementsData.getSalesOrderPricingElements()).thenReturn(Collections.singletonList(pricingElementData));
        

		Map<String, Object> result = defaultSapS4OMResponseProcessor.processOrderSimulationResponse(response, itemModel);
		assertTrue(result.size() > 1);
		verify(session, times(1)).setAttribute(Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testProcessOrderSimulationResponseForScheduleLines() {
		SAPS4OMData response = spy(SAPS4OMData.class);
		ItemModel itemModel = spy(ItemModel.class);
		SAPS4OMResponseData sapS4OMResponseData = spy(SAPS4OMResponseData.class);
		SAPS4OMItemsData sapS4OMItemsData = spy(SAPS4OMItemsData.class);
		SAPS4OMItemData sapS4OMItemData = spy(SAPS4OMItemData.class);
		ScheduleLinesData scheduleLinesData = spy(ScheduleLinesData.class);
		ScheduleLineData scheduleLineData = spy(ScheduleLineData.class);
		BaseStoreModel baseStoreModel = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfigurationModel = spy(SAPConfigurationModel.class);		
		BaseStoreService baseStoreService = spy(BaseStoreService.class);
		SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = spy(SAPPlantLogSysOrgModel.class);
		SAPSalesOrganizationModel sapSalesOrganization = spy(SAPSalesOrganizationModel.class);

		when(response.getResult()).thenReturn(sapS4OMResponseData);
		when(response.getResult().getItems()).thenReturn(sapS4OMItemsData);
		when(response.getResult().getItems().getSalesOrderItems()).thenReturn(Collections.singletonList(sapS4OMItemData));
		when(response.getResult().getSalesOrganization()).thenReturn("1710");
		when(response.getResult().getDistributionChannel()).thenReturn("00");
		when(response.getResult().getDivision()).thenReturn("10");
		when(sapS4OMItemData.getRequestedQuantity()).thenReturn("9999999");
		
		when(sapS4OMItemData.getScheduleLines()).thenReturn(scheduleLinesData);
		when(scheduleLinesData.getSalesOrderScheduleLines()).thenReturn(Collections.singletonList(scheduleLineData));
		when(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()).thenReturn("1");
		when(scheduleLineData.getConfirmedDeliveryDate()).thenReturn("30/05/2050 02:34:56");
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);

		when(defaultSapS4OMResponseProcessor.getBaseStoreService()).thenReturn(baseStoreService);
		when(defaultSapS4OMResponseProcessor.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(sapPlantLogSysOrgModel.getSalesOrg()).thenReturn(sapSalesOrganization);
		when(sapPlantLogSysOrgModel.getSalesOrg().getSalesOrganization()).thenReturn("1710");
		when(defaultSapS4OMResponseProcessor.getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()
		.getSapPlantLogSysOrg()).thenReturn(Collections.singleton(sapPlantLogSysOrgModel));
		
		Map<String, Object> result = defaultSapS4OMResponseProcessor.processOrderSimulationResponse(response, itemModel);
		assertTrue(result.size() > 1);
	}
	
	
	@Test
	public void testProcessOrderSimulationResponseForNullResponseBody() { 
		AbstractOrderModel itemModel = spy(AbstractOrderModel.class);

		Map<String, Object> result = defaultSapS4OMResponseProcessor.processOrderSimulationResponse(null, itemModel);
		assertEquals(0, result.size());
	}
	

}
