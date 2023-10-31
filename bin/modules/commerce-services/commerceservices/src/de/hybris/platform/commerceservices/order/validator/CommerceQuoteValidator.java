/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.validator;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Validator that gets called when a quote is validated by the {@link QuoteActionValidationStrategy}.
 */
public interface CommerceQuoteValidator
{

	/**
	 * Checks whether a user can perform a certain action on a quote.
	 *
	 * @param quoteAction
	 *           the quote action to be performed
	 * @param quoteModel
	 *           the quote on which the action is to be performed
	 * @param userModel
	 *           the user that wants to perform the action
	 * @throws IllegalQuoteStateException
	 *            if the action cannot be performed for a quote
	 */
	void validate(QuoteAction quoteAction, QuoteModel quoteModel, UserModel userModel);

}
