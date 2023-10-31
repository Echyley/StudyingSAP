/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantProductSourceTest
{
	private VariantProductSource variantProductSource;

	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;
	@Mock
	private VariantProductModel variant1;
	@Mock
	private VariantProductModel variant2;
	@Mock
	private VariantProductModel variant3;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	protected void configure()
	{
		variantProductSource = new VariantProductSource();

		given(model.getBaseProduct()).willReturn(baseProduct);
		given(baseProduct.getVariants()).willReturn(Sets.newHashSet(variant3));
		given(model.getVariants()).willReturn(Sets.newHashSet(variant1, variant2));
	}

	@Test
	public void mustGetAllTheProductsInAMultivatirantProduct()
	{

		final Collection<ProductModel> expectedModels = Sets.newHashSet(baseProduct, model, variant1, variant2, variant3);

		final Collection<ProductModel> foundModels = variantProductSource.getProducts(model);

		assertEquals(expectedModels, foundModels);
	}
}
