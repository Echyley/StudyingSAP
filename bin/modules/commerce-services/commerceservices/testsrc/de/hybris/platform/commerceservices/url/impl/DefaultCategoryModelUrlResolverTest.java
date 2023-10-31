/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.url.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;


/**
 * Unit test for {@link DefaultCategoryModelUrlResolver}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCategoryModelUrlResolverTest
{
	private static final String PATTERN = "/c/{category-code}";
	private static final String EXPECTED_URL = "/c/Test%20Category%20Code";
	private static final String TEST_CATEGORY_CODE = "Test Category Code";

	@Mock
	private CategoryModel categoryModel;

	private DefaultCategoryModelUrlResolver categoryModelUrlResolver;

	@Before
	public void setUp() throws Exception
	{
		given(categoryModel.getCode()).willReturn(TEST_CATEGORY_CODE);

		categoryModelUrlResolver = new DefaultCategoryModelUrlResolver();
		categoryModelUrlResolver.setPattern(PATTERN);
	}

	@Test
	public void testResolveInternalCategoryCode()
	{
		final String actualUrl = categoryModelUrlResolver.resolveInternal(categoryModel);
		Assert.assertEquals("Invalid URL", EXPECTED_URL, actualUrl);
	}
}
