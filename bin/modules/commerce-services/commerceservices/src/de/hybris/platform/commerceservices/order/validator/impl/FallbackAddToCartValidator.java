/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.validator.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Fallback implementation of {@link AddToCartValidator}. If no other validators are responsible for a product that is
 * to be added to the cart, the validation logic of this class will be active
 */
public class FallbackAddToCartValidator implements AddToCartValidator
{
	@Override
	public boolean supports(final CommerceCartParameter parameter)
	{
		// actual return value does not matter, as this is a fallback implementation that is
		// active if other validators don't support
		return true;
	}

	@Override
	public void validate(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		validateParameterNotNull(parameter, "Parameter must be specified");
		final ProductModel product = parameter.getProduct();
		validateParameterNotNull(product, "Product must be specified in parameter");

		if (product.getVariantType() != null)
		{
			throw new CommerceCartModificationException("Choose a variant instead of the base product");
		}
	}
}
