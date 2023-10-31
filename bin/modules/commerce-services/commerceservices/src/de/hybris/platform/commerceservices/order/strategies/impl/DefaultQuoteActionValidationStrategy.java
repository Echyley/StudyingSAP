/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.validator.CommerceQuoteValidator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteActionValidationStrategy}.
 */
public class DefaultQuoteActionValidationStrategy implements QuoteActionValidationStrategy
{
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;
	private List<CommerceQuoteValidator> commerceQuoteValidators = Collections.emptyList(); // so validators are NOT @Required

	@Override
	public void validate(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		checkParameters(quoteAction, quoteModel, userModel);
		if (!isValidStateTransition(quoteAction, quoteModel, userModel))
		{
			throw new IllegalQuoteStateException(quoteAction, quoteModel.getCode(), quoteModel.getState(), quoteModel.getVersion());
		}
		getCommerceQuoteValidators().forEach(validator -> validator.validate(quoteAction, quoteModel, userModel));
	}

	@Override
	public boolean isValidAction(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		checkParameters(quoteAction, quoteModel, userModel);
		return isValidStateTransition(quoteAction, quoteModel, userModel) && checkValidators(quoteAction, quoteModel, userModel);
	}

	protected void checkParameters(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("Quote action", quoteAction);
		validateParameterNotNullStandardMessage("Quote", quoteModel);
		validateParameterNotNullStandardMessage("User", userModel);
	}

	protected boolean isValidStateTransition(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		final Set<QuoteState> states = getQuoteStateSelectionStrategy().getAllowedStatesForAction(quoteAction, userModel);
		return CollectionUtils.isNotEmpty(states) && states.contains(quoteModel.getState());
	}

	protected boolean checkValidators(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		try
		{
			getCommerceQuoteValidators().forEach(validator -> validator.validate(quoteAction, quoteModel, userModel));
			return true;
		}
		catch (final IllegalQuoteStateException ex)
		{
			return false;
		}
	}

	protected QuoteStateSelectionStrategy getQuoteStateSelectionStrategy()
	{
		return quoteStateSelectionStrategy;
	}

	@Required
	public void setQuoteStateSelectionStrategy(final QuoteStateSelectionStrategy quoteStateSelectionStrategy)
	{
		this.quoteStateSelectionStrategy = quoteStateSelectionStrategy;
	}

	protected List<CommerceQuoteValidator> getCommerceQuoteValidators()
	{
		return Collections.unmodifiableList(commerceQuoteValidators);
	}

	public void setCommerceQuoteValidators(final List<CommerceQuoteValidator> commerceQuoteValidators)
	{
		if (commerceQuoteValidators != null)
		{
			this.commerceQuoteValidators = List.copyOf(commerceQuoteValidators);
		}
		else
		{
			this.commerceQuoteValidators = Collections.emptyList();
		}
	}
}
