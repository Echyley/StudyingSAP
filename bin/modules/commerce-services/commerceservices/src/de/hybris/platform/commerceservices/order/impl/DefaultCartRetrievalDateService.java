/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.commerceservices.order.CartRetrievalDateService;
import de.hybris.platform.commerceservices.order.hook.CartEarliestRetrievalDateHook;
import de.hybris.platform.commerceservices.order.strategies.CartRetrievalDateStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link CartRetrievalDateService}
 */
public class DefaultCartRetrievalDateService implements CartRetrievalDateService
{

	private static final Logger LOG = Logger.getLogger(DefaultCartRetrievalDateService.class);
	public static final String REQUESTEDRETRIEVALDATEENABLED = "requestedRetrievalDateEnabled";

	private CartService cartService;
	private ModelService modelService;
	private CartRetrievalDateStrategy cartRetrievalDateStrategy;
	private List<CartEarliestRetrievalDateHook> cartEarliestRetrievalDateHooks;

	@Override
	public String getCartEarliestRetrievalDate()
	{
		String earliestRetrievalDate = null;
		final CartModel cart = getSessionCart();
		if (isRequestedRetrievalDateFeatureEnabled(cart.getStore()))
		{
			final List<String> retrievalDates = getRetrievalDates(cart);
			earliestRetrievalDate = getCartRetrievalDateStrategy().getCartEarliestRetrievalDate(retrievalDates);
			cart.setEarliestRetrievalDate(earliestRetrievalDate);
			modelService.save(cart);
		}
		return earliestRetrievalDate;
	}

	@Override
	public void updateCartRequestedRetrievalDate(final String requestedRetrievalDate)
	{
		final CartModel cart = getSessionCart();
		if (isRequestedRetrievalDateFeatureEnabled(cart.getStore()))
		{
			getCartRetrievalDateStrategy().updateRequestedRetrievalDate(cart, requestedRetrievalDate);
		}
	}

	protected List<String> getRetrievalDates(final CartModel cart)
	{

		final List<String> retrievalDates = new ArrayList<>();
		for (final CartEarliestRetrievalDateHook hook : getCartEarliestRetrievalDateHooks())
		{
			final List<String> dates = hook.getEarliestRetrievalDates(cart);
			retrievalDates.addAll(dates);
		}

		return retrievalDates.stream().distinct().toList();
	}

	protected boolean isRequestedRetrievalDateFeatureEnabled(final BaseStoreModel baseStore)
	{
		if (baseStore != null)
		{
			try
			{
				final Boolean featureEnabled = getModelService().getAttributeValue(baseStore,
						BaseStoreModel.REQUESTEDRETRIEVALDATEENABLED);
				return featureEnabled != null && featureEnabled.booleanValue();
			}
			catch (final AttributeNotSupportedException e)
			{
				LOG.debug("Requested retrieval date feature is not enabled, please update the system", e);
			}
		}
		return false;
	}


	protected CartModel getSessionCart()
	{
		return getCartService().getSessionCart();
	}

	public CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public List<CartEarliestRetrievalDateHook> getCartEarliestRetrievalDateHooks()
	{
		return cartEarliestRetrievalDateHooks;
	}

	public void setCartEarliestRetrievalDateHooks(final List<CartEarliestRetrievalDateHook> cartEarliestRetrievalDateHooks)
	{
		this.cartEarliestRetrievalDateHooks = cartEarliestRetrievalDateHooks;
	}

	public CartRetrievalDateStrategy getCartRetrievalDateStrategy()
	{
		return cartRetrievalDateStrategy;
	}

	public void setCartRetrievalDateStrategy(final CartRetrievalDateStrategy cartRetrievalDateStrategy)
	{
		this.cartRetrievalDateStrategy = cartRetrievalDateStrategy;
	}

}
