/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.subscription.impl;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * DefaultSubscriptionCommerceCloneCartHook.
 * @deprecated since 2105
 */
@Deprecated(since = "2105", forRemoval= true )
public class DefaultSubscriptionCommerceCloneCartHook implements CommerceCloneSavedCartMethodHook
{
	private CartService cartService;
	private ModelService modelService;

	@Override
	public void beforeCloneSavedCart(@Nonnull final CommerceSaveCartParameter parameter) throws CommerceSaveCartException
	{
		return; //NOPMD
	}

	@Override
	public void afterCloneSavedCart(@Nonnull final CommerceSaveCartParameter parameter,@Nonnull final CommerceSaveCartResult saveCartResult)
			throws CommerceSaveCartException
	{
		return; //NOPMD
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
