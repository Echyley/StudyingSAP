/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.validator.CommerceQuoteValidator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteActionValidationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteActionValidationStrategyTest
{
	private static final String TEST_MSG = "test exception";
	private static final String QUOTE_CODE = "q123";
	private DefaultQuoteActionValidationStrategy defaultQuoteActionValidationStrategy;
	private Set<QuoteState> buyerQuoteStateList;
	private QuoteModel quoteModel;
	private UserModel userModel;
	@Mock(lenient = true)
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;
	@Mock
	private CommerceQuoteValidator failingValidator;
	@Mock
	private CommerceQuoteValidator succeedingValidator;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp()
	{
		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_DRAFT);
		defaultQuoteActionValidationStrategy = new DefaultQuoteActionValidationStrategy();
		defaultQuoteActionValidationStrategy.setQuoteStateSelectionStrategy(quoteStateSelectionStrategy);
		quoteModel = new QuoteModel();
		quoteModel.setState(QuoteState.BUYER_DRAFT);
		userModel = new UserModel();

		willThrow(new IllegalQuoteStateException(QuoteAction.CHECKOUT, QUOTE_CODE, QuoteState.BUYER_DRAFT, 1, TEST_MSG))
				.given(failingValidator).validate(any(QuoteAction.class), same(quoteModel), same(userModel));

	}

	@Test
	public void shouldPassValidation()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationForStates()
	{
		// a list of states which doesn't contain the QuoteModel.state.
		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_SUBMITTED);
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.EDIT, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationForEmptyAllowedStates()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(Collections.EMPTY_SET);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullQuote()
	{
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullQuoteAction()
	{
		defaultQuoteActionValidationStrategy.validate(null, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullUser()
	{
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, null);
	}

	@Test
	public void shouldReturnTrueForValidation()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		Assert.assertTrue("Should return true for the action validation",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, userModel));
	}

	@Test
	public void shouldReturnFalseForValidationForStates()
	{
		// a list of states which doesn't contain the QuoteModel.state.
		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_SUBMITTED);
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		Assert.assertFalse("Should return false for the action validation for states",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.EDIT, quoteModel, userModel));
	}

	@Test
	public void shouldReturnFalseForValidationForEmptyAllowedStates()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(Collections.EMPTY_SET);
		Assert.assertFalse("Should return false for the action validation for empty allowed states",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, userModel));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseForValidationForNullQuote()
	{
		defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseForValidationForNullQuoteAction()
	{
		defaultQuoteActionValidationStrategy.isValidAction(null, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseValidationForNullUser()
	{
		defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, null);
	}


	@Test
	public void shouldReturnFalseForFailingValidator()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.setCommerceQuoteValidators(Collections.singletonList(failingValidator));
		Assert.assertFalse("isValidAction should return false for a failing validator",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.EDIT, quoteModel, userModel));
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationForIllegalStatesDespiteSucceedingValidator()
	{
		defaultQuoteActionValidationStrategy.setCommerceQuoteValidators(Collections.singletonList(succeedingValidator));
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test
	public void shouldPassValidationWithSucceedingValidator()
	{
		defaultQuoteActionValidationStrategy.setCommerceQuoteValidators(Collections.singletonList(succeedingValidator));
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test
	public void shouldNotPassValidationWithFailingValidator()
	{
		// make sure exception thrown by validator is propagated and not a generic one
		expectedEx.expect(IllegalQuoteStateException.class);
		expectedEx.expectMessage(TEST_MSG);

		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.setCommerceQuoteValidators(Collections.singletonList(failingValidator));
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationWithOneFailingValidator()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy
				.setCommerceQuoteValidators(List.of(succeedingValidator, failingValidator, succeedingValidator));
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test
	public void shouldAcceptNullValidatorList()
	{
		defaultQuoteActionValidationStrategy.setCommerceQuoteValidators(null);
		assertNotNull(defaultQuoteActionValidationStrategy.getCommerceQuoteValidators());
		assertTrue("expected validator list to be empty",
				defaultQuoteActionValidationStrategy.getCommerceQuoteValidators().isEmpty());
	}
}
