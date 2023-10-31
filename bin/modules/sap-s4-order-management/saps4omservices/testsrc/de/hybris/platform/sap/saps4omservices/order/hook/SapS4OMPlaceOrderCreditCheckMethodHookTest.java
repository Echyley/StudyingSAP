/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.hook;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapS4OMPlaceOrderCreditCheckMethodHookTest {

	@Spy
	@InjectMocks
	SapS4OMPlaceOrderCreditCheckMethodHook sapS4OMPlaceOrderCreditCheckMethodHook;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	UserService userService;
	
	@Mock
	SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	
	@Before
	public void init() {
		sapS4OMPlaceOrderCreditCheckMethodHook.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
		sapS4OMPlaceOrderCreditCheckMethodHook.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
		sapS4OMPlaceOrderCreditCheckMethodHook.setUserService(userService);
		sapS4OMPlaceOrderCreditCheckMethodHook.setSapS4SalesOrderSimulationService(sapS4SalesOrderSimulationService);
	}
	
	@Test
	public void testBeforePlaceOrder() throws InvalidCartException {
		
		CommerceCheckoutParameter commerceCheckoutParameter = mock(CommerceCheckoutParameter.class);
		
		CartModel cartModel = spy(CartModel.class);
		
		BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		when(cartModel.getSite()).thenReturn(baseSiteModel);
		when(cartModel.getSite().getChannel()).thenReturn(SiteChannel.B2B);
		when(commerceCheckoutParameter.getCart()).thenReturn(cartModel);
		when(sapS4OMPlaceOrderCreditCheckMethodHook.getSapS4OrderManagementConfigService().isCreditCheckActive()).thenReturn(true);
		sapS4OMPlaceOrderCreditCheckMethodHook.beforePlaceOrder(commerceCheckoutParameter);
	}

}
