/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.validator.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;


/**
 * Units tests for {@link FallbackAddToCartValidator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FallbackAddToCartValidatorTest
{
	private final FallbackAddToCartValidator classUnderTest = new FallbackAddToCartValidator();
	private final CommerceCartParameter parameter = new CommerceCartParameter();
	@Mock
	private ProductModel product;
	@Mock
	private VariantTypeModel variantType;

	@Test
	public void testSupports()
	{
		assertTrue(classUnderTest.supports(parameter));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateNull() throws CommerceCartModificationException
	{
		classUnderTest.validate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateNoProduct() throws CommerceCartModificationException
	{
		classUnderTest.validate(parameter);
	}

	@Test
	public void testValidateProductValid() throws CommerceCartModificationException
	{
		parameter.setProduct(product);
		classUnderTest.validate(parameter);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidateProductVariantBaseProduct() throws CommerceCartModificationException
	{
		parameter.setProduct(product);
		Mockito.when(product.getVariantType()).thenReturn(variantType);
		classUnderTest.validate(parameter);
	}
}
