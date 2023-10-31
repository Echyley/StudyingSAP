/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultProductResolutionStrategyTest
{


	private static final String PRODUCT_CODE = "123";
	private static final String PRODUCT_NAME = "The Product";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private DefaultProductResolutionStrategy strategy;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleParameterData data;

	@Mock
	private ProductService productService;

	@Mock
	private ProductModel product;


	@Test
	public void testNullDataValue()
	{
		// given
		Mockito.lenient().when(data.getValue()).thenReturn(null);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		// then
		Assert.assertNull(result);
	}

	@Test
	public void testWrongDataValueType()
	{
		// given
		Mockito.lenient().when(data.getValue()).thenReturn(Integer.valueOf(2));

		//expect
		expectedException.expect(ClassCastException.class);

		//when
		strategy.getValue(data, promotionResult, Locale.US);

	}

	@Test
	public void testProductResolution()
	{
		//given
		Mockito.lenient().when(data.getValue()).thenReturn(PRODUCT_CODE);
		Mockito.lenient().when(product.getName()).thenReturn(PRODUCT_NAME);
		Mockito.lenient().when(productService.getProductForCode(Mockito.anyString())).thenReturn(product);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		//then
		Assert.assertEquals(PRODUCT_NAME, result);

	}
}
