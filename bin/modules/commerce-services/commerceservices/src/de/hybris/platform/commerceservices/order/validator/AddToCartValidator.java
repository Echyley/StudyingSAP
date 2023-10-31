/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.validator;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


/**
 * Is used to add validation logic during the addToCart process
 */
public interface AddToCartValidator
{
	/**
	 * States if the validator supports the given parameters. If this is the case, its validation result will be taken
	 * into account
	 *
	 * @param parameter Parameters used for the add to cart process
	 * @return Validator supports
	 */
	boolean supports(final CommerceCartParameter parameter);

	/**
	 * Validates add to cart parameters
	 *
	 * @param parameter Parameters used for the add to cart process
	 * @throws CommerceCartModificationException Validation issue
	 */
	void validate(final CommerceCartParameter parameter) throws CommerceCartModificationException;
}

