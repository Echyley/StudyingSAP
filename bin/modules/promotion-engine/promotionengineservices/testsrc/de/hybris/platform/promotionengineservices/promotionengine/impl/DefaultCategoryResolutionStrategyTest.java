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
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Arrays;
import java.util.Collection;
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
public class DefaultCategoryResolutionStrategyTest
{


	private static final String CATEGORY_CODE = "234";
	private static final String CATEGORY_NAME = "The Category";
	private static final String CLASSIFICATION_NAME = "The Classification";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private DefaultCategoryResolutionStrategy strategy;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleParameterData data;

	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryModel category1;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CategoryModel category2;

	@Mock
	private ClassificationSystemVersionModel classificationSystemVersion;


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
	public void testCategoryResolutionWithFiltering()
	{
		//given
		final Collection<CategoryModel> categories = Arrays.asList(category1, category2);
		Mockito.lenient().when(data.getValue()).thenReturn(CATEGORY_CODE);
		Mockito.lenient().when(category1.getName()).thenReturn(CATEGORY_NAME);
		Mockito.lenient().when(category1.getCatalogVersion()).thenReturn(catalogVersion);
		Mockito.lenient().when(category2.getName()).thenReturn(CLASSIFICATION_NAME);
		Mockito.lenient().when(category2.getCatalogVersion()).thenReturn(classificationSystemVersion);
		Mockito.lenient().when(categoryService.getCategoriesForCode(Mockito.anyString())).thenReturn(categories);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		//then
		Assert.assertEquals(CATEGORY_NAME, result);

	}

}
