/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.url.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.helper.ProductAndCategoryHelper;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductModelUrlResolverTest
{
	@Mock
	private CommerceCategoryService commerceCategoryService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ProductAndCategoryHelper productAndCategoryHelper;

	@InjectMocks
	private DefaultProductModelUrlResolver urlResolver;

	@Test
	public void testProductCodeIsEncodedCorrectly()
	{
		urlResolver.setDefaultPattern("/{product-code}/");
		final ProductModel productModel = new ProductModel();
		productModel.setCode("ABC +DEF+&");

		final String url = urlResolver.resolveInternal(productModel);

		assertThat(url).isEqualTo("/ABC%20%2BDEF%2B%26/");
	}
}
