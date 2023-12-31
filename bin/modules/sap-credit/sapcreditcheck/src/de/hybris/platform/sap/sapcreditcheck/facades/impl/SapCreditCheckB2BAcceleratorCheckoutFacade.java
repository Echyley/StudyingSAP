/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcreditcheck.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;


/**
 * B2B specific implementation of the {@link AcceleratorCheckoutFacade} interface extending
 * {@link SapCreditCheckB2BCheckoutFacade}. Delegates {@link AcceleratorCheckoutFacade} methods to the
 * defaultAcceleratorCheckoutFacade bean.
 */
public class SapCreditCheckB2BAcceleratorCheckoutFacade extends SapCreditCheckB2BCheckoutFacade implements AcceleratorCheckoutFacade
{

	private AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	@Override
	public String getCheckoutFlowGroupForCheckout()
	{
		return getAcceleratorCheckoutFacade().getCheckoutFlowGroupForCheckout();
	}

	@Override
	public List<PointOfServiceData> getConsolidatedPickupOptions()
	{
		return getAcceleratorCheckoutFacade().getConsolidatedPickupOptions();
	}

	@Override
	public List<CartModificationData> consolidateCheckoutCart(final String pickupPointOfServiceName)
			throws CommerceCartModificationException
	{
		return getAcceleratorCheckoutFacade().consolidateCheckoutCart(pickupPointOfServiceName);
	}

	@Override
	public boolean isExpressCheckoutAllowedForCart()
	{
		return getAcceleratorCheckoutFacade().isExpressCheckoutAllowedForCart();
	}

	@Override
	public boolean isExpressCheckoutEnabledForStore()
	{
		return getAcceleratorCheckoutFacade().isExpressCheckoutEnabledForStore();
	}

	@Override
	public boolean isTaxEstimationEnabledForCart()
	{
		return getAcceleratorCheckoutFacade().isTaxEstimationEnabledForCart();
	}

	@Override
	public boolean isNewAddressEnabledForCart()
	{
		return !isAccountPayment();
	}

	@Override
	public boolean isRemoveAddressEnabledForCart()
	{
		return !isAccountPayment();
	}

	@Override
	public ExpressCheckoutResult performExpressCheckout()
	{
		return getAcceleratorCheckoutFacade().performExpressCheckout();
	}

	@Override
	public boolean hasValidCart()
	{
		return getAcceleratorCheckoutFacade().hasValidCart();
	}

	@Override
	public boolean hasNoDeliveryAddress()
	{
		final CartData cartData = getCheckoutCart();
		return hasShippingItems() && (cartData == null || cartData.getDeliveryAddress() == null);
	}

	@Override
	public boolean hasNoDeliveryMode()
	{
		return getAcceleratorCheckoutFacade().hasNoDeliveryMode();
	}

	@Override
	public boolean hasNoPaymentInfo()
	{
		return getCart() == null || getCart().getPaymentInfo() == null;
	}

	protected AcceleratorCheckoutFacade getAcceleratorCheckoutFacade()
	{
		return acceleratorCheckoutFacade;
	}

	@Required
	public void setAcceleratorCheckoutFacade(final AcceleratorCheckoutFacade acceleratorCheckoutFacade)
	{
		this.acceleratorCheckoutFacade = acceleratorCheckoutFacade;
	}

	protected boolean isAccountPayment()
	{
		return getCheckoutCart().getPaymentType() != null
				&& CheckoutPaymentType.ACCOUNT.getCode().equals(getCheckoutCart().getPaymentType().getCode());
	}

}
