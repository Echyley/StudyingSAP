/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Abstract strategy for adding items to the cart
 */
public abstract class AbstractCommerceAddToCartStrategy extends AbstractCommerceCartStrategy implements CommerceAddToCartStrategy
{
	protected static final int APPEND_AS_LAST = -1;
	private List<CommerceAddToCartMethodHook> commerceAddToCartMethodHooks;
	private ConfigurationService configurationService;
	private List<AddToCartValidator> addToCartValidators = Collections.emptyList();
	private AddToCartValidator fallbackAddToCartValidator;

	protected void validateAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		validateParameterNotNull(parameters, "Parameters cannot be null");

		final CartModel cartModel = parameters.getCart();
		final ProductModel productModel = parameters.getProduct();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(productModel, "Product model cannot be null");

		if (parameters.getQuantity() < 1)
		{
			throw new CommerceCartModificationException("Quantity must not be less than one");
		}

		final List<AddToCartValidator> applicableValidators = getAddToCartValidators().stream()
				.filter(validator -> validator.supports(parameters)).collect(Collectors.toList());

		if (!applicableValidators.isEmpty())
		{
			//no functional notation here because of checked exceptions that we need to propagate
			//to the caller
			for (final AddToCartValidator validator : applicableValidators)
			{
				validator.validate(parameters);
			}
		}
		else
		{
			getFallbackAddToCartValidator().validate(parameters);
		}

	}

	protected void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		if (getCommerceAddToCartMethodHooks() != null && (parameters.isEnableHooks() && getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ADDTOCARTHOOK_ENABLED)))
		{
			for (final CommerceAddToCartMethodHook commerceAddToCartMethodHook : getCommerceAddToCartMethodHooks())
			{
				commerceAddToCartMethodHook.beforeAddToCart(parameters);
			}
		}
	}

	protected void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		if (getCommerceAddToCartMethodHooks() != null && (parameters.isEnableHooks() && getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ADDTOCARTHOOK_ENABLED)))
		{
			for (final CommerceAddToCartMethodHook commerceAddToCartMethodHook : getCommerceAddToCartMethodHooks())
			{
				commerceAddToCartMethodHook.afterAddToCart(parameters, result);
			}
		}
	}

	protected List<CommerceAddToCartMethodHook> getCommerceAddToCartMethodHooks()
	{
		return commerceAddToCartMethodHooks;
	}

	/**
	 * Optional setter for hooking into before and after execution of
	 * {@link #addToCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *
	 * @param commerceAddToCartMethodHooks
	 */
	public void setCommerceAddToCartMethodHooks(final List<CommerceAddToCartMethodHook> commerceAddToCartMethodHooks)
	{
		this.commerceAddToCartMethodHooks = commerceAddToCartMethodHooks;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @param addToCartValidators List of product validators
	 */
	public void setAddToCartValidators(final List<AddToCartValidator> addToCartValidators)
	{
		this.addToCartValidators = addToCartValidators == null ? Collections.emptyList() : List.copyOf(addToCartValidators);
	}

	protected List<AddToCartValidator> getAddToCartValidators()
	{
		return Collections.unmodifiableList(addToCartValidators);
	}

	/**
	 * @param fallbackAddToCartValidator Fallback product validator that will be active if no other validators are configured
	 */
	@Required
	public void setFallbackAddToCartValidator(final AddToCartValidator fallbackAddToCartValidator)
	{
		this.fallbackAddToCartValidator = fallbackAddToCartValidator;

	}

	protected AddToCartValidator getFallbackAddToCartValidator()
	{
		return fallbackAddToCartValidator;
	}
}
