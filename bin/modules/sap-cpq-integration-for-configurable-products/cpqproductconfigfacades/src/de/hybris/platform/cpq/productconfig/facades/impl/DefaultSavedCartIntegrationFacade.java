/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cpq.productconfig.facades.SavedCartIntegrationFacade;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * Default implementation of {@link SavedCartIntegrationFacade}
 */
public class DefaultSavedCartIntegrationFacade implements SavedCartIntegrationFacade
{
	private final AbstractOrderIntegrationService abstractOrderIntegrationService;
	private final CommerceCartService commerceCartService;
	private final UserService userService;

	/**
	 * Default constructor for dependency injection.
	 *
	 * @param abstractOrderIntegrationService
	 *           for retrieving the configuration id from the abstract order entry
	 * @param commerceCartService
	 *           for reading the saved carts
	 * @param userService
	 *           for retrieving of current user, which is required to read the saved cart
	 */
	public DefaultSavedCartIntegrationFacade(final AbstractOrderIntegrationService abstractOrderIntegrationService,
			final CommerceCartService commerceCartService, final UserService userService)
	{
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.commerceCartService = commerceCartService;
		this.userService = userService;
	}


	@Override
	public String getConfigurationId(final String cartCode, final int entryNumber) throws CommerceSaveCartException
	{
		final AbstractOrderEntryModel savedCartEntry = findSavedCartEntry(cartCode, entryNumber);
		return getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(savedCartEntry);
	}


	protected AbstractOrderEntryModel findSavedCartEntry(final String cartCode, final int entryNumber)
			throws CommerceSaveCartException
	{
		final CartModel savedCart = findSavedCart(cartCode);
		return savedCart.getEntries().stream()
				.filter(entry -> entryNumber == entry.getEntryNumber().intValue()).findFirst().orElseThrow(
						() -> new CommerceSaveCartException("Saved cart entry" + entryNumber + " not found for cart " + cartCode));
	}

	@Override
	public String getProductCode(final String cartCode, final int entryNumber) throws CommerceSaveCartException
	{
		return findSavedCartEntry(cartCode, entryNumber).getProduct().getCode();
	}

	protected CartModel findSavedCart(final String code) throws CommerceSaveCartException
	{
		final CartModel cartForCodeAndUser = getCommerceCartService().getCartForCodeAndUser(code,
				getUserService().getCurrentUser());

		if (cartForCodeAndUser == null)
		{
			throw new CommerceSaveCartException("Cannot find a cart for code [" + code + "]");
		}

		return cartForCodeAndUser;
	}

	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return abstractOrderIntegrationService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

}
