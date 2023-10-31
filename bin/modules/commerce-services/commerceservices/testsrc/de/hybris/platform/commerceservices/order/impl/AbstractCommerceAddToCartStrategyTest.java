/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link AbstractCommerceAddToCartStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractCommerceAddToCartStrategyTest
{
	private final AbstractCommerceAddToCartStrategy classUnderTest = new AbstractCommerceAddToCartStrategy()
	{
		@Override
		public List<CommerceCartModification> addToCart(final List<CommerceCartParameter> parameterList)
				throws CommerceCartMergingException
		{
			return null;
		}

		@Override
		public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
		{
			return null;
		}
	};
	private static final String VALIDATION_ERROR_MESSAGE = "Validation error";
	private static final String VALIDATION_ERROR_MESSAGE_FROM_FALLBACK = "Validation issue from fallback";
	private final CommerceCartParameter parameters = new CommerceCartParameter();
	private List<AddToCartValidator> addToCartValidators;
	@Mock
	private AddToCartValidator fallbackAddToCartValidator;
	@Mock
	private AddToCartValidator addToCartValidatorApplicableNotAccepting;
	@Mock
	private AddToCartValidator addToCartValidatorApplicableAccepting;
	@Mock
	private AddToCartValidator addToCartValidatorNotApplicable;
	@Mock
	private CartModel cart;
	@Mock
	private ProductModel product;

	@Before
	public void initialize() throws CommerceCartModificationException
	{
		parameters.setCart(cart);
		parameters.setProduct(product);
		parameters.setQuantity(1);

		addToCartValidators = new ArrayList<>();

		when(addToCartValidatorApplicableNotAccepting.supports(parameters)).thenReturn(true);
		when(addToCartValidatorApplicableAccepting.supports(parameters)).thenReturn(true);
		when(addToCartValidatorNotApplicable.supports(parameters)).thenReturn(false);
		doThrow(new CommerceCartModificationException(VALIDATION_ERROR_MESSAGE)).when(addToCartValidatorApplicableNotAccepting)
				.validate(parameters);

		classUnderTest.setFallbackAddToCartValidator(fallbackAddToCartValidator);
	}

	@Test
	public void testAddToCartValidators()
	{
		classUnderTest.setAddToCartValidators(addToCartValidators);
		assertEquals(classUnderTest.getAddToCartValidators(), addToCartValidators);
	}

	@Test
	public void testAddToCartValidatorsSetNull()
	{
		classUnderTest.setAddToCartValidators(null);
		assertTrue(Collections.isEmpty(classUnderTest.getAddToCartValidators()));
	}

	@Test
	public void testFallbackAddToCartValidator()
	{
		assertEquals(classUnderTest.getFallbackAddToCartValidator(), fallbackAddToCartValidator);
	}

	@Test
	public void testValidateMultipleApplicableValidatorsExistOneNotAccepting() throws CommerceCartModificationException
	{
		addToCartValidators.add(addToCartValidatorApplicableNotAccepting);
		addToCartValidators.add(addToCartValidatorApplicableAccepting);
		addToCartValidators.add(addToCartValidatorNotApplicable);
		classUnderTest.setAddToCartValidators(addToCartValidators);

		assertThatThrownBy(() -> classUnderTest.validateAddToCart(parameters)).isNotNull()
				.isInstanceOf(CommerceCartModificationException.class).hasMessageContaining(VALIDATION_ERROR_MESSAGE);
		verify(addToCartValidatorApplicableNotAccepting).supports(parameters);
		verify(addToCartValidatorApplicableAccepting).supports(parameters);
		verify(addToCartValidatorNotApplicable).supports(parameters);
	}

	@Test
	public void testValidateMultipleValidatorsExist() throws CommerceCartModificationException
	{
		addToCartValidators.add(addToCartValidatorApplicableAccepting);
		addToCartValidators.add(addToCartValidatorNotApplicable);
		classUnderTest.setAddToCartValidators(addToCartValidators);

		classUnderTest.validateAddToCart(parameters);

		verify(addToCartValidatorApplicableAccepting).supports(parameters);
		verify(addToCartValidatorNotApplicable).supports(parameters);

		verify(addToCartValidatorApplicableAccepting).validate(parameters);
		verify(addToCartValidatorNotApplicable, never()).validate(parameters);
	}

	@Test
	public void testValidateApplicableValidatorExistsAccepting() throws CommerceCartModificationException
	{
		addToCartValidators.add(addToCartValidatorApplicableAccepting);
		classUnderTest.setAddToCartValidators(addToCartValidators);
		classUnderTest.validateAddToCart(parameters);
		verify(addToCartValidatorApplicableAccepting).validate(parameters);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidateApplicableValidatorExistsAcceptingQtyWrong() throws CommerceCartModificationException
	{
		parameters.setQuantity(0);
		addToCartValidators.add(addToCartValidatorApplicableAccepting);
		classUnderTest.setAddToCartValidators(addToCartValidators);
		classUnderTest.validateAddToCart(parameters);
	}

	@Test
	public void testValidateFallbackIfNoApplicableValidatorExists() throws CommerceCartModificationException
	{
		addToCartValidators.add(addToCartValidatorNotApplicable);
		classUnderTest.setAddToCartValidators(addToCartValidators);
		classUnderTest.validateAddToCart(parameters);
		verify(fallbackAddToCartValidator).validate(parameters);
	}

	@Test
	public void testValidateFallbackIfValidatorsEmpty() throws CommerceCartModificationException
	{
		classUnderTest.setAddToCartValidators(addToCartValidators);
		classUnderTest.validateAddToCart(parameters);
		verify(fallbackAddToCartValidator).validate(parameters);
	}

	@Test
	public void testValidateFallbackIfValidatorsNull() throws CommerceCartModificationException
	{
		classUnderTest.setAddToCartValidators(null);
		classUnderTest.validateAddToCart(parameters);
		verify(fallbackAddToCartValidator).validate(parameters);
	}

	@Test
	public void testValidateFallbackForVariantBase() throws CommerceCartModificationException
	{
		doThrow(new CommerceCartModificationException(VALIDATION_ERROR_MESSAGE_FROM_FALLBACK)).when(fallbackAddToCartValidator)
				.validate(parameters);
		addToCartValidators.add(addToCartValidatorNotApplicable);
		classUnderTest.setAddToCartValidators(addToCartValidators);
		assertThatThrownBy(() -> classUnderTest.validateAddToCart(parameters)).isNotNull()
				.isInstanceOf(CommerceCartModificationException.class).hasMessageContaining(VALIDATION_ERROR_MESSAGE_FROM_FALLBACK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateParametersNull() throws CommerceCartModificationException
	{
		classUnderTest.validateAddToCart(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateParametersDoNotCarryCart() throws CommerceCartModificationException
	{
		parameters.setCart(null);
		classUnderTest.validateAddToCart(parameters);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateParametersDoNotCarryProduct() throws CommerceCartModificationException
	{
		parameters.setProduct(null);
		classUnderTest.validateAddToCart(parameters);
	}
}
