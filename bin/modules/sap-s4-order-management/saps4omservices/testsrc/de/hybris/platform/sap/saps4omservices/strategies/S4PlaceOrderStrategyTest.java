/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMRequestPayloadCreator;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMResponseProcessor;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class S4PlaceOrderStrategyTest {

	@Spy
	@InjectMocks
	S4PlaceOrderStrategy s4PlaceOrderStrategy;

	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;

	@Mock
	SapS4OMOutboundService sapS4omOutboundService;

	@Mock
	SapS4OMRequestPayloadCreator sapS4OMRequestPayloadCreator;

	@Mock
	SapS4OMResponseProcessor sapS4OMResponseProcessor;

	@Mock
	CalculationService calculationService;

	@Mock
	OrderService orderService;

	@Mock
	TimeService timeService;

	@Mock
	CommonI18NService commonI18NService;
	PaymentService paymentService;
	
	@Mock
	BaseSiteService baseSiteService;
	
	@Mock
	BaseStoreService baseStoreService;
	
	@Mock
	PromotionsService promotionsService;
	
	@Mock
	ExternalTaxesService externalTaxesService;
	
	@Mock
	ModelService modelService;
	
	@Mock
	CartService cartService;
	
	@Mock
	SAPS4OMAvailabilityService sapS4OMAvailabilityService;
	
	@Test
	public void testPlaceOrder() throws InvalidCartException {

		CommerceCheckoutParameter parameter = spy(CommerceCheckoutParameter.class);
		CartModel cart = spy(CartModel.class);
		B2BCustomerModel customerModel = spy(B2BCustomerModel.class);
		parameter.setCart(cart);
		cart.setUser(customerModel);
		OrderModel order = spy(OrderModel.class);
		order.setCode("1234");
		
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		AbstractOrderEntryModel entry = spy(OrderEntryModel.class);
		ProductModel product = spy(ProductModel.class);
		when(entry.getProduct()).thenReturn(product);
		entries.add(entry);
		order.setEntries(entries);

		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(orderService.createOrderFromCart(any(CartModel.class))).thenReturn(order);
		SAPS4OMRequestData requestData = new SAPS4OMRequestData();
		when(sapS4OMRequestPayloadCreator.getPayloadForOrderCreation(any(OrderModel.class))).thenReturn(requestData);
		SAPS4OMData response = new SAPS4OMData();
		when(sapS4omOutboundService.createOrder(anyString(), anyString(), any(SAPS4OMRequestData.class)))
				.thenReturn(response);
		when(sapS4OrderManagementConfigService.isATPCheckActive()).thenReturn(true);
		doNothing().when(sapS4OMAvailabilityService).removeProductAvailabilityFromCache(product);
		doNothing().when(cartService).removeSessionCart();
		doNothing().when(sapS4OMResponseProcessor).processOrderCreationResponse(response, order);

		CommerceOrderResult orderResult = s4PlaceOrderStrategy.placeOrder(parameter);

		assertNotNull(orderResult.getOrder());
		

	}

	@Test
	public void testPlaceOrderWhenSyncOrderDisabled() throws InvalidCartException, CalculationException {

		s4PlaceOrderStrategy.setCommercePlaceOrderMethodHooks(null);
		CommerceCheckoutParameter parameter = mock(CommerceCheckoutParameter.class);
		CartModel cart = spy(CartModel.class);

		B2BCustomerModel customerModel = spy(B2BCustomerModel.class);
		cart.setUser(customerModel);
		OrderModel order = spy(OrderModel.class);
		order.setCode("1234");

		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);
		when(parameter.getCart()).thenReturn(cart);
		when(calculationService.requiresCalculation(cart)).thenReturn(false);
		when(orderService.createOrderFromCart(cart)).thenReturn(order);
		when(timeService.getCurrentTime()).thenReturn(null);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(null);
		when(commonI18NService.getCurrentLanguage()).thenReturn(null);
		
		doNothing().when(modelService).saveAll(customerModel,order);
		doNothing().when(modelService).save(order);
		doNothing().when(promotionsService).transferPromotionsToOrder(cart, order, false);
		doNothing().when(calculationService).calculateTotals(order, false);
		when(externalTaxesService.calculateExternalTaxes(order)).thenReturn(false);
		doNothing().when(modelService).refresh(order);
		doNothing().when(modelService).refresh(customerModel);
		doNothing().when(orderService).submitOrder(order);
		doNothing().when(externalTaxesService).clearSessionTaxDocument();
		
		assertNotNull(s4PlaceOrderStrategy.placeOrder(parameter));
		

	}
	
	@Test
	public void testBeforePlaceOrder() throws InvalidCartException {
		CommerceCheckoutParameter commerceCheckoutParameter = mock(CommerceCheckoutParameter.class);
		CommercePlaceOrderMethodHook commercePlaceOrderMethodHook = mock(CommercePlaceOrderMethodHook.class);
		List<CommercePlaceOrderMethodHook> list = new ArrayList<>();
		list.add(commercePlaceOrderMethodHook);
		when(s4PlaceOrderStrategy.getSapS4PlaceOrderMethodHooks()).thenReturn(list);
		s4PlaceOrderStrategy.beforeS4PlaceOrder(commerceCheckoutParameter);
		assertEquals(s4PlaceOrderStrategy.getSapS4PlaceOrderMethodHooks().size(), 1);
	}
	

}