/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.futurestock.impl;

import de.hybris.platform.commerceservices.futurestock.FutureStockService;
import de.hybris.platform.commerceservices.futurestock.dao.FutureStockDao;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation for {@link FutureStockService}. Gets future availabilities for products.
 */
public class DefaultFutureStockService implements FutureStockService
{

	private FutureStockDao futureStockDao;

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();

		for (final ProductModel product : products)
		{
			final List<FutureStockModel> futureStocks = futureStockDao.getFutureStocksByProductCode(product.getCode());
			if (!CollectionUtils.isEmpty(futureStocks))
			{
				final HashMap<Date, Integer> futureAvailability = new HashMap<>();
				for (final FutureStockModel futureStock : futureStocks)
				{
					futureAvailability.put(futureStock.getDate(), futureStock.getQuantity());
				}
				productsMap.put(product.getCode(), futureAvailability);
			}
		}

		return productsMap;
	}

	public FutureStockDao getFutureStockDao()
	{
		return futureStockDao;
	}

	public void setFutureStockDao(final FutureStockDao futureStockDao)
	{
		this.futureStockDao = futureStockDao;
	}

}
