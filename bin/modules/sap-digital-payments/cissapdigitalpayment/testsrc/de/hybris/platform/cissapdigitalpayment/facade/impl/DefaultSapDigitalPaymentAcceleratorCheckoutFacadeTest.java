/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Default implementation of {@link DefaultSapDigitalPaymentAcceleratorCheckoutFacadeTest}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapDigitalPaymentAcceleratorCheckoutFacadeTest
{

	@InjectMocks
	private DefaultSapDigitalPaymentAcceleratorCheckoutFacade defaultSapDigitalPaymentAcceleratorCheckoutFacade;

	@Mock
	private CartFacade cartFacade;

	@Mock
	private CartService cartService;

	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Before
	public void setup()
	{
		//Initialization method
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwErrorOnEmptyPaymentInfoAndBillingAddress()
	{
		defaultSapDigitalPaymentAcceleratorCheckoutFacade.createPaymentSubscription(null);
		final CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		paymentInfo.setBillingAddress(null);
		defaultSapDigitalPaymentAcceleratorCheckoutFacade.createPaymentSubscription(paymentInfo);

	}

	@Test
	public void errorOnDifferentCartAndCurrentUser()
	{
		final CartModel cart = new CartModel();
		final UserModel cartUser = new CustomerModel();
		final UserModel currentUser = new CustomerModel();
		cart.setUser(cartUser);
		when(cartFacade.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cart);
		when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn((CustomerModel) currentUser);
		final CCPaymentInfoData ccPaymentInfoData = defaultSapDigitalPaymentAcceleratorCheckoutFacade
				.createPaymentSubscription(getpaymentInfo());
		assertNull(ccPaymentInfoData);

	}


	/**
	 *
	 */
	private CCPaymentInfoData getpaymentInfo()
	{
		final CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		final AddressData billingAddress = getDummyBillingAddress();
		paymentInfo.setSaved(true);
		billingAddress.setTitleCode("mr");
		paymentInfo.setBillingAddress(billingAddress);
		paymentInfo.setExpiryMonth("05");
		paymentInfo.setExpiryYear("2019");
		return paymentInfo;
	}

	/**
	 *
	 */
	private AddressData getDummyBillingAddress()
	{
		final AddressData billingAddress = new AddressData();
		return billingAddress;
	}

}
