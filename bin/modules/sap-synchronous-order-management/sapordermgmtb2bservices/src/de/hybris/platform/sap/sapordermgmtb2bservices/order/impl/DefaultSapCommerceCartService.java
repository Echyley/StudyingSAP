/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bservices.order.impl;


import de.hybris.platform.commerceservices.order.CommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;


/**
 * Default Sap Commerce Cart Service
 */
public class DefaultSapCommerceCartService extends DefaultCommerceCartService
{
	private CommerceCartRestorationStrategy commerceCartRestorationStrategy;

	/**
	 * @return the commerceCartRestorationStrategy
	 */
	@Override
	public CommerceCartRestorationStrategy getCommerceCartRestorationStrategy()
	{
		return commerceCartRestorationStrategy;
	}

	/**
	 * @param commerceCartRestorationStrategy
	 *           the commerceCartRestorationStrategy to set
	 */
	@Override
	public void setCommerceCartRestorationStrategy(final CommerceCartRestorationStrategy commerceCartRestorationStrategy)
	{
		this.commerceCartRestorationStrategy = commerceCartRestorationStrategy;
	}
}

