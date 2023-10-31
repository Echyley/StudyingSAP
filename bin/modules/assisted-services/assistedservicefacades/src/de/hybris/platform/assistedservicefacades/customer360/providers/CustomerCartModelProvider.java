/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Map;

public class CustomerCartModelProvider implements FragmentModelProvider<CartData>
{
	private CartFacade cartFacade;

	@Override
	public CartData getModel(final Map<String, String> parameters)
	{
		final CartData cartData = cartFacade.getSessionCart();
		final int limit = Config.getInt(AssistedservicefacadesConstants.AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED,
				AssistedservicefacadesConstants.AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED_DEFAULT);
		if (cartData.getEntries() != null && cartData.getEntries().size() > limit)
		{
			final List<OrderEntryData> entries = cartData.getEntries();
			entries.subList(limit, entries.size()).clear();
		}
		return cartData;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
