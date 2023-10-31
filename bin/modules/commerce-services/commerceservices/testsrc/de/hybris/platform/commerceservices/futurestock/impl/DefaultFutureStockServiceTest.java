/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.futurestock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.futurestock.dao.FutureStockDao;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;


/**
 * JUnit test suite for {@link DefaultFutureStockService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFutureStockServiceTest
{
	@InjectMocks
	private DefaultFutureStockService futureStockService;

	@Mock
	private FutureStockDao futureStockDao;

	@Test
	public void testGetFutureAvailabilityEmpty()
	{
		final List<ProductModel> productModels = new ArrayList<>();
		final Map<String, Map<Date, Integer>> productsMap = futureStockService.getFutureAvailability(productModels);
		Assert.assertEquals(0, productsMap.size());
	}

	@Test
	public void testGetFutureAvailability()
	{
		final String productCode = "000001";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);

		final FutureStockModel futureStockModel = new FutureStockModel();
		final Date stockDate = new Date();
		futureStockModel.setDate(stockDate);
		futureStockModel.setQuantity(4);

		final List<ProductModel> productModels = new ArrayList<>();
		productModels.add(productModel);
		final List<FutureStockModel> futureStocks = new ArrayList<>();
		futureStocks.add(futureStockModel);

		given(futureStockDao.getFutureStocksByProductCode(productCode)).willReturn(futureStocks);

		final Map<String, Map<Date, Integer>> productsMap = futureStockService.getFutureAvailability(productModels);
		Assert.assertEquals(1, productsMap.size());

		final Map<Date, Integer> futureStockMap = productsMap.get(productCode);
		Assert.assertEquals(1, futureStockMap.size());
		for (final Map.Entry<Date, Integer> entry : futureStockMap.entrySet())
		{
			Assert.assertEquals(4, entry.getValue().intValue());
		}
	}
}
