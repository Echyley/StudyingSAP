/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link ProductSummaryPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductSummaryPopulatorTest
{
	private static final String PRODUCT_SUMMARY = "Some product summary...";

	@Mock
	private ModelService modelService;

	private ProductSummaryPopulator productSummaryPopulator;

	@Before
	public void setUp()
	{
		productSummaryPopulator = new ProductSummaryPopulator();
		productSummaryPopulator.setModelService(modelService);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);

		given(modelService.getAttributeValue(source, ProductModel.SUMMARY)).willReturn(PRODUCT_SUMMARY);

		final ProductData result = new ProductData();
		productSummaryPopulator.populate(source, result);

		Assert.assertEquals(PRODUCT_SUMMARY, result.getSummary());
	}
}
