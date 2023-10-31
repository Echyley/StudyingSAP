/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PlacePurchaseOrderProcessingTest
{
	@Mock
	private CheckoutFacade checkoutFacade;

	@Mock
	private CartData cartData;

	@Mock
	private OrderData orderData;

	@Mock
	private CartModel cartModel;

	@Mock
	private CartService cartService;

	@Mock
	private PunchOutSessionService punchOutSessionService;

	@Mock
	private CommerceCartService commerceCartService;

	@InjectMocks
	private PlacePurchaseOrderProcessing placePurchaseOrderProcessing;

	@Before
	public void setUp() throws InvalidCartException, PunchOutException
	{
		cartData = new CartData();
		orderData = new OrderData();
		when(checkoutFacade.getCheckoutCart()).thenReturn(cartData);
	}

	@Test
	public void testExceptionMessage() throws InvalidCartException
	{
		when(checkoutFacade.placeOrder()).thenThrow(new InvalidCartException("Testing Failure Condition4"));
		when(checkoutFacade.getCheckoutCart()).thenReturn(cartData);

		assertThatThrownBy(() -> placePurchaseOrderProcessing.process())
			.isInstanceOf(PunchOutException.class);
	}

	@Test
	public void shouldProcessFullyValidateFalse() throws InvalidCartException
	{
		when(checkoutFacade.placeOrder()).thenReturn(orderData);
		when(punchOutSessionService.isPunchOutSessionCartValid()).thenReturn(Boolean.FALSE);

		OrderData res = placePurchaseOrderProcessing.process();

		verify(checkoutFacade).placeOrder();
		assertThat(res).isNotNull();
	}

	@Test
	public void shouldProcessFullyValidateTrue() throws InvalidCartException, CommerceCartRestorationException
	{
		when(checkoutFacade.placeOrder()).thenReturn(orderData);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(punchOutSessionService.isPunchOutSessionCartValid()).thenReturn(Boolean.TRUE);

		OrderData res = placePurchaseOrderProcessing.process();

		verify(commerceCartService).restoreCart(any(CommerceCartParameter.class));
		verify(checkoutFacade).placeOrder();

		assertThat(res).isNotNull();
	}
}
