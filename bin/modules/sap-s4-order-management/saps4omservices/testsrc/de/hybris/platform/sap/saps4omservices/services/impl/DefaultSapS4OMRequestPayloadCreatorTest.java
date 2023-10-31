/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMRequestPayloadCreatorTest {

	private static final String REQUESTED_DELIVERY_DATE = "9999-12-31";

	@Spy
	@InjectMocks
	DefaultSapS4OMRequestPayloadCreator sapS4OMRequestPayloadCreator;

	@Mock
	SapS4OrderUtil sapS4OrderUtil;
	
	@Mock
	UserService userService;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	BaseStoreService baseStoreService;
	
	@Mock
	CommonI18NService commonI18NService;
	
	@Mock
	DeliveryService deliveryService;
	
	@Before
	public void init() {
		sapS4OMRequestPayloadCreator.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
                when(sapS4OMRequestPayloadCreator.getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled()).thenReturn(true);
	}
	
	
	
	@Test
	public void testGetPayloadForOrderCreation() {
		
		OrderModel order = spy(OrderModel.class);
		B2BCustomerModel customer = spy(B2BCustomerModel.class);
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		CurrencyModel currency = spy(CurrencyModel.class);
		AbstractOrderEntryModel entry = spy(AbstractOrderEntryModel.class);
		ProductModel product = spy(ProductModel.class);
		WarehouseModel plant = spy(WarehouseModel.class);
		DeliveryModeModel deliveryMode = spy(DeliveryModeModel.class);
		
		Locale locale = Locale.ENGLISH;
		LanguageModel language = spy(LanguageModel.class);
		when(language.getIsocode()).thenReturn("EN");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		when(commonI18NService.getLocaleForIsoCode("EN")).thenReturn(locale);
				
		when(order.getUser()).thenReturn(customer);
		when(sapS4OrderUtil.getSoldToParty(customer)).thenReturn("1234");
		when(order.getStore()).thenReturn(baseStore);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		when(sapConfig.getSapcommon_transactionType()).thenReturn("OR");
		when(sapConfig.getSapcommon_salesOrganization()).thenReturn("1000");
		when(sapConfig.getSapcommon_distributionChannel()).thenReturn("10");
		when(sapConfig.getSapcommon_division()).thenReturn("00");
		when(sapConfig.getContactPersonPartnerFunctionCode(locale)).thenReturn("CP");
		when(sapConfig.getShipToPartnerFunctionCode(locale)).thenReturn("SH");
		when(sapConfig.getBillToPartnerFunctionCode(locale)).thenReturn("BP");
		when(order.getCurrency()).thenReturn(currency);
		when(currency.getSapCode()).thenReturn("USD");
		lenient().when(order.getCode()).thenReturn("");
		when(order.getEntries()).thenReturn(Collections.singletonList(entry));
		when(entry.getProduct()).thenReturn(product);
		when(product.getSapProductTypes()).thenReturn(Collections.singleton(SAPProductType.PHYSICAL));
		when(entry.getEntryNumber()).thenReturn(0);
		when(product.getCode()).thenReturn("PROD-01");
		when(entry.getQuantity()).thenReturn(0L);
		when(product.getSapPlant()).thenReturn(plant);
		when(plant.getCode()).thenReturn("1000");
		when(order.getDeliveryMode()).thenReturn(deliveryMode);
		when(sapConfig.getSaps4deliverycostconditiontype()).thenReturn("DC00");
		when(order.getDeliveryCost()).thenReturn(10.0);
		when(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(any(BaseStoreModel.class))).thenReturn(true);
		when(order.getRequestedRetrievalDate()).thenReturn(REQUESTED_DELIVERY_DATE);
		
		when(customer.getCustomerID()).thenReturn("123");
		
		SAPS4OMRequestData request = sapS4OMRequestPayloadCreator.getPayloadForOrderCreation(order);
		
		assertEquals(1,request.getItems().size());
		assertEquals(1,request.getPricingElements().size());
		assertEquals(3,request.getPartner().size());
		assertNotNull(request.getRequestedDeliveryDate());
		
	}
	
	@Test
	public void testGetPayloadForOrderSimulationForCartModel() { 
				
		AbstractOrderModel abstractOrderModel = spy(AbstractOrderModel.class);
		B2BCustomerModel customer = spy(B2BCustomerModel.class);

		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		CurrencyModel currency = spy(CurrencyModel.class);
		ProductModel productModel = spy(ProductModel.class);
		AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setProduct(productModel);
		entry.setEntryNumber(0);
		
		Locale locale = Locale.ENGLISH;
		LanguageModel language = spy(LanguageModel.class);
		when(language.getIsocode()).thenReturn("EN");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		when(commonI18NService.getLocaleForIsoCode("EN")).thenReturn(locale);
		when(abstractOrderModel.getCurrency()).thenReturn(currency);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		when(abstractOrderModel.getStore()).thenReturn(baseStore);
		when(abstractOrderModel.getUser()).thenReturn(customer);
		when(sapConfig.getSapcommon_transactionType()).thenReturn("OR");
		when(sapConfig.getSapcommon_salesOrganization()).thenReturn("1000");
		when(sapConfig.getSapcommon_distributionChannel()).thenReturn("10");
		when(sapConfig.getSapcommon_division()).thenReturn("00");
		when(currency.getSapCode()).thenReturn("USD");
		when(customer.getCustomerID()).thenReturn("1234");
		when(abstractOrderModel.getEntries()).thenReturn(Collections.singletonList(entry));
		when(productModel.getSapProductTypes()).thenReturn(Collections.singleton(SAPProductType.PHYSICAL));
		when(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(any(BaseStoreModel.class))).thenReturn(true);
		when(abstractOrderModel.getRequestedRetrievalDate()).thenReturn(REQUESTED_DELIVERY_DATE);
		
		SAPS4OMRequestData request = sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(abstractOrderModel);
		assertEquals(1,request.getItems().size());
		assertNotNull(request.getRequestedDeliveryDate());
	}
	
	@Test
	public void testGetPayloadForOrderSimulation() { 
		ProductModel productModel =   spy(ProductModel.class);
		List<ProductModel> productModelList = new ArrayList<>();
		productModelList.add(productModel);
		
		B2BCustomerModel customer = spy(B2BCustomerModel.class);
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		
		Locale locale = Locale.ENGLISH;
		LanguageModel language = spy(LanguageModel.class);
		when(language.getIsocode()).thenReturn("EN");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		when(commonI18NService.getLocaleForIsoCode("EN")).thenReturn(locale);
		
		CurrencyModel currency = spy(CurrencyModel.class);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		when(sapConfig.getSapcommon_transactionType()).thenReturn("OR");
		when(sapConfig.getSapcommon_salesOrganization()).thenReturn("1000");
		when(sapConfig.getSapcommon_distributionChannel()).thenReturn("10");
		when(sapConfig.getSapcommon_division()).thenReturn("00");
		when(sapS4OrderUtil.getSoldToParty(customer)).thenReturn("1234");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
		when(commonI18NService.getCurrentCurrency().getSapCode()).thenReturn("USD");
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getCustomerID()).thenReturn("1234");

		SAPS4OMRequestData request = sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(productModelList, false);
		
		assertEquals(2,request.getItems().size());
	}
	
	@Test
	public void testGetPayloadForStockAvailability() { 
		ProductModel productModel =   spy(ProductModel.class);
		List<ProductModel> productModelList = new ArrayList<>();
		productModelList.add(productModel);
		
		B2BCustomerModel customer = spy(B2BCustomerModel.class);
		
		Locale locale = Locale.ENGLISH;
		LanguageModel language = spy(LanguageModel.class);
		when(language.getIsocode()).thenReturn("EN");
		when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		when(commonI18NService.getLocaleForIsoCode("EN")).thenReturn(locale);
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		
		CurrencyModel currency = spy(CurrencyModel.class);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		when(sapConfig.getSapcommon_transactionType()).thenReturn("OR");
		when(sapConfig.getSapcommon_salesOrganization()).thenReturn("1000");
		when(sapConfig.getSapcommon_distributionChannel()).thenReturn("10");
		when(sapConfig.getSapcommon_division()).thenReturn("00");
		when(sapS4OrderUtil.getSoldToParty(customer)).thenReturn("1234");
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
		when(commonI18NService.getCurrentCurrency().getSapCode()).thenReturn("USD");
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getCustomerID()).thenReturn("1234");
		SAPS4OMRequestData request = sapS4OMRequestPayloadCreator.getPayloadForOrderSimulation(productModelList, true);

		assertEquals("1", request.getItems().get(0).getRequestedQuantity());
		assertEquals("9999999", request.getItems().get(1).getRequestedQuantity());
	}

}
