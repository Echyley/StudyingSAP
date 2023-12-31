/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * B2B specific implementation of the {@link AcceleratorCheckoutFacade} interface extending
 * {@link SapOrdermgmtB2BCheckoutFacade}. Delegates {@link AcceleratorCheckoutFacade} methods to the
 * defaultAcceleratorCheckoutFacade bean.
 */
public class SapOrdermgmtB2BAcceleratorCheckoutFacade extends SapOrdermgmtB2BCheckoutFacade implements AcceleratorCheckoutFacade
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
				&& CheckoutPaymentType.ACCOUNT.getCode().equalsIgnoreCase(getCheckoutCart().getPaymentType().getCode());
	}


	@Override
	public boolean hasNoDeliveryMode()
	{
		final CartData cartData = super.getCheckoutCart();
		return hasShippingItems() && (cartData == null || cartData.getDeliveryMode() == null);
	}

	@Override
	public boolean hasNoPaymentInfo()
	{

		if (isSyncOrdermgmtEnabled())
		{
			return !isAccountPayment();
		}

		return getCart() == null || getCart().getPaymentInfo() == null;

	}



}
