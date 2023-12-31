/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationSavedCartIntegrationFacade}
 */
public class ConfigurationSavedCartIntegrationFacadeImpl implements ConfigurationSavedCartIntegrationFacade
{
	private ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper;
	private CommerceCartService commerceCartService;
	private UserService userService;


	@Override
	public ConfigurationOverviewData getConfiguration(final String code, final int entryNumber) throws CommerceSaveCartException
	{
		final CartModel savedCart = findSavedCart(code);
		return getConfigurationAbstractOrderIntegrationHelper().retrieveConfigurationOverviewData(savedCart, entryNumber);
	}

	protected CartModel findSavedCart(final String code) throws CommerceSaveCartException
	{
		final CartModel cartForCodeAndUser = getCommerceCartService()
				.getCartForCodeAndUser(code, getUserService().getCurrentUser());

		if (cartForCodeAndUser == null)
		{
			throw new CommerceSaveCartException("Cannot find a cart for code [" + code + "]");
		}

		return cartForCodeAndUser;
	}

	protected ConfigurationAbstractOrderIntegrationHelper getConfigurationAbstractOrderIntegrationHelper()
	{
		return configurationAbstractOrderIntegrationHelper;
	}

	/**
	 * @param configurationAbstractOrderIntegrationHelper
	 */
	@Required
	public void setConfigurationAbstractOrderIntegrationHelper(
			final ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper)
	{
		this.configurationAbstractOrderIntegrationHelper = configurationAbstractOrderIntegrationHelper;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}
}
